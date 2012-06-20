package org.zeroxlab.momome;

import org.zeroxlab.momome.impl.*;
import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class org.zeroxlab.momome.TestMainActivity \
 * org.zeroxlab.momome.tests/android.test.InstrumentationTestRunner
 */
public class TestMainActivity extends ActivityInstrumentationTestCase2<MainActivity> {

    public TestMainActivity() {
        super("org.zeroxlab.momome", MainActivity.class);
    }

}
