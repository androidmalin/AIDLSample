package com.malin.client;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
 * <p>
 * IXXX是AIDL接口类型定义逻辑方法
 * XXXProxy是本地端代理对象，也是给应用操作的实际对象
 * XXXNative/XXX$Stub是远端服务的中间者对象，主要用来处理应用发送过来的命令处理工作
 * XXXService是最终的服务逻辑实现方法的地方，运行在远端进程中
 * https://blog.csdn.net/jiangwei0910410003/article/details/52549333
 * http://www.520monkey.com/archives/856
 */
@SuppressLint("SetTextI18n")
public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AIClient";

    //由AIDL文件生成的Java类
    private BookManager mBookManager = null;

    //标志当前与服务端连接状况的布尔值，false为未连接，true为连接中
    private boolean mBound = false;

    private TextView mTVContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        initView();
    }

    private void initView() {
        mTVContent = findViewById(R.id.tv_content);
        findViewById(R.id.btn_add_book).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_book) {
            addBook();
        }
    }

    /**
     * 按钮的点击事件，点击之后调用服务端的addBookIn方法
     */
    public void addBook() {
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
        //bindService()方法的第三个参数是一个int值，它是一个指示绑定选项的标志，通常应该是 BIND_AUTO_CREATE，以便创建尚未激活的服务
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private final IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binder died. Thread name:" + Thread.currentThread().getName());
            if (mBookManager == null) return;
            mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBookManager = null;
            // TODO:这里重新绑定远程Service
        }
    };

    /**
     * 这是实现客户端与服务端通信的一个关键类。
     * 要想实现它，就必须重写两个回调方法：onServiceConnected()以及onServiceDisconnected()，
     * 而我们可以通过这两个回调方法得到服务端里面的IBinder对象，从而达到通信的目的
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        /**
         * 系统会调用该方法以传递服务端的onBind() 方法返回的 IBinder。
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            //iBinder的理解：BookManager.Stub.asInterface(iBinder) iBinder为BinderProxy(Binder的内部类)-->new com.malin.client.BookManager.Stub.Proxy(iBinder)-->IBinder mRemote--> mRemote.transact
            //-->BinderProxy--> native boolean transactNative();-->
            //-->Client进程陷入内核态，Client调用getBooks()方法的线程挂起等待返回；
            //-->驱动完成一系列的操作之后唤醒Server进程，调用了Server进程本地对象的onTransact函数（实际上由Server端线程池完成)

            // 通过用它调用的 transact() 方法，我们得以将客户端的数据和请求发送到服务端去。
            // 从这个角度来看，这个 service 就像是服务端在客户端的代理一样——你想要找服务端？要传数据过去？行啊！你来找我，我给你把数据送过去——而
            // BookManager.java 中的那个 Proxy 类，就只能沦为二级代理了，我们在外部通过它来调动 service 对象。

            Log.e(TAG, "service connected");
            mTVContent.setText("service connected");
            //本地端的中间者
            mBookManager = BookManager.Stub.asInterface(iBinder);
            mBound = true;
            if (mBookManager == null) return;
            try {
                // 在客户端绑定远程服务成功后，给binder设置死亡代理：
                // 其中linkToDeath的第二个参数是个标记位，我们直接设为0即可
                // 当Binder死亡的时候我们就可以收到通知了。另外，通过Binder的方法isBinderAlive也可以判断Binder是否死亡。
                mBookManager.asBinder().linkToDeath(mDeathRecipient, 0);

                //包含Book对象的list
                List<Book> books = mBookManager.getBooks();
                Log.e(TAG, books.toString());
                mTVContent.setText(books.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * Android系统会在与服务的连接意外中断时（例如当服务崩溃或被终止时）调用该方法。
         * 当客户端取消绑定时，系统"绝对不会"调用该方法
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "service disconnected");
            mTVContent.setText("service disconnected");
            mBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }
}
