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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ContributionActivity extends Activity implements Momo {

    private TextView mTitle;
    private TextView mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribution);
        mTitle = (TextView) findViewById(R.id.contribution_title);
        mContent = (TextView) findViewById(R.id.contribution_content);

        mTitle.setText("Contribution and License");
        appendContribution();
        appendKeyczar();
        appendNewPopupMenu();
    }

    private void appendContribution() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("author: Julian Chu <walkingice at 0xlab.org>\n");
        sb.append("\n");
        mContent.append(sb.toString());
    }

    private void appendKeyczar() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Keyczar\n");
        sb.append("\n");
        sb.append("Base64Coder.java is part of project keyczar which Copyright 2008 Google Inc, but\n");
        sb.append("released under the Apache2 License.\n");
        sb.append("\n");
        sb.append("http://www.keyczar.org/\n");
        sb.append("\n");
        sb.append("@Copyright 2008 Google Inc.\n");
        sb.append("@Modified by 2012 walkingice to make it buildable\n");
        sb.append("\n");
        sb.append("Licensed under the Apache License, Version 2.0 (the \"License\")\n");
        sb.append("you may not use this file except in compliance with the License.\n");
        sb.append("You may obtain a copy of the License at\n");
        sb.append("\n");
        sb.append("     http://www.apache.org/licenses/LICENSE-2.0\n");
        sb.append("\n");
        sb.append("Unless required by applicable law or agreed to in writing, software\n");
        sb.append("distributed under the License is distributed on an \"AS IS\" BASIS,\n");
        sb.append("WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n");
        sb.append("See the License for the specific language governing permissions and\n");
        sb.append("limitations under the License.\n");
        sb.append("\n");
        mContent.append(sb.toString());
    }

    private void appendNewPopupMenu() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("NewPopupMenu\n");
        sb.append("\n");
        sb.append("Library Android New Popup Menu is Copyright 2012 u1aryz, but\n");
        sb.append("released under the Apache2 License.\n");
        sb.append("\n");
        sb.append("https://github.com/u1aryz/Android-NewPopupMenu\n");
        sb.append("Developer: @u1aryz ( Yuichi. Arai ) - u1aryz.d [at] gmail.com\n");
        sb.append("\n");
        sb.append("@Copyright 2012 u1aryz\n");
        sb.append("@Modified by 2012 walkingice\n");
        sb.append("\n");
        sb.append("Licensed under the Apache License, Version 2.0 (the \"License\");\n");
        sb.append("you may not use this file except in compliance with the License.\n");
        sb.append("You may obtain a copy of the License at\n");
        sb.append("\n");
        sb.append("    http://www.apache.org/licenses/LICENSE-2.0\n");
        sb.append("\n");
        sb.append("Unless required by applicable law or agreed to in writing, software\n");
        sb.append("distributed under the License is distributed on an \"AS IS\" BASIS,\n");
        sb.append("WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or\n");
        sb.append("implied.\n");
        sb.append("See the License for the specific language governing permissions and\n");
        sb.append("limitations under the License.\n");
        sb.append("\n");
        mContent.append(sb.toString());
    }
}
