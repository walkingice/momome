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
import org.zeroxlab.momome.Parser;
import org.zeroxlab.momome.Parser.ParseException;
import org.zeroxlab.momome.data.Item;
import org.zeroxlab.momome.data.Entry;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser implements Momo, Parser {

    public final static int VERSION_1 = 1; // maybe someday we need another version

    protected int mVersion;

    public JSONParser() {
        mVersion = VERSION_1;
    }

    public List<Item> parse(CharSequence content) throws ParseException {
        if (content == null || content.length() == 0) {
            throw new ParseException("Empty content, cannot parse",
                    ParseException.State.EMPTY);
        }

        if (mVersion == VERSION_1) {
            return parseVersion1(content);
        } else {
            throw new ParseException("Unknown parser version");
        }
    }

    public CharSequence generate(List<Item> items) throws ParseException {
        if (mVersion == VERSION_1) {
            return generateVersion1(items);
        } else {
            throw new ParseException("Unknown parser version");
        }
    }

    private List<Item> parseVersion1(CharSequence content) throws ParseException {
        try {
            JSONObject root = new JSONObject(content.toString());
            if (!root.has(KEY_VERSION)) {
                throw new ParseException("This JSON file does not have version",
                        ParseException.State.WRONG_FORMAT);
            }

            if (!root.has(KEY_ITEMS)) {
                throw new ParseException("This JSON file does not have version",
                        ParseException.State.WRONG_FORMAT);
            }

            JSONArray array = root.optJSONArray(KEY_ITEMS);
            List<Item> items = new ArrayList<Item>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonItem = array.getJSONObject(i);
                items.add(JSONObjectToItem(jsonItem));
            }

            return items;
        } catch (JSONException e) {
            throw new ParseException(e, ParseException.State.BAD_DATA);
        }
    }

    private Item JSONObjectToItem(JSONObject json) throws ParseException {
        if (!json.has(KEY_ITEM_TITLE)) {
            throw new ParseException("This JSON object does not have title",
                    ParseException.State.WRONG_FORMAT);
        }

        if (!json.has(KEY_ITEM_ENTRIES)) {
            throw new ParseException("This JSON object does not have entries",
                    ParseException.State.WRONG_FORMAT);
        }

        Item item = new Item(json.optString(KEY_ITEM_TITLE));
        JSONArrayToEntries(item, json.optJSONArray(KEY_ITEM_ENTRIES));

        /* we should update time after parse entries since updateEntry will
         * change the time as well. */
        long time = json.optLong(KEY_ITEM_TIME, System.currentTimeMillis());
        item.setLastModifiedTime(time);
        return item;
    }

    private void JSONArrayToEntries(Item item, JSONArray array) throws ParseException {
        for (int i = 0; i < array.length(); i ++) {
            JSONObject obj = array.optJSONObject(i);
            String data    = obj.optString(KEY_ENTRY_DATA, Entry.DEF_NAME);
            String comment = obj.optString(KEY_ENTRY_COMMENT, "");
            long   time    = obj.optLong(KEY_ENTRY_TIME, System.currentTimeMillis());
            item.addEntry(time, data, comment);
        }
    }

    private CharSequence generateVersion1(List<Item> items) throws ParseException {
        try {
            JSONObject root = new JSONObject();
            root.put(KEY_VERSION, VERSION_1);

            JSONArray array = new JSONArray();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                array.put(itemToJSONObject(item));
            }

            root.put(KEY_ITEMS, array);
            return root.toString();
        } catch (JSONException e) {
            throw new ParseException(e);
        }
    }

    private JSONObject itemToJSONObject(Item item) throws ParseException {
        try {
            JSONObject json = new JSONObject();
            List<Entry> entries = item.getEntries();
            JSONArray array = entriesToJSONArray(entries);
            json.put(KEY_ITEM_TITLE, item.getTitle());
            json.put(KEY_ITEM_TIME, item.getLastModifiedTime());
            json.put(KEY_ITEM_ENTRIES, array);
            return json;
        } catch (JSONException e) {
            throw new ParseException(e);
        }
    }

    private JSONArray entriesToJSONArray(List<Entry> entries) throws ParseException {
        try {
            JSONArray array = new JSONArray();
            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                JSONObject json = new JSONObject();
                json.put(KEY_ENTRY_DATA, entry.getData());
                json.put(KEY_ENTRY_COMMENT, entry.getComment());
                json.put(KEY_ENTRY_TIME, entry.getLastModifiedTime());
                array.put(json);
            }

            return array;
        } catch (JSONException e) {
            throw new ParseException(e);
        }
    }
}
