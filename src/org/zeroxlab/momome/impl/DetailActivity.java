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
    protected ViewGroup mContainer;
    protected View      mAddButton;
    protected View      mEditButton;
    protected Item      mItem;
    protected LayoutInflater mInflater;

    private boolean mIsEditing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mModel = MomoApp.getModel();

        String key = getIntent().getStringExtra(CROSS_ITEM_KEY);
        mItem  = mModel.getItem(key);
        initViews(key);
    }

    private void initViews(String key) {
        if (key == null || key.equals("") || mModel.status() != DataStatus.OK) {
            String msg = "Not valid key";
            Log.e(TAG, msg);
            Toast t = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            t.show();
            finish();
            return;
        }

        mContainer = (ViewGroup) findViewById(R.id.detail_container);
        mAddButton = findViewById(R.id.detail_add_button);
        mEditButton = findViewById(R.id.detail_edit_button);

        updateRows();
    }

    private void updateRows() {
        mContainer.removeAllViews();
        while(mContainer.getChildCount() != mItem.getEntryCount()) {
            if (mContainer.getChildCount() > mItem.getEntryCount()) {
                mContainer.removeViewAt(0);
            } else {
                View view = mInflater.inflate(R.layout.entry_editable, null);
                mContainer.addView(view);
            }
        }

        List<Item.ItemEntry> list = mItem.getEntries();
        for(int i = 0; i < list.size(); i++) {
            updateOneRow(list.get(i), mContainer.getChildAt(i));
        }
    }

    private void updateOneRow(ItemEntry entry, View view) {
        TextView viewKey   = (TextView) view.findViewById(R.id.entry_key);
        TextView viewValue = (TextView) view.findViewById(R.id.entry_value);
        View btnName   = view.findViewById(R.id.entry_btn_name);
        View btnValue  = view.findViewById(R.id.entry_btn_value);

        btnName.setTag(viewKey);
        viewKey.setTag(entry);
        btnValue.setTag(viewValue);
        viewValue.setTag(entry);

        viewKey.setText(entry.getName());
        viewValue.setText(entry.getContent());
        if (mIsEditing) {
            btnName.setVisibility(View.VISIBLE);
            btnValue.setVisibility(View.VISIBLE);
        } else {
            btnName.setVisibility(View.GONE);
            btnValue.setVisibility(View.GONE);
        }
    }

    public void onClickEditName(View v) {
        TextView tv = (TextView)v.getTag();
        final ItemEntry entry = (ItemEntry) tv.getTag();
        final EditText edit = new EditText(this);
        edit.setText(entry.getName());
        edit.selectAll();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Edit Name");
        builder.setView(edit);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int what) {
                mItem.updateEntry(entry, edit.getText().toString(), entry.getContent());
                updateRows();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int what) {
            }
        });

        builder.show();
    }

    public void onClickEditValue(View v) {
        TextView tv = (TextView)v.getTag();
        final ItemEntry entry = (ItemEntry) tv.getTag();
        final EditText edit = new EditText(this);
        edit.setText(entry.getContent());
        edit.selectAll();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Edit Content");
        builder.setView(edit);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int what) {
                mItem.updateEntry(entry, entry.getName(), edit.getText().toString());
                updateRows();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int what) {
            }
        });

        builder.show();
    }

    public void onClickEditButton(View v) {
        toggleEditing();
    }

    public void onClickAddButton(View v) {
        mItem.addEntry(Item.DEF_NAME, "");
        updateRows();
    }

    private void toggleEditing() {
        if (mIsEditing) {
            setVisibility(View.GONE);
            mAddButton.setVisibility(View.GONE);
            finishEditing();
        } else {
            setVisibility(View.VISIBLE);
            mAddButton.setVisibility(View.VISIBLE);
        }

        mIsEditing = ! mIsEditing;
    }

    private void finishEditing() {
        updateRows();
        // save result to file
    }

    private void setVisibility(int visibility) {
        for (int i = 0; i < mContainer.getChildCount(); i++) {
            View view = mContainer.getChildAt(i);
            if (view.getId() == R.id.entry) {
                setEntryVisibility((ViewGroup)view, visibility);
            } else {
            }
        }
    }

    private void setEntryVisibility(ViewGroup g, int visibility) {
        for (int i = 0; i < g.getChildCount(); i++) {
            View child = g.getChildAt(i);
            if (child.getId() == R.id.entry_btn_name
                    || child.getId() == R.id.entry_btn_value) {
                child.setVisibility(visibility);
            }
        }
    }
}
