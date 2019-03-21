package com.malin.client;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

/**
 * 可以将app/libs下的aidl文件拷贝到app/src/main/aidl/com/malin/client/目录下,
 * 同时将该目录下的java文件全部删除
 * 使用编译过程中自动生成的java文件
 * <p>
 * 客户端的AIDLActivity.java
 * https://blog.csdn.net/luoyanglizi/article/details/51980630
 * <p>
 * 首先建立连接，然后在 ServiceConnection 里面获取 BookManager 对象，接着通过它来调用服务端的方法
 */
@SuppressLint("SetTextI18n")
public class ClientActivity extends AppCompatActivity {

    private static final String TAG = "AIClient";
    //由AIDL文件生成的Java类
    private BookManager mBookManager = null;

    //标志当前与服务端连接状况的布尔值，false为未连接，true为连接中
    private boolean mBound = false;

    //包含Book对象的list
    private List<Book> mBooks;
    private TextView mTVContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        mTVContent = findViewById(R.id.tv_content);
    }

    /**
     * 按钮的点击事件，点击之后调用服务端的addBookIn方法
     *
     * @param view
     */
    public void addBook(View view) {
        //如果与服务端的连接处于未连接状态，则尝试连接
        if (!mBound) {
            attemptToBindService();
            mTVContent.setText("当前与服务端处于未连接状态，正在尝试重连，请稍后再试");
            return;
        }
        if (mBookManager == null) return;

        Book book = new Book();
        book.setName("From Client Book");
        book.setPrice(30);
        try {
            mBookManager.addBook(book);
            Log.e(TAG, book.toString());
            mTVContent.setText(book.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 尝试与服务端建立连接
     */
    private void attemptToBindService() {
        Intent intent = new Intent();
        intent.setAction("com.malin.aidl");
        intent.setPackage("com.malin.server");
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            attemptToBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    /**
     * 这是实现客户端与服务端通信的一个关键类。
     * 要想实现它，就必须重写两个回调方法：onServiceConnected()以及onServiceDisconnected()，
     * 而我们可以通过这两个回调方法得到服务端里面的IBinder对象，从而达到通信的目的
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            //iBinder的理解：BookManager.Stub.asInterface(iBinder)-->new com.malin.client.BookManager.Stub.Proxy(iBinder)-->IBinder mRemote--> mRemote.transact
            // 通过用它调用的 transact() 方法，我们得以将客户端的数据和请求发送到服务端去。
            // 从这个角度来看，这个 service 就像是服务端在客户端的代理一样——你想要找服务端？要传数据过去？行啊！你来找我，我给你把数据送过去——而
            // BookManager.java 中的那个 Proxy 类，就只能沦为二级代理了，我们在外部通过它来调动 service 对象。

            Log.e(TAG, "service connected");
            mTVContent.setText("service connected");
            mBookManager = BookManager.Stub.asInterface(iBinder);
            mBound = true;
            if (mBookManager != null) {
                try {
                    mBooks = mBookManager.getBooks();
                    Log.e(TAG, mBooks.toString());
                    mTVContent.setText(mBooks.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "service disconnected");
            mTVContent.setText("service disconnected");
            mBound = false;
        }
    };
}
