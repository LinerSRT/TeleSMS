package com.liner.telesms;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.liner.telesms.utils.PM;

@SuppressLint("StaticFieldLeak")
public class Core extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        PM.init(context);
    }

    public static Context getContext() {
        return context;
    }
}
