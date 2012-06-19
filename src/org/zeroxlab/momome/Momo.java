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

import android.os.Environment;

public interface Momo {
    public final static String TAG = "Momo";

    public final static String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getPath();
    public final static String EXTERNAL_DIR = EXTERNAL_STORAGE + "/Momome";

    /* for JSON */
    public final static String KEY_VERSION       = "momo_version";
    public final static String KEY_ITEMS         = "momo_items";
    public final static String KEY_ITEM_TITLE    = "momo_item_title";
    public final static String KEY_ITEM_ENTRIES  = "momo_item_entries";
    public final static String KEY_ENTRY_NAME    = "momo_entry_name";
    public final static String KEY_ENTRY_CONTENT = "momo_entry_content";


    /* for cross activity */
    public final static int    INVALID_INT    = -1;
    public final static String CROSS_ITEM_KEY = "cross_activity_item_key";
}
