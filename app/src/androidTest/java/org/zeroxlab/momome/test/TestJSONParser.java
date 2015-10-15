package org.zeroxlab.momome.test;

import android.test.AndroidTestCase;
import android.util.Log;

import org.json.JSONObject;
import org.zeroxlab.momome.Parser.ParseException;
import org.zeroxlab.momome.data.Item;
import org.zeroxlab.momome.data.JSONParser;

import java.util.ArrayList;
import java.util.List;

public class TestJSONParser extends AndroidTestCase {

    public final static String TAG = "Momome";
    private JSONParser mParser;

    private static String sTitle = "item_title";
    private Item mItem;

    private List<Item> mList;

    private CharSequence mData;

    public TestJSONParser() {
        mParser = new JSONParser();

        mItem = new Item(sTitle);
        mList = new ArrayList<>();
        mList.add(mItem);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testEmpty() {
        try {
            mParser.parse(null);
            fail("Parser does not throw exception even if it got null data");
        } catch (ParseException e){
            assertTrue(e.isEmpty());
        }

        try {
            mParser.parse("");
            fail("Parser does not throw exception even if it got empty data");
        } catch (ParseException e){
            assertTrue(e.isEmpty());
        }
    }

    public void testGenerate() {
        try {
            mData = mParser.generate(mList);
            assertNotNull(mData);
            assertFalse(mData.length() == 0);

            JSONObject json = new JSONObject(mData.toString());
            Log.d(TAG, json.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
