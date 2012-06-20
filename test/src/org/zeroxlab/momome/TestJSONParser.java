package org.zeroxlab.momome;

import org.zeroxlab.momome.*;
import org.zeroxlab.momome.data.*;
import org.zeroxlab.momome.data.Item.ItemEntry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;
import android.content.res.AssetManager;
import android.util.Log;
import java.util.*;

public class TestJSONParser extends AndroidTestCase implements Momo {

    private JSONParser mParser;

    private static String sTitle = "item_title";
    private Item mItem;
    private ItemEntry mEntry1;
    private ItemEntry mEntry2;

    private List<Item> mList;

    private CharSequence mData;

    public TestJSONParser() {
        mParser = new JSONParser();

        mItem = new Item(sTitle);
        mEntry1 = new Item.ItemEntry("name1", "content1");
        mEntry2 = new Item.ItemEntry("name2", "content2");
        mItem.addEntry(mEntry1);
        mItem.addEntry(mEntry2);
        mList = new ArrayList<Item>();
        mList.add(mItem);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
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

    public void testParse() {
        try {
            mData = mParser.generate(mList);
            List<Item> items = mParser.parse(mData);
            assertEquals(1, items.size());

            Item item = items.get(0);
            assertEquals(2, item.getEntryCount());

            ItemEntry entry1 = item.getEntry(0);
            ItemEntry entry2 = item.getEntry(1);

            assertTrue(entry1.getName().equals("name1")
                    || entry2.getName().equals("name1"));
            assertTrue(entry1.getName().equals("name2")
                    || entry2.getName().equals("name2"));
            assertTrue(entry1.getContent().equals("content1")
                    || entry2.getContent().equals("content1"));
            assertTrue(entry1.getContent().equals("content2")
                    || entry2.getContent().equals("content2"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
