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

package org.zeroxlab.momome.data;

import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.util.Util;
import java.util.ArrayList;
import java.util.List;

public class Item implements Momo {

    public final static String DEF_TITLE = "(NO TITLE)";
    public final static String DEF_NAME  = "(Empty)";

    protected String mTitle;
    protected String mId;
    protected List<ItemEntry> mEntries;
    protected long mTime;

    public Item() {
        this(DEF_TITLE);
    }

    public Item(String title) {
        this("" + Util.randomInt(), title);
    }

    public Item(String id, String title) {
        setTitle(title);
        setId(id);
        mEntries = new ArrayList<ItemEntry>();
        mTime = System.currentTimeMillis();
    }

    public void setTitle(String title) {
        mTitle = title;
        mTime = System.currentTimeMillis();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setId(String id) {
        mId = id;
        mTime = System.currentTimeMillis();
    }

    public String getId() {
        return mId;
    }

    public void setLastModifiedTime(long time) {
        mTime = time;
    }

    public long getLastModifiedTime() {
        return mTime;
    }

    public int getEntryCount() {
        return mEntries.size();
    }

    public ItemEntry getEntry(int position) {
        return mEntries.get(position);
    }

    public List<ItemEntry> getEntries() {
        return mEntries;
    }

    public void addEntry(ItemEntry entry) {
        if (!mEntries.contains(entry)) {
            mEntries.add(entry);
        }
    }

    public void addEntry(String name, String content) {
        long time = System.currentTimeMillis();
        this.addEntry(time, name, content);
    }

    public void addEntry(long time, String name, String content) {
        ItemEntry entry = new ItemEntry(name, content);
        updateEntry(time, entry, name, content);
    }

    public void updateEntry(ItemEntry entry, String name, String content) {
        long time = System.currentTimeMillis();
        updateEntry(time, entry, name, content);
    }

    public void updateEntry(long time, ItemEntry entry, String name, String content) {
        entry.update(name, content);
        entry.setLastModifiedTime(time);
        if (!mEntries.contains(entry)) {
            mEntries.add(entry);
            this.setLastModifiedTime(time);
        }
    }

    public boolean removeEntry(ItemEntry entry) {
        if (mEntries.contains(entry)) {
            mEntries.remove(entry);
            return true;
        }

        return false;
    }

    public void clear() {
        /* clear data in case showing data when locking */
        mTitle = DEF_TITLE;
        mId    = "";

        for(ItemEntry entry: mEntries) {
            entry.clear();
        }
    }

    public static class ItemEntry {
        String iData;
        String iComment;
        long   iTime;

        public ItemEntry(String data, String comment) {
            update(data, comment);
        }

        public void update(String data, String comment) {
            iData = data;
            iComment = comment;
            iTime = System.currentTimeMillis();
        }

        public void setLastModifiedTime(long time) {
            iTime = time;
        }

        public long getLastModifiedTime() {
            return iTime;
        }

        public String getData() {
            return iData;
        }

        public String getComment() {
            return iComment;
        }

        public void clear() {
            iData= "";
            iComment= "";
        }
    }
}
