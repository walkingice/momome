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
import org.zeroxlab.momome.widget.BasicInputDialog;
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
import android.widget.Toast;
import android.widget.ViewSwitcher;
import org.json.JSONObject;
import android.view.KeyEvent;
import android.view.WindowManager;

public class MainActivity extends EditableActivity implements Momo,
       EditableAdapter.EditListener<Item> {

    ListView mListView;
    View     mHint;
    ViewSwitcher mLTSwitcher; // left-top
    ViewSwitcher mRTSwitcher; // right-top
    ItemAdapter mAdapter;
    DialogListener mDialogListener;
    MomoModel.StatusListener mStatusListener;
    ItemClickListener mItemClickListener;

    private final static int DIALOG_PASSWORD  = 0xFF01;
    private final static int DIALOG_RENAME    = 0xFF02;
    private final static int DIALOG_ADD_ITEM  = 0xFF03;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initViews();

        mDialogListener = new DialogListener();
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
    protected void onDestroy() {
        super.onDestroy();
        onStopEdit();
        closeModel();
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        /* 1) singleTask launch mode
         * 2) clearTaskOnLaunch
         * when set both of them, onNewIntent meanse
         * re-enter this activity from HomeScreen or other
         * application. Locl model to protect data. */
        MomoApp.getModel().addListener(mStatusListener);
        closeModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        MomoApp.getModel().addListener(mStatusListener);
        updateVisibilityByStatus(MomoApp.getModel().status());
    }

    @Override
    public void onPause() {
        super.onPause();
        MomoApp.getModel().removeListener(mStatusListener);
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.main_list_view);
        mHint     = findViewById(R.id.main_hint);
        mLTSwitcher = (ViewSwitcher) findViewById(R.id.main_lt_switcher);
        mRTSwitcher = (ViewSwitcher) findViewById(R.id.main_rt_switcher);
    }

    private void launchEntryActivity(String key) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra(CROSS_ITEM_KEY, key);
        startActivity(intent);
    }

    private void closeModel() {
        MomoModel model = MomoApp.getModel();
        if (model.status() == DataStatus.OK) {
            model.save();
            model.lock();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onClickAdd(View v) {
        MomoModel model = MomoApp.getModel();
        if (model.status() == DataStatus.OK
                || model.status() == DataStatus.FILE_IS_EMPTY) {
            getNewItemName();
        }
    }

    public void onClickSettings(View v) {
        Intent i = new Intent(this, PrefMain.class);
        startActivity(i);
    }

    public void onClickEdit(View v) {
        MomoModel model = MomoApp.getModel();
        if (model.status() == DataStatus.OK
                || model.status() == DataStatus.FILE_IS_EMPTY) {
            super.toggleEditing();
        }
    }

    public void onClickDone(View v) {
        super.toggleEditing();
    }

    public void onClickMore(final View v) {
        final int fLock = 0;
        final int fSettings = 1;
        PopupMenu menu = new PopupMenu(this);
        menu.add(fLock, R.string.lockaction);
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
            closeModel();
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
        mLTSwitcher.showNext();
        mRTSwitcher.showNext();
        mAdapter.setEditing(true);
        mListView.setOnItemClickListener(null);
        mListView.invalidateViews();
    }

    @Override
    protected void onStopEdit() {
        mLTSwitcher.showNext();
        mRTSwitcher.showNext();
        mAdapter.setEditing(false);
        mListView.setOnItemClickListener(mItemClickListener);
        mListView.invalidateViews();
    }

    private void doReload() {
        MomoModel model = MomoApp.getModel();
        if (model.status() == DataStatus.NO_PASSWORD
                || model.status() == DataStatus.FILE_CANNOT_ACCESS
                || model.status() == DataStatus.PASSWORD_WRONG) {
            askForPassword();
        }
    }

    private void askForPassword() {
        String msg = super.getString(R.string.main_dialog_unlock);
        if (!MomoApp.getModel().internalFileExists()) {
            Log.d(TAG, "not exist?");
            msg = super.getString(R.string.main_dialog_init);
        }

        BasicInputDialog dialog = new BasicInputDialog(this, msg);
        dialog.setListener(DIALOG_PASSWORD, mDialogListener);
        dialog.show();
    }

    private void renameItem(Item item) {
        BasicInputDialog dialog = new BasicInputDialog(this,
                super.getString(R.string.main_dialog_rename_item));
        dialog.setDefaultText(item.getTitle());
        dialog.setExtra(item);
        dialog.setListener(DIALOG_RENAME, mDialogListener);
        dialog.show();
    }

    private void getNewItemName() {
        BasicInputDialog dialog = new BasicInputDialog(this,
                super.getString(R.string.main_dialog_add_item));
        dialog.setListener(DIALOG_ADD_ITEM, mDialogListener);
        dialog.show();
    }

    private void onEnteredPassword(CharSequence password) {
        MomoModel model = MomoApp.getModel();
        model.unlock(password);
        /* FIXME: it should detect PASSWORD_WRONG ONLY*/
        if(model.status() == DataStatus.PASSWORD_WRONG
                || model.status() == DataStatus.FILE_CANNOT_ACCESS ) {

            Toast.makeText(this, "Decrypt failed", Toast.LENGTH_LONG).show();
        }
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

    private void updateVisibilityByStatus(DataStatus status) {
        if (status == DataStatus.OK
                || status == DataStatus.FILE_IS_EMPTY) {
            mAdapter.notifyDataSetChanged();
            mHint.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        } else {
            mHint.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    private class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
            Item item = (Item)mAdapter.getItem(pos);
            launchEntryActivity(item.getId());
        }
    }

    private class DialogListener implements BasicInputDialog.InputListener {
        public void onInput(int id, CharSequence input, Object extra) {
            if (input.toString().equals("")) {
                return; // do nothing if user input nothing
            } else if (id == DIALOG_PASSWORD) {
                onEnteredPassword(input);
            } else if (id == DIALOG_RENAME) {
                if (extra != null && extra instanceof Item) {
                    onRenameItem(input, (Item) extra);
                }
            } else if (id == DIALOG_ADD_ITEM) {
                onAddItem(input);
            }
        }

        public void onCancelInput(int id, Object extra) {
        }
    }

    private class StatusListener implements MomoModel.StatusListener {
        public void onStatusChanged(DataStatus now) {
            MomoModel model = MomoApp.getModel();
            updateVisibilityByStatus(model.status());
        }
    }
}
