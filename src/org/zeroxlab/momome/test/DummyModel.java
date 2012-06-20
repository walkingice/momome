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

import org.zeroxlab.momome.data.Item;;
import org.zeroxlab.momome.MomoModel;
import org.zeroxlab.momome.util.Util;

import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DummyModel implements MomoModel {

    List<Item> mList;

    private String[] mTitles = {"Facebook", "Twitter", "Gmail"};
    private String[] mContents = {"contentOfFB", "contentOfTW", "contentOfGMail"};

    public DummyModel() {
        mList = new ArrayList<Item>();
        initItems();
        assignRandomDataToItems();
    }

    @Override
    public void lock() {
        // I am dummy, I am not luck and never lock!
    }

    @Override
    public boolean unlock(CharSequence password) {
        return true;
    }

    @Override
    public boolean save() {
        return true;
    }

    @Override
    public DataStatus status() {
        return DataStatus.OK;
    }

    @Override
    public int getItemsSize() {
        return mList.size();
    }

    @Override
    public void addItem(Item item) {
        if (!mList.contains(item)) {
            mList.add(item);
        }
    }

    @Override
    public Item getItem(String key) {
        for (int i = 0; i < mList.size(); i++) {
            Item item = mList.get(i);
            if (item.getId().equals(key)) {
                return item;
            }
        }

        Log.e(TAG, "No item with key:" + key);
        return null;
    }

    @Override
    public List<Item> getItems() {
        return mList;
    }


    private void assignRandomDataToItems() {
        for (int i = 0; i < mList.size(); i++) {
            Item item = mList.get(i);
            assignRandomData(item);
        }
    }

    private void assignRandomData(Item item) {
        int count = Util.randomInt(5) + 1;
        for (int i = 0; i < count; i++) {
            item.addEntry("Key" + i, "Value" + Util.randomInt(100));
        }
    }

    private void initItems() {
        for (int i = 0; i < mTitles.length; i++) {
            Item item = new Item(mTitles[i]);
            mList.add(item);
        }
    }
}
