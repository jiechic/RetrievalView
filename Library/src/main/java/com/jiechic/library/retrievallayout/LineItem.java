package com.jiechic.library.retrievallayout;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/7/29.
 */
public class LineItem {
    public static final String DEFAULTID = "0x00000000";

    private String key;
    private List<Item> list;
    private Item defaultItem = null;

    public LineItem(@NonNull String key, @NonNull String notSelectString) {
        list = new ArrayList<>();
        this.key = key;
        defaultItem = new Item(DEFAULTID, notSelectString);
        defaultItem.setKey(this.key);
        list.add(defaultItem);
    }

    public LineItem(@NonNull String key) {
        list = new ArrayList<>();
        this.key = key;
    }

    public void addItem(Item item) {
        item.setKey(key);
        list.add(item);
    }


    public void clear() {
        list.clear();
        if (defaultItem != null) {
            defaultItem.setSelected(true);
            list.add(defaultItem);
        }
    }

    protected String getKey() {
        return key;
    }

    protected List<Item> getList() {
        return list;
    }
}
