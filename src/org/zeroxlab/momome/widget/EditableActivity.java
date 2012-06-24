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

package org.zeroxlab.momome.widget;

import org.zeroxlab.momome.R;
import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.MomoApp;
import org.zeroxlab.momome.MomoModel;
import org.zeroxlab.momome.data.Item;
import org.zeroxlab.momome.data.Item.ItemEntry;
import org.zeroxlab.momome.widget.EntryAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.EditText;

public abstract class EditableActivity extends Activity {

    private boolean mIsEditing = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
    }

    @Override
    public void setContentView(int resId) {
        super.setContentView(resId);
    }

    @Override
    public void onBackPressed() {
        if (mIsEditing) {
            toggleEditing();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            if (onHomePressed()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void onPerformEdit(View v) {
        toggleEditing();
    }

    private void toggleEditing() {
        mIsEditing = !mIsEditing;
        if (mIsEditing) {
            onStartEdit();
        } else {
            onStopEdit();
        }
    }

    /* return false if pass the event to super class */
    abstract protected boolean onHomePressed();

    abstract protected void onStartEdit();
    abstract protected void onStopEdit();
}
