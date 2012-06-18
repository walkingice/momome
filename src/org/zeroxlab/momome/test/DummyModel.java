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

package org.zeroxlab.momome.test;

import org.zeroxlab.momome.MomoModel;
import org.zeroxlab.momome.util.Util;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class DummyModel implements MomoModel {

    JSONObject mRoot;
    JSONArray  mArray;

    private String[] mTitles = {"Facebook", "Twitter", "Gmail"};
    private String[] mContents = {"contentOfFB", "contentOfTW", "contentOfGMail"};

    public DummyModel() {
        try {
            initRootJson();
            assignRandomDataToItems();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isAccessible() {
        return true;
    }

    @Override
    public int getItemsSize() {
        return mTitles.length;
    }

    @Override
    public JSONObject getItem(int id) {
        for (int i = 0; i < mArray.length(); i++) {
            JSONObject j = mArray.optJSONObject(i);
            if (j.optInt(ITEM_ID, INVALID_INT) == id) {
                return j;
            }
        }

        Log.d(TAG, "No such JSONObject for id:" + id);
        return null;
    }

    @Override
    public Map<String, String> getItemContent(int id) {
        JSONObject item = getItem(id);
        if (item == null) {
            Log.d(TAG, "No such JSONObject for id:" + id);
            return null;
        }

        Map<String,String> map = new TreeMap<String,String>();
        Iterator iter = item.keys();
        while(iter.hasNext()){
            String key = (String)iter.next();
            String value = item.optString(key);
            map.put(key,value);
        }

        return map;
    }

    @Override
    public JSONObject[] getItems() {
        JSONObject[] items = new JSONObject[mArray.length()];
        for (int i = 0; i < mArray.length(); i++) {
            items[i] = mArray.optJSONObject(i);
        }

        return items;
    }


    private void assignRandomDataToItems() {
        for (int i = 0; i < mArray.length(); i++) {
            JSONObject item = mArray.optJSONObject(i);
            assignRandomData(item);
        }
    }

    private void assignRandomData(JSONObject item) {
        int count = Util.randomInt(5);
        for (int i = 0; i < count; i++) {
            try {
                item.put("Key" + i, "Value" + Util.randomInt(100));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initRootJson() throws JSONException {
        mRoot = new JSONObject();
        mArray = new JSONArray();

        for (int i = 0; i < mTitles.length; i++) {
            JSONObject obj = new JSONObject();
            obj.put(ITEM_ID, Util.randomInt());
            obj.put(ITEM_TITLE, mTitles[i]);
            obj.put(ITEM_CONTENT, mContents[i]);
            mArray.put(obj);
        }

        mRoot.put(ITEM_LIST, mArray);
    }
}
