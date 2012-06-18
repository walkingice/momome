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

public interface Momo {
    public final static String TAG = "Momo";

    /* for JSON */
    public final static String ITEM_ID       = "momo_item_id_in_integer";
    public final static String ITEM_LIST     = "momo_items";
    public final static String ITEM_TITLE    = "momo_item_title";
    public final static String ITEM_CONTENT  = "momo_item_content";


    /* for cross activity */
    public final static int    INVALID_INT   = -1;
    public final static String CROSS_ITEM_ID = "cross_activity_item_id";
}
