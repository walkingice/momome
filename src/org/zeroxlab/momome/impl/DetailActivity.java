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
import org.zeroxlab.momome.widget.EntryAdapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import android.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;

public class DetailActivity extends Activity implements Momo {
    protected MomoModel mModel;
    protected ListView  mContainer;
    protected View      mAddButton;
    protected View      mEditButton;
    protected Item      mItem;
    protected LayoutInflater mInflater;
    protected EntryAdapter mAdapter;

    private boolean mIsEditing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mModel = MomoApp.getModel();

        String key = getIntent().getStringExtra(CROSS_ITEM_KEY);
        mItem  = mModel.getItem(key);

        if (initViews(key)) {
            mAdapter = new EntryAdapter(this, mItem);
            mAdapter.setListener(new EditListener());
            mContainer.setAdapter(mAdapter);
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

        mContainer = (ListView) findViewById(R.id.detail_container);
        mAddButton = findViewById(R.id.detail_add_button);
        mEditButton = findViewById(R.id.detail_edit_button);
        return true;
    }

    public void onClickEditButton(View v) {
        toggleEditing();
    }

    public void onClickAddButton(View v) {
        mItem.addEntry(Item.DEF_NAME, "");
        mAdapter.notifyDataSetChanged();
    }

    private void toggleEditing() {
        mIsEditing = ! mIsEditing;
        mAdapter.setEditing(mIsEditing);
        if (mIsEditing) {
            mAddButton.setVisibility(View.VISIBLE);
            mContainer.invalidateViews();
        } else {
            mAddButton.setVisibility(View.GONE);
            mContainer.invalidateViews();
            finishEditing();
        }
    }

    private void finishEditing() {
        mAdapter.notifyDataSetChanged();
        mModel.save();
    }

    class EditListener implements EntryAdapter.EditListener {
        public void onEdit(ItemEntry entry) {
            askName(entry); // askName will call askContent
        }

        public void onDelete(ItemEntry entry) {
            boolean success = mItem.removeEntry(entry);
            if (success) {
                mAdapter.notifyDataSetChanged();
            }
        }

        private void askName(final ItemEntry entry) {
            final EditText edit = new EditText(DetailActivity.this);

            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int what) {
                    mItem.updateEntry(entry, edit.getText().toString(), entry.getContent());
                    askContent(entry);
                }
            };

            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int what) {
                    askContent(entry);
                }
            };

            showDialog(edit, entry.getName(), "Edit Name", entry, ok, cancel);
        }

        private void askContent(final ItemEntry entry) {
            final EditText edit = new EditText(DetailActivity.this);

            DialogInterface.OnClickListener ok = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int what) {
                    mItem.updateEntry(entry, entry.getName(), edit.getText().toString());
                    mAdapter.notifyDataSetChanged();
                }
            };

            DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int what) {
                }
            };

            showDialog(edit, entry.getContent(), "Edit Content", entry, ok, cancel);
        }

        private void showDialog(EditText edit,
                CharSequence defValue,
                CharSequence msg,
                ItemEntry entry,
                DialogInterface.OnClickListener okListener,
                DialogInterface.OnClickListener cancelListener) {

            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
            builder.setMessage(msg);
            builder.setView(edit);
            edit.setText(defValue);
            edit.selectAll();
            edit.requestFocus();
            builder.setPositiveButton(android.R.string.ok, okListener);
            builder.setNegativeButton(android.R.string.cancel, cancelListener);
            builder.show();
        }
    }
}
