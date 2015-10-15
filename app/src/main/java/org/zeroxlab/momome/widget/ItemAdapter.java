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

package org.zeroxlab.momome.widget;

import org.zeroxlab.momome.R;
import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.MomoApp;
import org.zeroxlab.momome.MomoModel;
import org.zeroxlab.momome.data.Item;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class ItemAdapter extends EditableAdapter<Item> implements Momo {

    protected MomoModel       mModel;

    public ItemAdapter(Context context) {
        super(context);
        mModel    = MomoApp.getModel();
    }

    @Override
    public int getCount() {
        if (modelAccessible()) {
            return mModel.getItemsSize();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int pos) {
        if (modelAccessible()) {
            List<Item> items = mModel.getItems();
            return items.get(pos);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    protected void initView(int pos, EditableListItem item) {
        Item data = getBoundData(pos);
        item.setText(data.getTitle());
    }

    @Override
    protected Item getBoundData(int pos) {
        return (Item) getItem(pos);
    }

    @Override
    public void notifyDataSetChanged() {
        List<Item> items = mModel.getItems();
        java.util.Collections.sort(items);
        super.notifyDataSetChanged();
    }

    private boolean modelAccessible() {
        return ((mModel.status() == DataStatus.OK)
                || mModel.status() == DataStatus.FILE_IS_EMPTY);
    }
}
