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

import org.zeroxlab.momome.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

public class DetailActivity extends Activity implements Momo {
    TextView mTextView;
    MomoModel mModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        mModel = MomoApp.getModel();

        int pos = getIntent().getIntExtra(CROSS_POS, INVALID_INT);
        initViews(pos);
    }

    private void initViews(int pos) {
        if (pos == INVALID_INT) {
            String msg = "Not valid position";
            Log.e(TAG, msg);
            Toast t = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            t.show();
            finish();
            return;
        }

        JSONObject root = mModel.getJSON();
        JSONArray array = root.optJSONArray(ITEM_LIST);
        JSONObject json = (JSONObject) array.opt(pos);
        mTextView = (TextView) findViewById(R.id.detail_textview);
        mTextView.setText(json.optString(ITEM_CONTENT));
    }
}
