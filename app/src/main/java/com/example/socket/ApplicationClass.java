package com.example.socket;

import android.app.Application;

import java.util.ArrayList;

public class ApplicationClass extends Application {
    public static ArrayList<RowFiles> listFiles;
    public static String type;

    @Override
    public void onCreate() {
        super.onCreate();
        listFiles = new ArrayList<>(0);
        type="";
    }

}
