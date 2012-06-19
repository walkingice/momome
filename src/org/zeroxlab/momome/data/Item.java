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
    public final static String DEF_NAME  = "(NO NAME)";
    public final static String ENTRY_NAME    = "entry_name";
    public final static String ENTRY_CONTENT = "entry_content";

    protected String mTitle;
    protected String mId;
    protected List<ItemEntry> mEntries;

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
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public int getEntryCount() {
        return mEntries.size();
    }

    public List<ItemEntry> getEntries() {
        return mEntries;
    }

    public void addEntry(String name, String content) {
        ItemEntry entry = new ItemEntry(name, content);
        updateEntry(entry, name, content);
    }

    public void updateEntry(ItemEntry entry, String name, String content) {
        entry.update(name, content);
        if (!mEntries.contains(entry)) {
            mEntries.add(entry);
        }
    }

    public static class ItemEntry {
        String iName;
        String iContent;

        public ItemEntry(String name, String content) {
            update(name, content);
        }

        public void update(String name, String content) {
            iName = name;
            iContent = content;
        }

        public String getName() {
            return iName;
        }

        public String getContent() {
            return iContent;
        }
    }
}
