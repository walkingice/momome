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

    public final static String FILENAME = "MomomeData";

    public final static String ENCODING = "UTF8";

    /* for JSON */
    public final static String KEY_VERSION       = "momo_version";
    public final static String KEY_ITEMS         = "momo_items";
    public final static String KEY_ITEM_TITLE    = "momo_item_title";
    public final static String KEY_ITEM_ENTRIES  = "momo_item_entries";
    public final static String KEY_ITEM_TIME     = "momo_item_last_modified_time";
    public final static String KEY_ENTRY_DATA    = "momo_entry_data";
    public final static String KEY_ENTRY_COMMENT = "momo_entry_comment";
    public final static String KEY_ENTRY_TIME    = "momo_entry_last_modified_time";


    /* for cross activity */
    public final static int    INVALID_INT    = -1;
    public final static String CROSS_ITEM_KEY = "cross_activity_item_key";
    public final static String CROSS_ENTRY_DATA_KEY   = "cross_activity_entry_data_key";
    public final static String CROSS_ENTRY_COMMENT_KEY = "cross_activity_entry_comment_key";
    public final static String CROSS_ENTRY_TIME_KEY = "cross_activity_entry_time_key";

    public enum DataStatus {
        OK,
        DATA_LOCKING,
        NO_PASSWORD,
        PASSWORD_WRONG,
        FILE_CANNOT_ACCESS,
        FILE_IS_EMPTY
    }
}
