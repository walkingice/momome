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

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import android.widget.ViewSwitcher
import com.u1aryz.android.lib.newpopupmenu.PopupMenu
import org.zeroxlab.momome.Momo
import org.zeroxlab.momome.MomoApp
import org.zeroxlab.momome.MomoModel
import org.zeroxlab.momome.R
import org.zeroxlab.momome.data.Item
import org.zeroxlab.momome.widget.BasicInputDialog
import org.zeroxlab.momome.widget.EditableActivity
import org.zeroxlab.momome.widget.EditableAdapter
import org.zeroxlab.momome.widget.ItemAdapter

class MainActivity : EditableActivity(), Momo, EditableAdapter.EditListener<Item> {

    lateinit var mListView: ListView
    lateinit var mHint: View
    lateinit var mLTSwitcher: ViewSwitcher // left-top
    lateinit var mRTSwitcher: ViewSwitcher // right-top
    lateinit var mAdapter: ItemAdapter
    lateinit var mDialogListener: DialogListener
    lateinit var mStatusListener: MomoModel.StatusListener
    lateinit var mItemClickListener: ItemClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        initViews()

        mDialogListener = DialogListener()
        mStatusListener = StatusListener()
        val model = MomoApp.getModel()
        mAdapter = ItemAdapter(this)
        mAdapter.setListener(this)
        mListView.adapter = mAdapter

        mItemClickListener = ItemClickListener()
        mListView.onItemClickListener = mItemClickListener

