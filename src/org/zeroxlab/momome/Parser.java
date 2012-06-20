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

import org.zeroxlab.momome.data.Item;
import java.util.List;

public interface Parser {
    public List<Item> parse(CharSequence content) throws ParseException;
    public CharSequence generate(List<Item> items) throws ParseException;

    public class ParseException extends Exception {

        State mState = State.OTHER;

        public enum State {
            OTHER,
            BAD_DATA,
            EMPTY,
            WRONG_FORMAT
        }

        public ParseException(String msg) {
            this(msg, State.OTHER);
        }

        public ParseException(String msg, Throwable throwable) {
            this(msg, throwable, State.OTHER);
        }

        public ParseException(Throwable throwable) {
            this(throwable, State.OTHER);
        }

        public ParseException(String msg, State state) {
            super(msg);
            mState = state;
        }

        public ParseException(String msg, Throwable throwable, State state) {
            super(msg, throwable);
            mState = state;
        }

        public ParseException(Throwable throwable, State state) {
            super(throwable);
            mState = state;
        }

        public boolean isBadData() {
            return (mState == State.BAD_DATA);
        }

        public boolean isEmpty() {
            return (mState == State.EMPTY);
        }

        public boolean isWrongFormat() {
            return (mState == State.WRONG_FORMAT);
        }
    }
}
