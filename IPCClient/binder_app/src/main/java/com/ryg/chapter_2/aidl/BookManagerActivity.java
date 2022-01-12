package com.ryg.chapter_2.aidl;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ryg.chapter_2.R;

import java.util.List;

public class BookManagerActivity extends AppCompatActivity {

    private static final String TAG = "BookManagerActivity";
    private static final int MESSAGE_NEW_BOOK_ARRIVED = 1;

    private IBookManager mRemoteBookManager;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_NEW_BOOK_ARRIVED) {
                Log.d(TAG, "handleMessage# receive new book :" + msg.obj);
            } else {
                super.handleMessage(msg);
            }
        }
    };

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binder died. tname:" + Thread.currentThread().getName());
            if (mRemoteBookManager == null)
                return;
            mRemoteBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mRemoteBookManager = null;
            // TODO:这里重新绑定远程Service
        }
    };

    /**
     * 首先客户端要注册IOnNewBookArrivedListener到远程服务端，
     * 这样当有新书时服务端才能通知当前客户端，同时我们要在Activity退出时解除这个注册；
     * 另一方面，当有新书时，服务端会回调客户端的IOnNewBookArrivedListener对象中的onNewBookArrived方法，
     * 但是这个方法是在客户端的Binder线程池中执行的，
     * 因此，为了便于进行UI操作，我们需要有一个Handler可以将其切换到客户端的主线程中去执行，
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            IBookManager bookManager = IBookManager.Stub.asInterface(service);
            mRemoteBookManager = bookManager;
            try {
                mRemoteBookManager.asBinder().linkToDeath(mDeathRecipient, 0);

                List<Book> list = bookManager.getBookList();
                Log.i(TAG, "onServiceConnected# query book list, list type:" + list.getClass().getCanonicalName());
                Log.i(TAG, "onServiceConnected# query book list:" + list);

                Book newBook = new Book(4, "Android进阶");
                bookManager.addBook(newBook);

                Log.i(TAG, "onServiceConnected# add book:" + newBook);

                List<Book> newList = bookManager.getBookList();
                Log.i(TAG, "onServiceConnected# query book list:" + newList.toString());

                bookManager.registerListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mRemoteBookManager = null;
            Log.d(TAG, "onServiceDisconnected. tname:" + Thread.currentThread().getName());
        }
    };

    private final IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void onButton1Click(View view) {
        Toast.makeText(this, "click button1", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            if (mRemoteBookManager != null) {
                try {
                    List<Book> newList = mRemoteBookManager.getBookList();
                    Log.d(TAG, "onButton1Click Thread. newList:" + newList);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 从上面的代码可以看出，当BookManagerActivity关闭时，我们会在onDestroy中去解除已经注册到服务端的listener，
     * 这就相当于我们不想再接收图书馆的新书提醒了，所以我们可以随时取消这个提醒服务。
     * 按back键退出BookManagerActivity，下面是打印出的log。
     */
    @Override
    protected void onDestroy() {
        if (mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()) {
            try {
                Log.i(TAG, "unregister listener:" + mOnNewBookArrivedListener);
                mRemoteBookManager.unregisterListener(mOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
