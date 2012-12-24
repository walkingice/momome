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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;

public class BasicInputDialog extends AlertDialog {

    protected InputListener mListener;
    protected EditText      mEditText;
    protected int           mId;
    protected Object        mExtra;

    private final int PADDING = 5;

    public BasicInputDialog(Context context) {
        this(context, null);
    }

    public BasicInputDialog(Context context, int msgId) {
        this(context, context.getText(msgId));
    }

    public BasicInputDialog(Context context, CharSequence msg) {
        super(context);
        super.setMessage(msg);
        init(context);
    }

    public void setDefaultText(CharSequence text) {
        mEditText.setText(text);
        mEditText.selectAll();
    }

    public void setInputType(int type) {
        mEditText.setInputType(type);
    }

    public void setExtra(Object o) {
        mExtra = o;
    }

    private void init(Context context) {
        mEditText = new EditText(context);
        mEditText.setSingleLine(true);
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        setIcon(android.R.drawable.ic_dialog_info);
        setCancelable(true);
        setView(mEditText, PADDING, PADDING, PADDING, PADDING);
    }

    public void setListener(int id, InputListener listener) {
        mId = id;
        mListener = listener;

        InternalListener internal = new InternalListener();
        super.setButton(DialogInterface.BUTTON_POSITIVE,
                getContext().getText(android.R.string.ok),
                internal);
        super.setButton(DialogInterface.BUTTON_NEGATIVE,
                getContext().getText(android.R.string.cancel),
                internal);
    }

    private class InternalListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            if (mListener == null) {
                return;
            }

            CharSequence input = mEditText.getText();
            if (input == null) {
                input = "";
            }

            if (which == DialogInterface.BUTTON_POSITIVE) {
                mListener.onInput(mId, input, mExtra);
            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                mListener.onCancelInput(mId, mExtra);
            }
        }
    }

    public interface InputListener {
        public void onInput(int id, CharSequence input, Object extra);
        public void onCancelInput(int id, Object extra);
    }
}
