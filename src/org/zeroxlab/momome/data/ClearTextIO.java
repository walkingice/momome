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

package org.zeroxlab.momome.data;

import org.zeroxlab.momome.FileIO;
import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.util.Util;

import android.util.Log;
import java.io.IOException;

public class ClearTextIO implements FileIO, Momo {
    @Override
    public boolean save(String key, String outputPath, CharSequence content) {
        try {
            Util.writeStrToFile(outputPath, content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public CharSequence read(String key, String inputPath) {
        CharSequence data = null;

        try {
            data = Util.readStrFromFile(inputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
