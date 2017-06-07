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
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import org.zeroxlab.momome.Momo
import org.zeroxlab.momome.MomoApp
import org.zeroxlab.momome.MomoModel
import org.zeroxlab.momome.R
import org.zeroxlab.momome.data.Entry
import org.zeroxlab.momome.data.Item
import org.zeroxlab.momome.widget.BasicInputDialog
import org.zeroxlab.momome.widget.EditableActivity
import org.zeroxlab.momome.widget.EditableAdapter
import org.zeroxlab.momome.widget.EntryAdapter

class EntryActivity : EditableActivity(), Momo {
    lateinit var mModel: MomoModel
    lateinit var mContainer: ListView
    lateinit var mTitle: TextView
    lateinit var mLTSwitcher: ViewSwitcher // left-top
    lateinit var mRTSwitcher: ViewSwitcher // right-top
    lateinit var mItem: Item
    lateinit var mInflater: LayoutInflater
    lateinit var mAdapter: EntryAdapter
    lateinit var mEntryClickListener: EntryClickListener
    lateinit var mDialogListener: DialogListener

    private val DIALOG_DATA = 0x1000
    private val DIALOG_COMMENT = 0x1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        mInflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mModel = MomoApp.getModel()

        mEntryClickListener = EntryClickListener()
        mDialogListener = DialogListener()
        val key = intent.getStringExtra(Momo.CROSS_ITEM_KEY)
        mItem = mModel.getItem(key)

        if (initViews(key)) {
            mTitle.text = mItem.title
            mAdapter = EntryAdapter(this, mItem)
            mAdapter.setListener(EditListener())
            mContainer.adapter = mAdapter
            mContainer.onItemClickListener = mEntryClickListener
        }
    }

    private fun initViews(key: String?): Boolean {
        if (key == null || key == "" || mModel.status() != Momo.DataStatus.OK) {
            val msg = "Not valid key"
            Log.e(Momo.TAG, msg)
            val t = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
            t.show()
            finish()
            return false
        }

        mTitle = findViewById(R.id.entry_title) as TextView
        mContainer = findViewById(R.id.entry_container) as ListView
        mRTSwitcher = findViewById(R.id.entry_rt_switcher) as ViewSwitcher
        mLTSwitcher = findViewById(R.id.entry_lt_switcher) as ViewSwitcher
        return true
    }

    fun onClickEditButton(v: View) {
        super.toggleEditing()
    }

    fun onClickDoneButton(v: View) {
        super.toggleEditing()
    }

    fun onClickAddButton(v: View) {
        val entry = Entry(Entry.DEF_NAME, "")
        mItem.addEntry(entry)
        mAdapter.notifyDataSetChanged()
        askData(entry)
    }

    override fun onStartEdit() {
        mAdapter.setEditing(true)
        mLTSwitcher.showNext()
        mRTSwitcher.showNext()
        mContainer.onItemClickListener = null
        mContainer.invalidateViews()
    }

    override fun onStopEdit() {
        mAdapter.setEditing(false)
        mLTSwitcher.showNext()
        mRTSwitcher.showNext()
        mContainer.onItemClickListener = mEntryClickListener
        mContainer.invalidateViews()
        finishEditing()
    }

    private fun finishEditing() {
        mAdapter.notifyDataSetChanged()
        mModel.save()
    }

    private fun askData(entry: Entry) {
        val dialog = BasicInputDialog(this, "Edit data")
        dialog.setListener(DIALOG_DATA, mDialogListener)
        dialog.setExtra(entry)
        dialog.setDefaultText(entry.data)
        dialog.show()
    }

    private fun askComment(entry: Entry) {
        val dialog = BasicInputDialog(this, "Edit comment")
        dialog.setListener(DIALOG_COMMENT, mDialogListener)
        dialog.setExtra(entry)
        dialog.setDefaultText(entry.comment)
        dialog.show()
    }


    inner class DialogListener : BasicInputDialog.InputListener {
        override fun onInput(id: Int, input: CharSequence, extra: Any) {
            val entry = extra as Entry
            if (id == DIALOG_DATA) {
                mItem.updateEntry(entry, input.toString(), entry.comment)
                askComment(entry)
            } else if (id == DIALOG_COMMENT) {
                mItem.updateEntry(entry, entry.data, input.toString())
            }

            mAdapter.notifyDataSetChanged()
        }

        override fun onCancelInput(id: Int, extra: Any) {
            if (id == DIALOG_DATA) {
                askComment(extra as Entry)
            }
            mAdapter.notifyDataSetChanged()
        }
    }

    internal inner class EditListener : EditableAdapter.EditListener<Entry> {
        override fun onEdit(entry: Entry) {
            askData(entry) // askData will call askComment
        }

        override fun onDelete(entry: Entry) {
            val success = mItem.removeEntry(entry)
            if (success) {
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    inner class EntryClickListener : OnItemClickListener {
        override fun onItemClick(a: AdapterView<*>, v: View, pos: Int, id: Long) {
            val intent = Intent(this@EntryActivity, DetailActivity::class.java)
            val entry = mAdapter.getItem(pos) as Entry
            intent.putExtra(Momo.CROSS_ENTRY_DATA_KEY, entry.data)
            intent.putExtra(Momo.CROSS_ENTRY_COMMENT_KEY, entry.comment)
            intent.putExtra(Momo.CROSS_ENTRY_TIME_KEY, entry.lastModifiedTime)
            startActivity(intent)
        }
    }
}
