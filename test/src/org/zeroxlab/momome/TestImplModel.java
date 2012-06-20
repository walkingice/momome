package org.zeroxlab.momome;

import org.zeroxlab.momome.*;
import org.zeroxlab.momome.impl.*;
import org.zeroxlab.momome.data.*;
import org.zeroxlab.momome.data.Item.ItemEntry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import java.io.*;
import java.util.*;

public class TestImplModel extends AndroidTestCase implements Momo {

    public final static String EXTERNAL_STORAGE
        = Environment.getExternalStorageDirectory().getPath();
    public final static String TEST_FILE = EXTERNAL_STORAGE + "/TestMomome.json";

    protected final static String PASSWORD = "TestPassword";
    protected ImplModel mModel;

    public TestImplModel() {
        mModel = new ImplModel();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initItems();
        mModel.setFilePath(TEST_FILE);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        removeTestFile();
        mModel.lock();
    }

    public void test_01_Unlock() {
        mModel.setFilePath("/this/abs/path/should/not/exist");
        assertFalse(mModel.unlock(PASSWORD));
        assertTrue(mModel.status() == DataStatus.FILE_CANNOT_ACCESS);

        mModel.setFilePath(TEST_FILE);
        removeTestFile();
        assertTrue(mModel.unlock(PASSWORD));
        assertTrue(mModel.status() == DataStatus.FILE_IS_EMPTY);

        removeTestFile();
        createTestFile();
        assertTrue(mModel.unlock(PASSWORD));
        assertTrue(mModel.status() == DataStatus.FILE_IS_EMPTY);
    }

    public void test_02_UnlockWithJunk() {
        putJunkToTestFile();
        assertFalse(mModel.unlock(PASSWORD));
        assertTrue(mModel.status() == DataStatus.PASSWORD_WRONG);
    }

    public void test_03_Lock() {
        assertTrue(mModel.unlock(PASSWORD));
        mModel.lock();
        assertTrue(mModel.status() == DataStatus.NO_PASSWORD);
    }

    public void test_04_save() {
        assertTrue(mModel.unlock(PASSWORD));
        initItems();
        assertTrue(mModel.save());
        mModel.lock();
        assertTrue(mModel.unlock(PASSWORD));
        assertTrue(mModel.status() == DataStatus.OK);
    }

    private void initItems() {
        Item itemA = new Item("titleA");
        itemA.addEntry("A-Entry-1", "A-content-1");
        itemA.addEntry("A-Entry-2", "A-content-2");
        itemA.addEntry("A-Entry-3", "A-content-3");

        Item itemB = new Item("titleB");
        itemB.addEntry("B-Entry-1", "B-content-1");
        itemB.addEntry("B-Entry-2", "B-content-2");

        mModel.addItem(itemA);
        mModel.addItem(itemB);
    }

    private void putJunkToTestFile() {
        try {
            File f = new File(TEST_FILE);
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write('a');
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void createTestFile() {
        try {
            File f = new File(TEST_FILE);
            f.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    private void removeTestFile() {
        try {
            File f = new File(TEST_FILE);
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
