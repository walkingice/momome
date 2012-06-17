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

package org.zeroxlab.momome;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONAdapter extends BaseAdapter implements Momo {

    protected Context         mContext;
    protected LayoutInflater  mInflater;
    protected JSONObject      mRoot;
    protected JSONArray       mItems;

    public JSONAdapter(Context context, JSONObject root) {
        super();
        mContext  = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setRoot(root);
    }

    @Override
    public int getCount() {
        if (mRoot == null || mItems == null) {
            return 0;
        } else {
            return mItems.length();
        }
    }

    @Override
    public Object getItem(int pos) {
        if (mRoot == null || mItems == null) {
            return null;
        } else {
            return mItems.opt(pos);
        }
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
         if (convertView == null || !(convertView instanceof TextView)) {
             convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
         }

         TextView tv = (TextView) convertView;
         Object obj = getItem(pos);
         if (obj != null && obj instanceof JSONObject) {
             JSONObject json = (JSONObject) obj;
             tv.setText(json.optString(ITEM_TITLE, "No Title"));
         } else {
             Log.e(TAG, "cannot get jsonobject for position:" + pos);
         }

         return convertView;
    }

    public void setRoot(JSONObject root) {
        mRoot  = root;
        this.onRootChanged();
    }

    public JSONObject getRoot() {
        return mRoot;
    }

    public void onRootChanged() {
        mItems = mRoot.optJSONArray(ITEM_LIST);
        super.notifyDataSetChanged();
    }
}
