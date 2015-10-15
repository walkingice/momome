package org.zeroxlab.momome.test;

import android.test.AndroidTestCase;
import android.util.Log;

public class UtilTest extends AndroidTestCase {

    public UtilTest() {
    }

    @Override
    protected void setUp() {
        Log.d("Momome", "set up");
    }

    @Override
    protected void tearDown() {
        Log.d("Momome", "teardown");
    }

    public void testFoo() {
        Log.d("Momome", "test foo");
    }
}
