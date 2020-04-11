package com.example.socket;

import android.net.Uri;

public class RowFiles {
    public String name,size,midsize;
    public Uri uri;

    public RowFiles(String name, String size,String midsize,Uri uri) {
        this.name = name;
        this.size = size;
        this.uri = uri;
        this.midsize = midsize;
    }


    public Uri getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getMidsize() {
        return midsize;
    }

    public void setMidsize(String midsize) {
        this.midsize = midsize;
    }
}