        doReload()
    }

    override fun onDestroy() {
        super.onDestroy()
        onStopEdit()
        closeModel()
    }

    override fun onNewIntent(newIntent: Intent) {
        /* 1) singleTask launch mode
         * 2) clearTaskOnLaunch
         * when set both of them, onNewIntent meanse
         * re-enter this activity from HomeScreen or other
         * application. Locl model to protect data. */
        MomoApp.getModel().addListener(mStatusListener)
        closeModel()
    }

    public override fun onResume() {
        super.onResume()
        MomoApp.getModel().addListener(mStatusListener)
        updateVisibilityByStatus(MomoApp.getModel().status())
    }

    public override fun onPause() {
        super.onPause()
        MomoApp.getModel().removeListener(mStatusListener)
    }

    private fun initViews() {
        mListView = findViewById(R.id.main_list_view) as ListView
        mHint = findViewById(R.id.main_hint)
        mLTSwitcher = findViewById(R.id.main_lt_switcher) as ViewSwitcher
        mRTSwitcher = findViewById(R.id.main_rt_switcher) as ViewSwitcher
    }

    private fun launchEntryActivity(key: String) {
        val intent = Intent(this, EntryActivity::class.java)
        intent.putExtra(Momo.CROSS_ITEM_KEY, key)
        startActivity(intent)
    }

    private fun closeModel() {
        val model = MomoApp.getModel()
        if (model.status() == Momo.DataStatus.OK) {
            model.save()
            model.lock()
            mAdapter.notifyDataSetChanged()
        }
    }

    fun onClickAdd(v: View) {
        val model = MomoApp.getModel()
        if (model.status() == Momo.DataStatus.OK || model.status() == Momo.DataStatus.FILE_IS_EMPTY) {
            getNewItemName()
        }
    }

    fun onClickSettings(v: View) {
        val i = Intent(this, PrefMain::class.java)
        startActivity(i)
    }

    fun onClickEdit(v: View) {
        val model = MomoApp.getModel()
        if (model.status() == Momo.DataStatus.OK || model.status() == Momo.DataStatus.FILE_IS_EMPTY) {
            super.toggleEditing()
        }
    }

    fun onClickDone(v: View) {
        super.toggleEditing()
    }

    fun onClickMore(v: View) {
        val fLock = 0
        val fSettings = 1
        val menu = PopupMenu(this)
        menu.add(fLock, R.string.lockaction)
        menu.add(fSettings, R.string.settings)
        menu.setHeaderTitle("More options")
        menu.setOnItemSelectedListener { item ->
            if (item.itemId == fLock) {
                onClickReload(v)
            } else if (item.itemId == fSettings) {
                onClickSettings(v)
            }
        }
        menu.show(v)
    }

    fun onClickReload(v: View) {
        val model = MomoApp.getModel()
        if (model.status() == Momo.DataStatus.OK) {
            closeModel()
        } else {
            doReload()
        }
    }

    override fun onEdit(item: Item) {
        renameItem(item)
    }

    override fun onDelete(item: Item) {
        val model = MomoApp.getModel()
        model.removeItem(item)
        mAdapter.notifyDataSetChanged()
    }

    override fun onStartEdit() {
        mLTSwitcher.showNext()
        mRTSwitcher.showNext()
        mAdapter.setEditing(true)
        mListView.onItemClickListener = null
        mListView.invalidateViews()
    }

    override fun onStopEdit() {
        mLTSwitcher.showNext()
        mRTSwitcher.showNext()
        mAdapter.setEditing(false)
        mListView.onItemClickListener = mItemClickListener
        mListView.invalidateViews()
    }

    private fun doReload() {
        val model = MomoApp.getModel()
        if (model.status() == Momo.DataStatus.NO_PASSWORD
                || model.status() == Momo.DataStatus.FILE_CANNOT_ACCESS
                || model.status() == Momo.DataStatus.PASSWORD_WRONG) {
            askForPassword()
        }
    }

    private fun askForPassword() {
        var msg = super.getString(R.string.main_dialog_unlock)
        if (!MomoApp.getModel().internalFileExists()) {
            Log.d(Momo.TAG, "not exist?")
            msg = super.getString(R.string.main_dialog_init)
        }

        val dialog = BasicInputDialog(this, msg)
        dialog.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
        dialog.setListener(DIALOG_PASSWORD, mDialogListener)
        dialog.show()
    }

    private fun renameItem(item: Item) {
        val dialog = BasicInputDialog(this,
                super.getString(R.string.main_dialog_rename_item))
        dialog.setDefaultText(item.title)
        dialog.setExtra(item)
        dialog.setListener(DIALOG_RENAME, mDialogListener)
        dialog.show()
    }

    private fun getNewItemName() {
        val dialog = BasicInputDialog(this,
                super.getString(R.string.main_dialog_add_item))
        dialog.setListener(DIALOG_ADD_ITEM, mDialogListener)
        dialog.show()
    }

    private fun onEnteredPassword(password: CharSequence) {
        val model = MomoApp.getModel()
        model.unlock(password)
        /* FIXME: it should detect PASSWORD_WRONG ONLY*/
        if (model.status() == Momo.DataStatus.PASSWORD_WRONG || model.status() == Momo.DataStatus.FILE_CANNOT_ACCESS) {

            Toast.makeText(this, "Decrypt failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun onRenameItem(name: CharSequence, item: Item) {
        item.title = name.toString()
        MomoApp.getModel().save()
        mAdapter.notifyDataSetChanged()
    }

    private fun onAddItem(name: CharSequence) {
        val item = Item(name.toString())
        MomoApp.getModel().addItem(item)
        MomoApp.getModel().save()
        mAdapter.notifyDataSetChanged()
    }

    private fun updateVisibilityByStatus(status: Momo.DataStatus) {
        if (status == Momo.DataStatus.OK || status == Momo.DataStatus.FILE_IS_EMPTY) {
            mAdapter.notifyDataSetChanged()
            mHint.visibility = View.GONE
            mListView.visibility = View.VISIBLE
        } else {
            mHint.visibility = View.VISIBLE
            mListView.visibility = View.GONE
        }
    }

    inner class ItemClickListener : OnItemClickListener {
        override fun onItemClick(a: AdapterView<*>, v: View, pos: Int, id: Long) {
            val item = mAdapter.getItem(pos) as Item
            launchEntryActivity(item.id)
        }
    }

    inner class DialogListener : BasicInputDialog.InputListener {
        override fun onInput(id: Int, input: CharSequence, extra: Any?) {
            if (input.toString() == "") {
                return  // do nothing if user input nothing
            } else if (id == DIALOG_PASSWORD) {
                onEnteredPassword(input)
            } else if (id == DIALOG_RENAME) {
                if (extra != null && extra is Item) {
                    onRenameItem(input, (extra as Item?)!!)
                }
            } else if (id == DIALOG_ADD_ITEM) {
                onAddItem(input)
            }
        }

        override fun onCancelInput(id: Int, extra: Any) {}
    }

    private inner class StatusListener : MomoModel.StatusListener {
        override fun onStatusChanged(now: Momo.DataStatus) {
            val model = MomoApp.getModel()
            updateVisibilityByStatus(model.status())
        }
    }

    companion object {

        private val DIALOG_PASSWORD = 0xFF01
        private val DIALOG_RENAME = 0xFF02
        private val DIALOG_ADD_ITEM = 0xFF03
    }
}
