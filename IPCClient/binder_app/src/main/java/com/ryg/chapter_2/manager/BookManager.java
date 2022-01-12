package com.ryg.chapter_2.manager;

import android.os.IBinder;

import com.ryg.chapter_2.aidl.IBookManager;

public class BookManager {

    private IBookManager mBookManager;

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (mBookManager == null)
                return;
            mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBookManager = null;
            // TODO:这里重新绑定远程Service
        }
    };
}
