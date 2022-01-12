package com.ryg.chapter_2.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.ryg.chapter_2.utils.MyConstants;

/**
 * Messenger可以翻译为信使，顾名思义，通过它可以在不同进程中传递Message对象，
 * 在Message中放入我们需要传递的数据，就可以轻松地实现数据的进程间传递了。
 * Messenger是一种轻量级的IPC方案，它的底层实现是AIDL，为什么这么说呢，
 * 我们大致看一下Messenger这个类的构造方法就明白了。
 * <p>
 * {@link Messenger}
 * 下面是Messenger的两个构造方法，从构造方法的实现上我们可以明显看出AIDL的痕迹，
 * 不管是IMessenger还是Stub.asInterface，这种使用方法都表明它的底层是AIDL。
 * <p>
 * Messenger的使用方法很简单，它对AIDL做了封装，使得我们可以更简便地进行进程间通信。
 * 同时，由于它一次处理一个请求，因此在服务端我们不用考虑线程同步的问题，
 * 这是因为服务端中不存在并发执行的情形。
 * 实现一个Messenger有如下几个步骤，分为服务端和客户端。
 * <p>
 * 1.服务端进程
 * 首先，我们需要在服务端创建一个Service来处理客户端的连接请求，
 * 同时创建一个Handler并通过它来创建一个Messenger对象，
 * {@link MessengerService#onBind(Intent)}
 * 然后在Service的onBind()方法中返回这个Messenger对象底层的Binder即可。
 * <p>
 * 2.客户端进程
 * <p>
 * 客户端进程中，首先要绑定服务端的Service，绑定成功后用服务端返回的IBinder对象创建一个Messenger，
 * 通过这个Messenger就可以向服务端发送消息了，发消息类型为Message对象。
 * 如果需要服务端能够回应客户端，就和服务端一样，我们还需要创建一个Handler并创建一个新的Messenger，
 * 并把这个Messenger对象通过Message的replyTo参数传递给服务端，服务端通过这个replyTo参数就可以回应客户端。
 * <p>
 * 3.
 * 然后，注册service，让其运行在单独的进程中：
 * <service
 * android:name="com.ryg.chapter_2.messenger.MessengerService"
 * android:process=":remote" >
 *
 * <p>
 * Android开发艺术探索
 */
public class MessengerService extends Service {

    private static final String TAG = "MessengerService";

    /**
     * 这里Messenger的作用是将客户端发送的消息传递给MessengerHandler处理。
     */
    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    private static class MessengerHandler extends Handler {

        public MessengerHandler() {
            super(Looper.myLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MyConstants.MSG_FROM_CLIENT) {
                Log.i(TAG, "receive msg from Client:" + msg.getData().getString("msg"));
                Messenger client = msg.replyTo;
                Message replyMessage = Message.obtain(null, MyConstants.MSG_FROM_SERVICE);
                Bundle bundle = new Bundle();
                bundle.putString("reply", "嗯，你的消息我已经收到，稍后会回复你。");
                replyMessage.setData(bundle);
                try {
                    client.send(replyMessage);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                super.handleMessage(msg);
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
