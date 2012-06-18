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
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Map;
import java.util.Set;

public class DetailActivity extends Activity implements Momo {
    protected MomoModel mModel;
    protected ViewGroup mContainer;
    protected View      mAddButton;
    protected View      mEditButton;
    protected LayoutInflater mInflater;

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
        Map<String, String> map = mModel.getItemContent(id);
        Set<Map.Entry<String, String>> set = map.entrySet();
        for(Map.Entry<String, String> entry : set) {
            String key = entry.getKey();
            String value = entry.getValue();
            insertNewRow(key, value);
        }
    }

    private void insertNewRow(String key, String value) {
        View view = mInflater.inflate(R.layout.entry_editable, null);
        TextView viewKey   = (TextView) view.findViewById(R.id.entry_key);
        TextView viewValue = (TextView) view.findViewById(R.id.entry_value);
        viewKey.setText(key);
        viewValue.setText(value);

        int btnPosition = mContainer.indexOfChild(mAddButton);
        int targetPosition = mContainer.getChildCount();
        if (btnPosition != -1) {
            // if the Add button is in the container, insert new row
            // before the add button.
            targetPosition = btnPosition;
        }

        mContainer.addView(view, targetPosition);
    }

    public void onClickEditName(View v) {
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
