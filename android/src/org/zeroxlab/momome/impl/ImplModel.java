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

import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.data.ClearTextIO;
import org.zeroxlab.momome.data.PasswordIO;
import org.zeroxlab.momome.data.Item;
import org.zeroxlab.momome.data.JSONParser;
import org.zeroxlab.momome.FileIO;
import org.zeroxlab.momome.FileIO.RWException;
import org.zeroxlab.momome.Parser.ParseException;
import org.zeroxlab.momome.Parser;
import org.zeroxlab.momome.MomoModel;
import org.zeroxlab.momome.util.Util;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImplModel implements MomoModel, Momo {

    protected Context    mContext;
    protected List<StatusListener> mListeners;
    protected List<Item> mList;
    protected Parser     mParser;
    protected FileIO     mFileIO;
    protected CharSequence mPassword;

    protected String mFilePath = EXTERNAL_DIR + "/encrypted";
    protected DataStatus mStatus;

    public ImplModel(Context context) {
        mContext   = context;
        mListeners = new ArrayList<StatusListener>();
        mList = new ArrayList<Item>();
        mParser = new JSONParser();
        mFileIO = new PasswordIO();

        lock();
    }

    @Override
    public boolean internalFileExists() {
        try {
            // no specified path, it refers to Internal storage
            FileInputStream stream = mContext.openFileInput(FILENAME);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    @Override
    public void lock() {
        clearListContent();
        mPassword = null;
        changeStatus(DataStatus.NO_PASSWORD);
    }

    @Override
    public boolean unlock(CharSequence password) {
        mPassword = password;

        try {
            // no specified path, it refers to Internal storage
            FileInputStream stream = mContext.openFileInput(FILENAME);
            CharSequence data = mFileIO.read(mPassword.toString(), stream);
            mList = mParser.parse(data);
        } catch (FileNotFoundException e) {
            changeStatus(DataStatus.FILE_IS_EMPTY);
            return true;
        } catch (IOException e) {
                changeStatus(DataStatus.FILE_CANNOT_ACCESS);
                clearListContent();
                e.printStackTrace();
                return false;
        } catch (RWException e) {
                changeStatus(DataStatus.FILE_CANNOT_ACCESS);
                clearListContent();
                e.printStackTrace();
                return false;
        } catch (ParseException e) {
            if (e.isEmpty()) {
                changeStatus(DataStatus.FILE_IS_EMPTY);
                return true;
            } else if (e.isBadData()) {
                changeStatus(DataStatus.PASSWORD_WRONG);
            }
            e.printStackTrace();
            return false;
        }

        changeStatus(DataStatus.OK);
        return true;
    }

    @Override
    public boolean saveHelper(FileOutputStream fos, List<Item> data) {
        try {
            CharSequence content = mParser.generate(data);
            mFileIO.save(mPassword.toString(), fos, content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Item> loadHelper(FileInputStream fis, CharSequence password) {
        try {
            CharSequence content = mFileIO.read(password.toString(), fis);
            return mParser.parse(content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
     }

    @Override
    public boolean save() {
        try {
            if (mStatus == DataStatus.OK || mStatus == DataStatus.FILE_IS_EMPTY) {
                CharSequence data = mParser.generate(mList);
                FileOutputStream stream = mContext.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                mFileIO.save(mPassword.toString(), stream, data);
                changeStatus(DataStatus.OK);
                return true;
            }
            return false;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "cannot write file");
            e.printStackTrace();
            return false;
        } catch (RWException e) {
            e.printStackTrace();
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete() {
        lock();
        return mContext.deleteFile(FILENAME);
    }

    @Override
    public DataStatus status() {
        return mStatus;
    }

    @Override
    public boolean changePassword(CharSequence oldPwd, CharSequence newPwd) {
        if (mPassword == null) {
            mPassword = oldPwd;
            return true;
        }

        if(mPassword.toString().equals(oldPwd.toString())) {
            mPassword = newPwd;
            return true;
        }

        return false;
    }

    @Override
    public int getItemsSize() {
        return mList.size();
    }

    @Override
    public void addItem(Item item) {
        if (!mList.contains(item)) {
            mList.add(item);
        }
    }

    @Override
    public void removeItem(Item item) {
        if (mList.contains(item)) {
            mList.remove(item);
        }
    }

    @Override
    public Item getItem(String key) {
        for (int i = 0; i < mList.size(); i++) {
            Item item = mList.get(i);
            if (item.getId().equals(key)) {
                return item;
            }
        }

        Log.e(TAG, "No item with key:" + key);
        return null;
    }

    @Override
    public void addListener(StatusListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeListener(StatusListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    private void changeStatus(DataStatus newStatus) {
        boolean changed = (mStatus != newStatus);
        mStatus = newStatus;
        if (changed) {
            for(StatusListener listener: mListeners) {
                listener.onStatusChanged(mStatus);
            }
        }
    }

    @Override
    public List<Item> getItems() {
        return mList;
    }

    public void setFilePath(String path) {
        mFilePath = path;
        lock();
    }

    private void clearListContent() {
        for(Item item: mList) {
            item.clear();
        }

        mList.clear();
    }
}
