package com.ryg.chapter_2.messenger;

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
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ryg.chapter_2.R;
import com.ryg.chapter_2.utils.MyConstants;

/**
 * 使用Messenger来进行进程间通信的方法，可以发现，
 * Messenger是以串行的方式处理客户端发来的消息，
 * 如果大量的消息同时发送到服务端，服务端仍然只能一个个处理，如果有大量的并发请求，
 * 那么用Messenger就不太合适了。
 * 同时，Messenger的作用主要是为了传递消息，很多时候我们可能需要跨进程调用服务端的方法，
 * 这种情形用Messenger就无法做到了，但是我们可以使用AIDL来实现跨进程的方法调用。
 * AIDL也是Messenger的底层实现，因此Messenger本质上也是AIDL，
 * 只不过系统为我们做了封装从而方便上层的调用而已。
 * 这里先介绍使用AIDL来进行进程间通信的流程，分为服务端和客户端两个方面。
 * <p>
 * Android开发艺术探索
 * <p>
 * {@link MessengerService}
 */
@SuppressLint("SetTextI18n")
public class MessengerActivity extends AppCompatActivity {

    private static final String TAG = "MessengerActivity";

    private final Messenger mGetReplyMessenger = new Messenger(new MessengerHandler());

    @SuppressLint("HandlerLeak")
    private class MessengerHandler extends Handler {
        public MessengerHandler() {
            super(Looper.myLooper());
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MyConstants.MSG_FROM_SERVICE) {
                Log.i(TAG, "receive msg from Service:" + msg.getData().getString("reply"));
                mTextView.setText("从服务端收到信息 " + msg.getData().getString("reply"));
            } else {
                super.handleMessage(msg);
            }
        }
    }

    /**
     * 2.客户端进程
     * <p>
     * 客户端进程中，首先要绑定服务端的Service，绑定成功后用服务端返回的IBinder对象创建一个Messenger，
     * 通过这个Messenger就可以向服务端发送消息了，发消息类型为Message对象。
     * 如果需要服务端能够回应客户端，就和服务端一样，我们还需要创建一个Handler并创建一个新的Messenger，
     * 并把这个Messenger对象通过Message的replyTo参数传递给服务端，服务端通过这个replyTo参数就可以回应客户端。
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mTextView.setText("onServiceConnected");

            // mBookManager = BookManager.Stub.asInterface(iBinder);本地端的中间者
            Messenger messenger = new Messenger(service);
            Log.d(TAG, "bind service");
            Message msg = Message.obtain(null, MyConstants.MSG_FROM_CLIENT);
            Bundle data = new Bundle();
            data.putString("msg", "hello, this is client.");
            msg.setData(data);
            msg.replyTo = mGetReplyMessenger;
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mTextView.setText("断开");
        }
    };

    private TextView mTextView;

    /**
     * 接下来再看看客户端的实现，客户端的实现也比较简单，首先需要绑定远程进程的MessengerService，{@link MessengerService}
     * 绑定成功后，
     * 根据服务端返回的binder对象创建Messenger对象并使用此对象向服务端发送消息。
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        mTextView = findViewById(R.id.content_tv);

        mTextView.setOnClickListener(v -> {
            mTextView.setText("开始绑定");
            Intent intent = new Intent();
            intent.setAction("com.ryg.MessengerService.launch");
            intent.setPackage("com.ryg.chapter_2");
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
}
