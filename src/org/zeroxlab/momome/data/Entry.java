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

import org.zeroxlab.momome.Momo;
import org.zeroxlab.momome.util.Util;
import java.util.ArrayList;
import java.util.List;

public class Entry implements Momo, Comparable<Entry> {

    String mData;
    String mComment;
    long   mTime;

    public Entry(String data, String comment) {
        update(data, comment);
    }

    public void update(String data, String comment) {
        mData = data;
        mComment = comment;
        mTime = System.currentTimeMillis();
    }

    public void setLastModifiedTime(long time) {
        mTime = time;
    }

    public long getLastModifiedTime() {
        return mTime;
    }

    public String getData() {
        return mData;
    }

    public String getComment() {
        return mComment;
    }

    public void clear() {
        mData= "";
        mComment= "";
    }

    @Override
    public int compareTo(Entry another) {
        int dataCompare = getData().compareTo(another.getData());
        if (dataCompare == 0) {
            return getComment().compareTo(another.getComment());
        } else {
            return dataCompare;
        }
    }
}
