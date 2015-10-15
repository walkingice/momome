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

package org.zeroxlab.momome;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public interface FileIO {
    public boolean save(String key,
            FileOutputStream stream,
            CharSequence content) throws RWException;

    public CharSequence read(String key,
            FileInputStream stream) throws RWException;

    public boolean save(String key,
            String outputPath,
            CharSequence content) throws RWException;

    public CharSequence read(String key,
            String inputPath) throws RWException;

    public class RWException extends Exception {

        public RWException(String msg) {
            super(msg);
        }

        public RWException(String msg, Throwable throwable) {
            super(msg, throwable);
        }

        public RWException(Throwable throwable) {
            super(throwable);
        }
    }
}
