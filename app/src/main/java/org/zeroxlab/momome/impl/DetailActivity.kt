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

package org.zeroxlab.momome.impl

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import org.zeroxlab.momome.Momo
import org.zeroxlab.momome.R
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : Activity(), Momo {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val viewData = findViewById(R.id.detail_data) as TextView
        val viewComment = findViewById(R.id.detail_comment) as TextView
        val viewTime = findViewById(R.id.detail_time) as TextView

        val intent = intent
        val data = intent.getStringExtra(Momo.CROSS_ENTRY_DATA_KEY)
        val comment = intent.getStringExtra(Momo.CROSS_ENTRY_COMMENT_KEY)
        val time = intent.getLongExtra(Momo.CROSS_ENTRY_TIME_KEY, System.currentTimeMillis())

        val date = Date(time)
        viewData.text = data
        viewComment.text = comment
        viewTime.text = sSDF.format(date)
    }

    companion object {
        protected val sSDF = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")
    }
}
