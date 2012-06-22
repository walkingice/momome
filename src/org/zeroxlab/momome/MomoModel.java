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

import org.zeroxlab.momome.data.Item;

import org.json.JSONObject;
import java.util.List;

public interface MomoModel extends Momo {

    /**
     * Lock to make saved data unavailable until unlocked.
     */
    public void lock();

    /**
     * Unlock saved data by key.
     *
     * Momome assumes local data was saved in password based encryption.
     * @param password The password to decrypt file
     * @return True if successful.
     */
    public boolean unlock(CharSequence password);

    /**
     * Ask Model to save data immediatly.
     * @return true if successful.
     */
    public boolean save();

    /**
     * To check if there is data to access.
     *
     * Model might have data for accessing. It might not have if
     * the data is locked, the password is wrong or the file is not exists.
     *
     * @return Status code
     */
    public DataStatus status();

    public int getItemsSize();
    public void addItem(Item item);
    public void removeItem(Item item);
    public Item getItem(String key);
    public List<Item> getItems();

    public void addListener(StatusListener listener);
    public void removeListener(StatusListener listener);

    public interface StatusListener  {
        public void onStatusChanged(DataStatus now);
    }
}
