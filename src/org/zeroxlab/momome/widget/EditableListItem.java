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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import java.util.List;
import android.util.AttributeSet;

public class EditableListItem extends FrameLayout {

    private int mLayoutRes = R.layout.editable_list_item;

    protected boolean   mIsEditing = false;
    protected View      mBtnEdit;
    protected View      mBtnDelete;
    protected TextView  mText;
    protected TextView  mComment;
    protected Listener  mListener;

    public EditableListItem(Context context) {
        this(context, null);
    }

    public EditableListItem(Context context, boolean isEditing) {
        this(context, null, isEditing);
    }

    public EditableListItem(Context context, AttributeSet attrs) {
        this(context, attrs, false);
    }

    public EditableListItem(Context context, AttributeSet attrs,
            boolean isEditing) {

        super(context, null);
        mIsEditing = isEditing;
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(mLayoutRes, this, true);
        mBtnEdit   = findViewById(R.id.editable_item_btn_edit);
        mBtnDelete = findViewById(R.id.editable_item_btn_delete);
        mText      = (TextView) findViewById(R.id.editable_item_text);
        mComment   = (TextView) findViewById(R.id.editable_item_comment);

        PrivateOnClickListener listener = new PrivateOnClickListener();
        mBtnEdit.setOnClickListener(listener);
        mBtnDelete.setOnClickListener(listener);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public CharSequence getText() {
        if (mText != null) {
            return mText.getText();
        } else {
            return null;
        }
    }

    public CharSequence getComment() {
        if (mComment != null) {
            return mComment.getText();
        } else {
            return null;
        }
    }

    public void setText(CharSequence text) {
        if (mText != null) {
            mText.setText(text);
        }
    }

    public void setComment(CharSequence text) {
        if (mComment != null) {
            mComment.setText(text);
        }
    }

    public void setEditing(boolean editing) {
        mIsEditing = editing;
        if (mIsEditing) {
            mBtnEdit.setVisibility(View.VISIBLE);
            mBtnDelete.setVisibility(View.VISIBLE);
        } else {
            mBtnEdit.setVisibility(View.GONE);
            mBtnDelete.setVisibility(View.GONE);
        }
    }

    private void onClickEdit(View v) {
        if (mListener != null) {
            mListener.onClickEdit(this);
        }
    }

    private void onClickDelete(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete);
        if (mText != null) {
            builder.setMessage(mText.getText());
        }
        builder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (mListener != null) {
                                mListener.onClickDelete(EditableListItem.this);
                            }
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }

    private class PrivateOnClickListener implements View.OnClickListener {
        public void onClick(View v ) {
            if (v == mBtnEdit) {
                onClickEdit(v);
            } else if (v == mBtnDelete) {
                onClickDelete(v);
            }
        }
    }

    public interface Listener {
        public void onClickEdit(View thisItem);
        public void onClickDelete(View thisItem);
    }
}
