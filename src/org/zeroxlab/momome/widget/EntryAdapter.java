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
import org.zeroxlab.momome.data.Item.ItemEntry;
import org.zeroxlab.momome.widget.EditableListItem;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class EntryAdapter extends BaseAdapter implements Momo {

    protected Context         mContext;
    protected LayoutInflater  mInflater;
    protected Item            mItem;
    protected EditListener    mListener;
    protected ClickListener   mMyListener;
    protected boolean         mIsEditing = false;

    public EntryAdapter(Context context, Item item) {
        super();
        mContext  = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItem     = item;
        mMyListener = new ClickListener();
    }

    @Override
    public int getCount() {
        return mItem.getEntryCount();
    }

    @Override
    public Object getItem(int pos) {
        return mItem.getEntry(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
         if (convertView == null
                 || !(convertView instanceof EditableListItem)) {

             convertView = new EditableListItem(mContext, mIsEditing);
         }

         ItemEntry entry = (ItemEntry) getItem(pos);

         EditableListItem item = (EditableListItem)convertView;
         item.setEditing(mIsEditing);
         item.setText(entry.getData());
         item.setComment(entry.getComment());
         item.setListener(mMyListener);

         item.setTag(entry);
         return convertView;
    }

    public void setEditing(boolean editing) {
        mIsEditing = editing;
    }

    private class ClickListener implements EditableListItem.Listener {
        public void onClickEdit(View whichItem) {
            if (mListener == null) {
                return;
            } else {
                mListener.onEdit((ItemEntry)whichItem.getTag());
            }
        }

        public void onClickDelete(View whichItem) {
            if (mListener == null) {
                return;
            } else {
                mListener.onDelete((ItemEntry)whichItem.getTag());
            }
        }
    }

    public void setListener(EditListener listener) {
        mListener = listener;
    }

    /**
     * each views generate by getView() provide interface to editing.
     *
     * The inflated list item might has buttons 'Edit' or 'Delete'.
     * ItemAdapter tries to listen it OnClick event and notify
     * EditLister to do corresponding action.
     * */
    public interface EditListener {
        public void onEdit(ItemEntry which);
        public void onDelete(ItemEntry which);
    }
}

