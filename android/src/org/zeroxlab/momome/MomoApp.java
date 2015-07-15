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

import org.zeroxlab.momome.impl.ImplModel;
import org.zeroxlab.momome.test.DummyModel;
import android.app.Application;
import java.io.File;

public class MomoApp extends Application implements Momo {

    private static MomoModel sModel;

    public MomoApp() {
    }

    public static MomoModel getModel() {
        return sModel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkExternalStorage();
        sModel = new ImplModel(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void checkExternalStorage() {
        File dir = new File(EXTERNAL_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
}
