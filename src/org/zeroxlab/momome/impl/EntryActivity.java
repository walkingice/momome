/*
 * Authored By Julian Chu <walkingice@0xlab.org>
 *
 * Copyright (c) 2012 0xlab.org - http://0xlab.org/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zeroxlab.momome.impl;

import org.zeroxlab.momome.R;
import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.MomoApp;
import org.zeroxlab.momome.MomoModel;
import org.zeroxlab.momome.data.Item;
import org.zeroxlab.momome.data.Item.ItemEntry;
import org.zeroxlab.momome.widget.BasicInputDialog;
import org.zeroxlab.momome.widget.EditableActivity;
import org.zeroxlab.momome.widget.EditableAdapter;
import org.zeroxlab.momome.widget.EntryAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class EntryActivity extends EditableActivity implements Momo {
    protected MomoModel mModel;
    protected ListView  mContainer;
    protected TextView  mTitle;
    protected View      mAddButton;
    protected View      mEditButton;
    protected Item      mItem;
    protected LayoutInflater mInflater;
    protected EntryAdapter mAdapter;
    protected EntryClickListener mEntryClickListener;
    protected DialogListener     mDialogListener;

    private final int DIALOG_DATA    = 0x1000;
    private final int DIALOG_COMMENT = 0x1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mModel = MomoApp.getModel();

        mEntryClickListener = new EntryClickListener();
        mDialogListener     = new DialogListener();
        String key = getIntent().getStringExtra(CROSS_ITEM_KEY);
        mItem  = mModel.getItem(key);

        if (initViews(key)) {
            mTitle.setText(mItem.getTitle());
            mAdapter = new EntryAdapter(this, mItem);
            mAdapter.setListener(new EditListener());
            mContainer.setAdapter(mAdapter);
            mContainer.setOnItemClickListener(mEntryClickListener);
        }
    }

    private boolean initViews(String key) {
        if (key == null || key.equals("") || mModel.status() != DataStatus.OK) {
            String msg = "Not valid key";
            Log.e(TAG, msg);
            Toast t = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            t.show();
            finish();
            return false;
        }

        mTitle     = (TextView) findViewById(R.id.entry_title);
        mContainer = (ListView) findViewById(R.id.entry_container);
        mAddButton = findViewById(R.id.entry_add_button);
        mEditButton = findViewById(R.id.entry_edit_button);
        return true;
    }

    public void onClickEditButton(View v) {
        super.onPerformEdit(v);
    }

    public void onClickAddButton(View v) {
        mItem.addEntry(Item.DEF_NAME, "");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStartEdit() {
        mAdapter.setEditing(true);
        mAddButton.setVisibility(View.VISIBLE);
        mContainer.setOnItemClickListener(null);
        mContainer.invalidateViews();
    }

    @Override
    protected void onStopEdit() {
        mAdapter.setEditing(false);
        mAddButton.setVisibility(View.GONE);
        mContainer.setOnItemClickListener(mEntryClickListener);
        mContainer.invalidateViews();
        finishEditing();
    }

    private void finishEditing() {
        mAdapter.notifyDataSetChanged();
        mModel.save();
    }

    private void askData(final ItemEntry entry) {
        BasicInputDialog dialog = new BasicInputDialog(this, "Edit data");
        dialog.setListener(DIALOG_DATA, mDialogListener);
        dialog.setExtra(entry);
        dialog.setDefaultText(entry.getData());
        dialog.show();
    }

    private void askComment(final ItemEntry entry) {
        BasicInputDialog dialog = new BasicInputDialog(this, "Edit comment");
        dialog.setListener(DIALOG_COMMENT, mDialogListener);
        dialog.setExtra(entry);
        dialog.setDefaultText(entry.getComment());
        dialog.show();
    }


    private class DialogListener implements BasicInputDialog.InputListener {
        public void onInput(int id, CharSequence input, Object extra) {
            ItemEntry entry = (ItemEntry) extra;
            if (id == DIALOG_DATA) {
                mItem.updateEntry(entry, input.toString(), entry.getComment());
                askComment(entry);
            } else if (id == DIALOG_COMMENT) {
                mItem.updateEntry(entry, entry.getData(), input.toString());
                mAdapter.notifyDataSetChanged();
            }
        }

        public void onCancelInput(int id, Object extra) {
            if (id == DIALOG_DATA) {
                askComment((ItemEntry) extra);
            }
        }
    }

    class EditListener implements EditableAdapter.EditListener<ItemEntry> {
        public void onEdit(ItemEntry entry) {
            askData(entry); // askData will call askComment
        }

        public void onDelete(ItemEntry entry) {
            boolean success = mItem.removeEntry(entry);
            if (success) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private class EntryClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
            Intent intent = new Intent(EntryActivity.this, DetailActivity.class);
            ItemEntry entry = (ItemEntry)mAdapter.getItem(pos);
            intent.putExtra(CROSS_ENTRY_DATA_KEY, entry.getData());
            intent.putExtra(CROSS_ENTRY_COMMENT_KEY, entry.getComment());
            intent.putExtra(CROSS_ENTRY_TIME_KEY, entry.getLastModifiedTime());
            startActivity(intent);
        }
    }
}
