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

package org.zeroxlab.momome.impl;

import org.zeroxlab.momome.R;
import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.MomoApp;
import org.zeroxlab.momome.MomoModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.WindowManager;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends Activity implements Momo {
    protected final static SimpleDateFormat sSDF = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView viewData    = (TextView)findViewById(R.id.detail_data);
        TextView viewComment = (TextView)findViewById(R.id.detail_comment);
        TextView viewTime    = (TextView)findViewById(R.id.detail_time);

        Intent intent = getIntent();
        String data = intent.getStringExtra(CROSS_ENTRY_DATA_KEY);
        String comment = intent.getStringExtra(CROSS_ENTRY_COMMENT_KEY);
        long time = intent.getLongExtra(CROSS_ENTRY_TIME_KEY, System.currentTimeMillis());

        Date date = new Date(time);
        viewData.setText(data);
        viewComment.setText(comment);
        viewTime.setText(sSDF.format(date));
    }
}
