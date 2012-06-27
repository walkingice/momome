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
import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.MomoApp;
import org.zeroxlab.momome.MomoModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class PrefMain extends PreferenceActivity implements Momo {

    OnPreferenceClickListener mDataActionListener;
    MomoModel mModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = MomoApp.getModel();

        addPreferencesFromResource(R.xml.pref_main);
        Preference exportPref = findPreference(KEY_EXPORT_DATA);
        Preference importPref = findPreference(KEY_IMPORT_DATA);
        Preference deletePref = findPreference(KEY_DELETE_DATA);
        Preference changePref = findPreference(KEY_CHANGE_PASSWORD);

        OnPreferenceClickListener listener = new DataActionListener();
        exportPref.setOnPreferenceClickListener(listener);
        importPref.setOnPreferenceClickListener(listener);
        deletePref.setOnPreferenceClickListener(listener);
        changePref.setOnPreferenceClickListener(listener);

        if (mModel.status() != DataStatus.OK
                || mModel.status() != DataStatus.FILE_IS_EMPTY) {

            exportPref.setEnabled(false);
            importPref.setEnabled(false);
            changePref.setEnabled(false);
        }
    }

    class DataActionListener implements OnPreferenceClickListener {
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals(KEY_EXPORT_DATA)) {
                Toast.makeText(PrefMain.this, "Export not implement yet",Toast.LENGTH_SHORT).show();
            } else if (preference.getKey().equals(KEY_IMPORT_DATA)) {
                Toast.makeText(PrefMain.this, "Import not implement yet",Toast.LENGTH_SHORT).show();
            } else if (preference.getKey().equals(KEY_DELETE_DATA)) {
                Toast.makeText(PrefMain.this, "Delete not implement yet",Toast.LENGTH_SHORT).show();
            } else if (preference.getKey().equals(KEY_CHANGE_PASSWORD)) {
                Toast.makeText(PrefMain.this, "Change password not implement yet",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PrefMain.this, "oooops",Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        }
    }
}
