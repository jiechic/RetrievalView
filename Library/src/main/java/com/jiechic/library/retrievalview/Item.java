package com.jiechic.library.retrievalview;

import android.support.annotation.NonNull;

/**
 * Created by <a href="http://www.jiechic.com" target="_blank">jiechic</a> on 15/7/29.
 */
public class Item {
    private String key = "";
    private String Id = "";
    private String Name = "";
    private Boolean Selected = false;

    public Item(@NonNull String _id, @NonNull String _Name) {
        this.Id = _id;
        this.Name = _Name;
    }


    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    protected Boolean isSelected() {
        return Selected;
    }

    protected void setSelected(Boolean isSelected) {
        this.Selected = isSelected;
    }

    protected void setKey(String key) {
        this.key=key;
    }
    public String getKey(){
        return key;
    }
}
