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
import java.util.Map;
import java.util.Set;
import android.app.AlertDialog;
import android.widget.EditText;
import android.content.DialogInterface;

public class DetailActivity extends Activity implements Momo {
    protected MomoModel mModel;
    protected ViewGroup mContainer;
    protected View      mAddButton;
    protected View      mEditButton;
    protected LayoutInflater mInflater;
    protected Map<String, String> mMap;

    private boolean mIsEditing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mModel = MomoApp.getModel();

        int id = getIntent().getIntExtra(CROSS_ITEM_ID, INVALID_INT);
        initViews(id);
    }

    private void initViews(int id) {
        if (id == INVALID_INT || !mModel.isAccessible()) {
            String msg = "Not valid id";
            Log.e(TAG, msg);
            Toast t = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            t.show();
            finish();
            return;
        }

        mContainer = (ViewGroup) findViewById(R.id.detail_container);
        mAddButton = findViewById(R.id.detail_add_button);
        mEditButton = findViewById(R.id.detail_edit_button);

        StringBuilder sb = new StringBuilder();
        mMap = mModel.getItemContent(id);
        regenerateRows(mMap);
    }

    private void regenerateRows(Map<String, String> map) {
        mContainer.removeAllViews();
        Set<Map.Entry<String, String>> set = map.entrySet();
        for(Map.Entry<String, String> entry : set) {
            insertNewRow(entry);
        }
    }

    private void insertNewRow(Map.Entry<String, String> entry) {
        View view = mInflater.inflate(R.layout.entry_editable, null);
        TextView viewKey   = (TextView) view.findViewById(R.id.entry_key);
        TextView viewValue = (TextView) view.findViewById(R.id.entry_value);
        View btnName   = view.findViewById(R.id.entry_btn_name);
        View btnValue  = view.findViewById(R.id.entry_btn_value);

        btnName.setTag(viewKey);
        viewKey.setTag(entry);
        btnValue.setTag(viewValue);
        viewValue.setTag(entry);

        viewKey.setText(entry.getKey());
        viewValue.setText(entry.getValue());

        mContainer.addView(view);
    }

    public void onClickEditName(View v) {
        TextView tv = (TextView)v.getTag();
        final Map.Entry<String, String> entry = (Map.Entry<String, String>)tv.getTag();
        final EditText edit = new EditText(this);
        edit.setText(entry.getKey());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Edit Name");
        builder.setView(edit);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int what) {
                mMap.remove(entry.getKey());
                mMap.put(edit.getText().toString(), entry.getValue());
                regenerateRows(mMap);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int what) {
            }
        });

        builder.show();
    }

    public void onClickEditValue(View v) {
    }

    public void onClickEditButton(View v) {
        toggleEditing();
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
        regenerateRows(mMap);
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
