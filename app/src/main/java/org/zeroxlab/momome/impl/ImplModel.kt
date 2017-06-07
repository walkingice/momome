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

import android.content.Context
import android.util.Log
import org.zeroxlab.momome.FileIO
import org.zeroxlab.momome.FileIO.RWException
import org.zeroxlab.momome.Momo
import org.zeroxlab.momome.MomoModel
import org.zeroxlab.momome.Parser
import org.zeroxlab.momome.Parser.ParseException
import org.zeroxlab.momome.data.Item
import org.zeroxlab.momome.data.JSONParser
import org.zeroxlab.momome.data.PasswordIO
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ImplModel(protected var mContext: Context) : MomoModel, Momo {
    protected var mListeners: MutableList<MomoModel.StatusListener>
    protected var mList: MutableList<Item>
    protected var mParser: Parser
    protected var mFileIO: FileIO
    protected var mPassword: CharSequence? = null

    protected var mFilePath = Momo.EXTERNAL_DIR + "/encrypted"
    var mStatus: Momo.DataStatus = Momo.DataStatus.DATA_LOCKING

    init {
        mListeners = ArrayList<MomoModel.StatusListener>()
        mList = ArrayList<Item>()
        mParser = JSONParser()
        mFileIO = PasswordIO()

        lock()
    }

    override fun internalFileExists(): Boolean {
        try {
            // no specified path, it refers to Internal storage
            val stream = mContext.openFileInput(Momo.FILENAME)
            return true
        } catch (e: FileNotFoundException) {
            return false
        }

    }

    override fun lock() {
        clearListContent()
        mPassword = null
        changeStatus(Momo.DataStatus.NO_PASSWORD)
    }

    override fun unlock(password: CharSequence): Boolean {
        mPassword = password

        try {
            // no specified path, it refers to Internal storage
            val stream = mContext.openFileInput(Momo.FILENAME)
            val data = mFileIO.read(mPassword!!.toString(), stream)
            mList = mParser.parse(data)
        } catch (e: FileNotFoundException) {
            changeStatus(Momo.DataStatus.FILE_IS_EMPTY)
            return true
        } catch (e: IOException) {
            changeStatus(Momo.DataStatus.FILE_CANNOT_ACCESS)
            clearListContent()
            e.printStackTrace()
            return false
        } catch (e: RWException) {
            changeStatus(Momo.DataStatus.FILE_CANNOT_ACCESS)
            clearListContent()
            e.printStackTrace()
            return false
        } catch (e: ParseException) {
            if (e.isEmpty) {
                changeStatus(Momo.DataStatus.FILE_IS_EMPTY)
                return true
            } else if (e.isBadData) {
                changeStatus(Momo.DataStatus.PASSWORD_WRONG)
            }
            e.printStackTrace()
            return false
        }

        changeStatus(Momo.DataStatus.OK)
        return true
    }

    override fun saveHelper(fos: FileOutputStream, data: List<Item>): Boolean {
        try {
            val content = mParser.generate(data)
            mFileIO.save(mPassword!!.toString(), fos, content)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    override fun loadHelper(fis: FileInputStream, password: CharSequence): List<Item>? {
        try {
            val content = mFileIO.read(password.toString(), fis)
            return mParser.parse(content)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    override fun save(): Boolean {
        try {
            if (mStatus == Momo.DataStatus.OK || mStatus == Momo.DataStatus.FILE_IS_EMPTY) {
                val data = mParser.generate(mList)
                val stream = mContext.openFileOutput(Momo.FILENAME, Context.MODE_PRIVATE)
                mFileIO.save(mPassword!!.toString(), stream, data)
                changeStatus(Momo.DataStatus.OK)
                return true
            }
            return false
        } catch (e: FileNotFoundException) {
            Log.e(Momo.TAG, "cannot write file")
            e.printStackTrace()
            return false
        } catch (e: RWException) {
            e.printStackTrace()
            return false
        } catch (e: ParseException) {
            e.printStackTrace()
            return false
        }

    }

    override fun delete(): Boolean {
        lock()
        return mContext.deleteFile(Momo.FILENAME)
    }

    override fun status(): Momo.DataStatus {
        return mStatus
    }

    override fun changePassword(oldPwd: CharSequence, newPwd: CharSequence): Boolean {
        if (mPassword == null) {
            mPassword = oldPwd
            return true
        }

        if (mPassword!!.toString() == oldPwd.toString()) {
            mPassword = newPwd
            return true
        }

        return false
    }

    override fun getItemsSize(): Int {
        return mList.size
    }

    override fun addItem(item: Item) {
        if (!mList.contains(item)) {
            mList.add(item)
        }
    }

    override fun removeItem(item: Item) {
        if (mList.contains(item)) {
            mList.remove(item)
        }
    }

    override fun getItem(key: String): Item? {
        for (i in mList.indices) {
            val item = mList[i]
            if (item.id == key) {
                return item
            }
        }

        Log.e(Momo.TAG, "No item with key:" + key)
        return null
    }

    override fun addListener(listener: MomoModel.StatusListener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener)
        }
    }

    override fun removeListener(listener: MomoModel.StatusListener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener)
        }
    }

    private fun changeStatus(newStatus: Momo.DataStatus) {
        val changed = mStatus != newStatus
        mStatus = newStatus
        if (changed) {
            for (listener in mListeners) {
                listener.onStatusChanged(mStatus)
            }
        }
    }

    override fun getItems(): List<Item> {
        return mList
    }

    fun setFilePath(path: String) {
        mFilePath = path
        lock()
    }

    private fun clearListContent() {
        for (item in mList) {
            item.clear()
        }

        mList.clear()
    }
}
