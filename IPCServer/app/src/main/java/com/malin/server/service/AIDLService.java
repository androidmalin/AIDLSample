package com.malin.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.malin.client.Book;
import com.malin.client.BookManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 在服务端实现AIDL中定义的方法接口的具体逻辑，然后在客户端调用这些方法接口，从而达到跨进程通信的目的。
 * <p>
 * 可以将app/libs下的aidl文件拷贝到app/src/main/aidl/com/malin/client/目录下,
 * 同时将该目录下的java文件全部删除
 * 使用编译过程中自动生成的java文件
 * <p>
 * 服务端的AIDLService.java
 * https://blog.csdn.net/luoyanglizi/article/details/51980630
 * https://blog.csdn.net/luoyanglizi/article/details/51586437
 * <p>
 * 第一块是初始化: 在 onCreate() 方法里面进行了一些数据的初始化操作。
 * 第二块是创建远端服务的具体实现,通过继承BookManager.Stub类,并实现BookManager.Stub 中的方法。在这里面提供AIDL里面定义的方法接口的具体实现逻辑。
 * 第三块是重写: onBind() 方法。在里面返回写好的 BookManager.Stub的子类(远端服务的具体实现)
 */
public class AIDLService extends Service {

    public final String TAG = "AIDLService";

    //包含Book对象的list
    private volatile List<Book> mBooks = new ArrayList<>();

    //远端服务的具体实现.
    //BookManager.Stub(抽象类,继承了Binder,同时实现了AIDL接口)为远端服务的中间者.
    //在服务端实现AIDL中定义的方法接口的具体逻辑，然后在客户端调用这些方法接口，从而达到跨进程通信的目的。
    //由AIDL文件生成的BookManager
    private final BookManager.Stub mBookManager = new BookManager.Stub() {
        @Override
        public List<Book> getBooks() {
            Log.d(TAG, TAG + "==>getBooks()");
            synchronized (this) {
                Log.e(TAG, "invoking getBooks() method , now the list is : " + mBooks.toString());
                if (mBooks != null) {
                    return mBooks;
                } else {
                    return new ArrayList<>();
                }
            }
        }

        @Override
        public void addBook(Book book) {
            Log.d(TAG, TAG + "==>addBook()");
            synchronized (this) {
                if (mBooks == null) {
                    mBooks = new ArrayList<>();
                }
                if (book == null) {
                    Log.e(TAG, "Book is null in In");
                    book = new Book();
                }
                //尝试修改book的参数，主要是为了观察其到客户端的反馈
                book.setPrice(99999);
                if (!mBooks.contains(book)) {
                    mBooks.add(book);
                }
                //打印mBooks列表，观察客户端传过来的值
                Log.e(TAG, "now " + mBooks.toString());
            }
        }
    };

    /**
     * onCreate 在每个service的生命周期中这个方法会且仅会调用一次
     * 它的调用在onStartCommand()以及onBind()之前
     */
    @Override
    public void onCreate() {
        Log.d(TAG, TAG + "==>onCreate()");
        Book book = new Book();
        book.setName("From Server Book");
        book.setPrice(28);
        mBooks.add(book);
        super.onCreate();
    }

    /**
     * 要创建一个支持绑定的service，我们必须要重写它的onBind()方法。这个方法会返回一个IBinder对象，它是客户端用来和服务器进行交互的接口。
     * 而要得到IBinder接口，我们通常有三种方式：继承Binder类，使用Messenger类，使用AIDL。
     * <p>
     * 当其他组件通过bindService()方法与service相绑定之后，此方法将会被调用。
     * 这个方法有一个IBinder的返回值，这意味着在重写它的时候必须返回一个IBinder对象，
     * 它是用来支撑其他组件与service之间的通信的
     * 如果你不想让这个service被其他组件所绑定，可以通过在这个方法返回一个null值来实现
     * <p>
     * 如果要创建一个支持绑定的service，我们必须要重写它的onBind()方法。这个方法会返回一个IBinder对象，
     * 它是客户端用来和服务器进行交互的接口。
     * 而要得到IBinder接口，我们通常有三种方式：继承Binder类，使用Messenger类，使用AIDL
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAG + "==>onBind()");
        Log.e(TAG, String.format("on bind,intent = %s", intent.toString()));
        return mBookManager;
    }


    /**
     * 当其他组件通过startService()方法启动service时，此方法将会被调用
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + "==>onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 这是service生命周期中调用的最后一个方法，当这个方法被调用之后，service就会被销毁。
     * 所以我们应当在这个方法里面进行一些资源的清理，比如注册的一些监听器什么的。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + "==>onDestroy()");
    }
}
