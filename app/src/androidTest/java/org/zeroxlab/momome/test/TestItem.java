package org.zeroxlab.momome.test;

import android.test.AndroidTestCase;

import org.zeroxlab.momome.data.Item;

public class TestItem extends AndroidTestCase {

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
}
