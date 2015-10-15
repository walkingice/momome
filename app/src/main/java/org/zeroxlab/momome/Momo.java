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
    String TAG = "Momo";

    // just a test for Java 8 new feature
    default String getAppTag () {
        return TAG;
    }

    String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory().getPath();
    String EXTERNAL_DIR = EXTERNAL_STORAGE + "/Momome";

    String FILENAME = "MomomeData";

    String ENCODING = "UTF8";

    /* for JSON */
    String KEY_VERSION       = "momo_version";
    String KEY_ITEMS         = "momo_items";
    String KEY_ITEM_TITLE    = "momo_item_title";
    String KEY_ITEM_ENTRIES  = "momo_item_entries";
    String KEY_ITEM_TIME     = "momo_item_last_modified_time";
    String KEY_ENTRY_DATA    = "momo_entry_data";
    String KEY_ENTRY_COMMENT = "momo_entry_comment";
    String KEY_ENTRY_TIME    = "momo_entry_last_modified_time";


    /* for cross activity */
    int    INVALID_INT    = -1;
    String CROSS_ITEM_KEY = "cross_activity_item_key";
    String CROSS_ENTRY_DATA_KEY   = "cross_activity_entry_data_key";
    String CROSS_ENTRY_COMMENT_KEY = "cross_activity_entry_comment_key";
    String CROSS_ENTRY_TIME_KEY = "cross_activity_entry_time_key";

    /* for preference */
    String KEY_EXPORT_DATA = "key_export_data";
    String KEY_IMPORT_DATA = "key_import_data";
    String KEY_DELETE_DATA = "key_delete_data";
    String KEY_SEND_DATA   = "key_send_data";
    String KEY_CHANGE_PASSWORD = "key_change_password";

    enum DataStatus {
        OK,
        DATA_LOCKING,
        NO_PASSWORD,
        PASSWORD_WRONG,
        FILE_CANNOT_ACCESS,
        FILE_IS_EMPTY
    }
}
