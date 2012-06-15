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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DummyModel implements MomoModel {

    JSONObject mRoot;

    private String[] mTitles = {"Facebook", "Twitter", "Gmail"};

    public DummyModel() {
        try {
            initRootJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJSON() {
        return mRoot;
    }

    private void initRootJson() throws JSONException {
        mRoot = new JSONObject();

        JSONArray array = new JSONArray();

        for (int i = 0; i < mTitles.length; i++) {
            JSONObject obj = new JSONObject();
            obj.put(ITEM_TITLE, mTitles[i]);
            array.put(obj);
        }

        mRoot.put(ITEM_LIST, array);
    }
}
