package com.example.socket;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

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
