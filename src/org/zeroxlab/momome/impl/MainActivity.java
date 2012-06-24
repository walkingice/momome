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
import org.zeroxlab.momome.data.Item;
import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.MomoApp;
import org.zeroxlab.momome.MomoModel;
import org.zeroxlab.momome.widget.EditableActivity;
import org.zeroxlab.momome.widget.EditableAdapter;
import org.zeroxlab.momome.widget.EditableListItem;
import org.zeroxlab.momome.widget.ItemAdapter;

import com.u1aryz.android.lib.newpopupmenu.MenuItem;
import com.u1aryz.android.lib.newpopupmenu.PopupMenu;
import com.u1aryz.android.lib.newpopupmenu.PopupMenu.OnItemSelectedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONObject;
import android.view.KeyEvent;
import android.view.WindowManager;

public class MainActivity extends EditableActivity implements Momo,
       EditableAdapter.EditListener<Item> {

    ListView mListView;
    View     mBtnAdd;
    View     mBtnMore;
    ItemAdapter mAdapter;
    MomoModel.StatusListener mStatusListener;
    ItemClickListener mItemClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initViews();

        mStatusListener = new StatusListener();
        MomoModel model = MomoApp.getModel();
        mAdapter = new ItemAdapter(this);
        mAdapter.setListener(this);
        mListView.setAdapter(mAdapter);

        mItemClickListener = new ItemClickListener();
        mListView.setOnItemClickListener(mItemClickListener);

        doReload();
    }

    @Override
    public void onAttachedToWindow() {
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        super.onAttachedToWindow();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            //this.finish(); //結束此activity
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        MomoApp.getModel().addListener(mStatusListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        MomoApp.getModel().removeListener(mStatusListener);
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.main_list_view);
        mBtnAdd   = findViewById(R.id.main_btn_add);
        mBtnMore  = findViewById(R.id.main_btn_more);
    }

    private void launchEntryActivity(String key) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra(CROSS_ITEM_KEY, key);
        startActivity(intent);
    }

    public void onClickAdd(View v) {
        MomoModel model = MomoApp.getModel();
        if (model.status() == DataStatus.OK
                || model.status() == DataStatus.FILE_IS_EMPTY) {
            getNewItemName();
        }
    }

    public void onClickSettings(View v) {
    }

    public void onClickEdit(View v) {
        super.onPerformEdit(v);
    }

    public void onClickMore(final View v) {
        final int fLock = 0;
        final int fSettings = 1;
        PopupMenu menu = new PopupMenu(this);
        menu.add(fLock, R.string.lock);
        menu.add(fSettings, R.string.settings);
        menu.setHeaderTitle("More options");
        menu.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(MenuItem item) {
                if (item.getItemId() == fLock) {
                    onClickReload(v);
                } else if (item.getItemId() == fSettings) {
                    onClickSettings(v);
                }
            }
        });
        menu.show(v);
    }

    public void onClickReload(View v) {
        MomoModel model = MomoApp.getModel();
        if (model.status() == DataStatus.OK) {
            model.lock();
            mAdapter.notifyDataSetChanged();
        } else {
            doReload();
        }
    }

    @Override
    public void onEdit(Item item) {
        renameItem(item);
    }

    @Override
    public void onDelete(Item item) {
        MomoModel model = MomoApp.getModel();
        model.removeItem(item);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStartEdit() {
        mBtnAdd.setVisibility(View.VISIBLE);
        mBtnMore.setVisibility(View.GONE);
        mAdapter.setEditing(true);
        mListView.setOnItemClickListener(null);
        mListView.invalidateViews();
    }

    @Override
    protected void onStopEdit() {
        mBtnAdd.setVisibility(View.GONE);
        mBtnMore.setVisibility(View.VISIBLE);
        mAdapter.setEditing(false);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.invalidateViews();
    }

    private void doReload() {
        MomoModel model = MomoApp.getModel();
        if (model.status() == DataStatus.NO_PASSWORD
                || model.status() == DataStatus.PASSWORD_WRONG) {
            askForPassword();
        }
    }

    private void askForPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText edit = new EditText(this);
        DialogInterface.OnClickListener okListener = new PasswordListener(edit);

        builder.setMessage("Enter Password to Unlock");
        builder.setCancelable(true);
        builder.setView(edit);
        builder.setPositiveButton(android.R.string.ok, okListener);
        builder.setNegativeButton(android.R.string.cancel, okListener);

        builder.show();
    }

    private void renameItem(Item item) {
        EditText edit = new EditText(this);
        edit.setText(item.getTitle());
        edit.selectAll();
        DialogInterface.OnClickListener rename = new RenameListener(edit, item);
        showInputDialog("Rename for item", rename, edit);
    }

    private void getNewItemName() {
        EditText edit = new EditText(this);
        DialogInterface.OnClickListener okListener = new AddItemListener(edit);
        showInputDialog("Name of new Item", okListener, edit);
    }

    private void showInputDialog(String msg, DialogInterface.OnClickListener listener, EditText edit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setView(edit);
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listener);

        builder.show();    }

    private void onEnteredPassword(CharSequence password) {
        MomoModel model = MomoApp.getModel();
        model.unlock(password);
    }

    private void onRenameItem(CharSequence name, Item item) {
        item.setTitle(name.toString());
        MomoApp.getModel().save();
        mAdapter.notifyDataSetChanged();
    }

    private void onAddItem(CharSequence name) {
        Item item = new Item(name.toString());
        MomoApp.getModel().addItem(item);
        MomoApp.getModel().save();
        mAdapter.notifyDataSetChanged();
    }

    private class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
            Item item = (Item)mAdapter.getItem(pos);
            launchEntryActivity(item.getId());
        }
    }

    private class PasswordListener implements DialogInterface.OnClickListener {
        TextView iTextView;

        PasswordListener(TextView tv) {
            iTextView = tv;
        }

        public void onClick(DialogInterface dialog, int button) {
            CharSequence input = iTextView.getText();
            if (button != AlertDialog.BUTTON_POSITIVE
                    || input == null
                    || input.toString().equals("")) {

                return; // cancel or does not give a name, nothing happen
            } else {
                onEnteredPassword(input);
            }
        }
    }

    private class RenameListener implements DialogInterface.OnClickListener {
        TextView iTextView;
        Item iItem;

        RenameListener(TextView tv, Item item) {
            iTextView = tv;
            iItem = item;
        }

        public void onClick(DialogInterface dialog, int button) {
            CharSequence input = iTextView.getText();
            if (button != AlertDialog.BUTTON_POSITIVE
                    || input == null
                    || input.toString().equals("")) {

                return; // cancel or does not give a name, nothing happen
            } else {
                onRenameItem(input,iItem);
            }
        }
    }

    private class AddItemListener implements DialogInterface.OnClickListener {
        TextView iTextView;

        AddItemListener(TextView tv) {
            iTextView = tv;
        }

        public void onClick(DialogInterface dialog, int button) {
            CharSequence input = iTextView.getText();
            if (button != AlertDialog.BUTTON_POSITIVE
                    || input == null
                    || input.toString().equals("")) {

                return; // cancel or does not give a name, nothing happen
            } else {
                onAddItem(input);
            }
        }
    }

    private class StatusListener implements MomoModel.StatusListener {
        public void onStatusChanged(DataStatus now) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
