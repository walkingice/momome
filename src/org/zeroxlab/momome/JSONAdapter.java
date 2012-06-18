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
    protected MomoModel       mModel;

    public JSONAdapter(Context context) {
        super();
        mContext  = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mModel    = MomoApp.getModel();
    }

    @Override
    public int getCount() {
        if (mModel.isAccessible()) {
            return mModel.getItemsSize();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int pos) {
        if (mModel.isAccessible()) {
            JSONObject[] items = mModel.getItems();
            return items[pos];
        } else {
            return null;
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
             tv.setId(json.optInt(ITEM_ID));
         } else {
             Log.e(TAG, "cannot get jsonobject for position:" + pos);
             tv.setId(INVALID_INT);
         }

         return convertView;
    }
}
