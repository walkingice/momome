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

public class TestItem extends AndroidTestCase implements Momo {

    private static String sTitle= "TEST-Title";

    private Item mItem;
    private Item mNoTitleItem;

    public TestItem() {
        mItem = new Item(sTitle);
        mNoTitleItem = new Item();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTitle() {
        assertNotNull(mItem.getTitle());
        assertNotNull(mNoTitleItem.getTitle());
        assertEquals(sTitle, mItem.getTitle());
        assertEquals(Item.DEF_TITLE, mNoTitleItem.getTitle());

        String blah = "blahblah";
        mItem.setTitle(blah);
        assertEquals(blah, mItem.getTitle());
    }

    public void testEntry() {
        mItem.addEntry("one", "foo");
        mItem.addEntry("two", "foo");
        mItem.addEntry("three", "foo");
        assertEquals(3, mItem.getEntryCount());

        Item.ItemEntry entry = mItem.getEntry(0); // it should be 'one'
        mItem.updateEntry(entry, "two", "blah"); // modify the item

        Item.ItemEntry modified = mItem.getEntry(0); // 'one' turns into 'two'
        assertEquals("two", modified.getData());
        assertEquals(3, mItem.getEntryCount());

        ItemEntry onemore = new ItemEntry("XD", "XD");
        mItem.updateEntry(onemore, "xd", "xd");
        assertEquals(4, mItem.getEntryCount());
        mItem.updateEntry(onemore, "xd", "xd");
        assertEquals(4, mItem.getEntryCount());
        mItem.addEntry(onemore);
        assertEquals(4, mItem.getEntryCount());

        ItemEntry another = new ItemEntry("XD", "XD");
        mItem.addEntry(another);
        assertEquals(5, mItem.getEntryCount());
    }
}
