package com.ryg.chapter_2;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import com.ryg.chapter_2.binderpool.BinderPool;
import com.ryg.chapter_2.utils.MyUtils;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = MyUtils.getProcessName(getApplicationContext(), Process.myPid());
        Log.d(TAG, "application start, process name:" + processName);
        new Thread(this::doWorkInBackground).start();
    }

    private void doWorkInBackground() {
        // init binder pool
        BinderPool.getInstance(getApplicationContext());
    }
}
