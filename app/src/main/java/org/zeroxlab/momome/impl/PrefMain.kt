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

import org.zeroxlab.momome.R
import org.zeroxlab.momome.data.Item
import org.zeroxlab.momome.Momo
import org.zeroxlab.momome.MomoApp
import org.zeroxlab.momome.MomoModel
import org.zeroxlab.momome.widget.BasicInputDialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class PrefMain : PreferenceActivity(), Momo, MomoModel.StatusListener {

    internal var mDataActionListener: OnPreferenceClickListener? = null
    lateinit var mModel: MomoModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mModel = MomoApp.getModel()

        addPreferencesFromResource(R.xml.pref_main)
        val exportPref = findPreference(Momo.KEY_EXPORT_DATA)
        val importPref = findPreference(Momo.KEY_IMPORT_DATA)
        val deletePref = findPreference(Momo.KEY_DELETE_DATA)
        val changePref = findPreference(Momo.KEY_CHANGE_PASSWORD)
        val sendPref = findPreference(Momo.KEY_SEND_DATA)

        val listener = DataActionListener()
        exportPref.onPreferenceClickListener = listener
        importPref.onPreferenceClickListener = listener
        deletePref.onPreferenceClickListener = listener
        changePref.onPreferenceClickListener = listener
        sendPref.onPreferenceClickListener = listener

        updateVisibility()
    }

    override fun onStatusChanged(now: Momo.DataStatus) {
        updateVisibility()
    }

    public override fun onResume() {
        super.onResume()
        mModel.addListener(this)
    }

    public override fun onPause() {
        super.onPause()
        mModel.removeListener(this)
    }

    private fun askImportData() {
        val file = File(Momo.EXTERNAL_DIR, Momo.FILENAME)
        if (!file.exists()) {
            showMsg(super.getString(R.string.pref_dialog_import_data_missed) + file.path)
            return
        }

        val dialog = BasicInputDialog(this)
        dialog.setTitle(R.string.pref_import_ext)
        dialog.setMessage(super.getString(R.string.pref_dialog_import_data_msg) + file.path)
        dialog.setListener(0, object : BasicInputDialog.InputListener {
            override fun onInput(id: Int, input: CharSequence, extra: Any) {
                onImportData(input)
            }

            override fun onCancelInput(id: Int, extra: Any) {}
        })

        dialog.show()
    }

    private fun onImportData(password: CharSequence) {
        val input = File(Momo.EXTERNAL_DIR, Momo.FILENAME)
        if (!input.exists()) {
            showMsg(super.getString(R.string.pref_dialog_import_data_fail) + input.path)
            return
        }

        try {
            val fis = FileInputStream(input)
            val list = mModel.loadHelper(fis, password)
            if (list != null) {
                for (i in list.indices) {
                    mModel.addItem(list[i])
                }
                mModel.save()
                showMsg(super.getString(R.string.pref_dialog_done))
            } else {
                showMsg(super.getString(R.string.pref_dialog_import_data_fail))
            }
        } catch (e: Exception) {
            showMsg("Exception occurred!")
            e.printStackTrace()
        }

    }

    private fun askExportData() {
        val builder = AlertDialog.Builder(this)
        val file = File(Momo.EXTERNAL_DIR, Momo.FILENAME)
        builder.setIcon(android.R.drawable.ic_dialog_info)
        builder.setTitle(R.string.pref_export_ext)
        builder.setMessage(super.getString(R.string.pref_dialog_export_data_msg) + file.path)
        builder.setPositiveButton(android.R.string.yes
        ) { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                onExportData()
            }
        }
        builder.show()
    }

    private fun onExportData() {
        val items = mModel.items
        val output = File(Momo.EXTERNAL_DIR, Momo.FILENAME)
        if (!output.parentFile.canWrite()) {
            showMsg(super.getString(R.string.pref_dialog_export_data_cannot_write) + output.path)
            return
        }

        try {
            val fos = FileOutputStream(output)
            val success = mModel.saveHelper(fos, items)
            if (success) {
                showMsg(R.string.pref_dialog_done)
            } else {
                showMsg(R.string.pref_dialog_export_data_fail)
            }
        } catch (e: Exception) {
            showMsg("Exception occurred!")
            e.printStackTrace()
        }

    }

    private fun onSendData() {
        val attachment = File(Momo.EXTERNAL_DIR, Momo.FILENAME)

        if (!attachment.exists()) {
            showMsg(R.string.pref_dialog_send_data_missed)
            return
        }

        val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")
        val date = sdf.format(Date(System.currentTimeMillis()))

        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_SUBJECT, "MomoMe backup encrypted data")
        intent.putExtra(android.content.Intent.EXTRA_TEXT, date)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(attachment))
        intent.type = "plain/text"
        startActivity(Intent.createChooser(intent, "Mail Chooser"))
    }

    private fun askPassword() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_setpassword, null)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.pref_change_password)
        builder.setView(view)
        builder.setPositiveButton(android.R.string.yes
        ) { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                val t1 = view.findViewById(R.id.pwd_oldpwd) as TextView
                val t2 = view.findViewById(R.id.pwd_newpwd1) as TextView
                val t3 = view.findViewById(R.id.pwd_newpwd2) as TextView
                onChangePassword(t1.text, t2.text, t3.text)
            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.show()
    }

    private fun onChangePassword(old: CharSequence, new1: CharSequence, new2: CharSequence) {
        if (new1.toString() != new2.toString()) {
            showMsg(R.string.pref_dialog_pwd_mismatch)
            return
        }

        if (mModel.changePassword(old, new1)) {
            showMsg(R.string.pref_dialog_pwd_success)
            mModel.save()
        } else {
            showMsg(R.string.pref_dialog_pwd_incorrect)
        }
    }

    private fun askDeleteData() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.pref_delete_file)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setMessage(super.getString(R.string.pref_dialog_del_data_msg))
        builder.setPositiveButton(android.R.string.yes
        ) { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {
                checkDeleteData()
            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.show()
    }

    private fun checkDeleteData() {
        val answer = "" + ((Math.random() * 900).toInt() + 100) // 100 ~ 999
        val dialog = BasicInputDialog(this)
        dialog.setTitle(R.string.pref_dialog_del_check_title)
        dialog.setMessage(super.getString(R.string.pref_dialog_del_check_msg) + answer)
        dialog.setListener(0, object : BasicInputDialog.InputListener {
            override fun onInput(id: Int, input: CharSequence, extra: Any) {
                if (input.toString() == answer) {
                    doDeleteData()
                } else {
                    showMsg(R.string.pref_dialog_del_check_abort)
                }
            }

            override fun onCancelInput(id: Int, extra: Any) {}
        })

        dialog.show()
    }

    private fun doDeleteData() {
        if (mModel.delete()) {
            showMsg(R.string.pref_dialog_del_success)
        } else {
            showMsg(R.string.pref_dialog_del_fail)
        }
    }

    private fun updateVisibility() {
        val exportPref = findPreference(Momo.KEY_EXPORT_DATA)
        val importPref = findPreference(Momo.KEY_IMPORT_DATA)
        val changePref = findPreference(Momo.KEY_CHANGE_PASSWORD)
        val sendPref = findPreference(Momo.KEY_SEND_DATA)

        if (mModel.status() == Momo.DataStatus.OK) {
            exportPref.isEnabled = true
            importPref.isEnabled = true
            changePref.isEnabled = true
            sendPref.isEnabled = true
        } else {
            exportPref.isEnabled = false
            importPref.isEnabled = false
            changePref.isEnabled = false
            sendPref.isEnabled = false
        }

        if (mModel.status() == Momo.DataStatus.FILE_IS_EMPTY) {
            importPref.isEnabled = true
        }
    }

    private fun showMsg(id: Int) {
        this.showMsg(super.getString(id))
    }

    private fun showMsg(msg: CharSequence) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
        builder.setNeutralButton(android.R.string.ok, null)
        builder.show()
    }

    internal inner class DataActionListener : OnPreferenceClickListener {
        override fun onPreferenceClick(preference: Preference): Boolean {
            if (preference.key == Momo.KEY_EXPORT_DATA) {
                askExportData()
            } else if (preference.key == Momo.KEY_IMPORT_DATA) {
                askImportData()
            } else if (preference.key == Momo.KEY_DELETE_DATA) {
                askDeleteData()
            } else if (preference.key == Momo.KEY_CHANGE_PASSWORD) {
                askPassword()
            } else if (preference.key == Momo.KEY_SEND_DATA) {
                onSendData()
            } else {
                Toast.makeText(this@PrefMain, "oooops", Toast.LENGTH_SHORT).show()
                return false
            }

            return true
        }
    }
}
