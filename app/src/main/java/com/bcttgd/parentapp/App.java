package com.bcttgd.parentapp;

import android.content.Context;
import android.os.StrictMode;

public class App extends android.app.Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public static Context getContext(){
        return mContext;
    }
}
