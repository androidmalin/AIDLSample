package com.malin.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.malin.client.Book;
import com.malin.client.BookManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 可以将app/libs下的aidl文件拷贝到app/src/main/aidl/com/malin/client/目录下,
 * 同时将该目录下的java文件全部删除
 * 使用编译过程中自动生成的java文件
 * <p>
 * 服务端的AIDLService.java
 * https://blog.csdn.net/luoyanglizi/article/details/51980630
 */
public class AIDLService extends Service {

    public final String TAG = "AIDLService";

    private static final String PACKAGE_ALLOW = "com.malin.client";

    //包含Book对象的list
    private List<Book> mBooks = new ArrayList<>();

    //由AIDL文件生成的BookManager
    private final BookManager.Stub mBookManager = new BookManager.Stub() {
        @Override
        public List<Book> getBooks() {
            synchronized (this) {
                Log.e(TAG, "invoking getBooks() method , now the list is : " + mBooks.toString());
                if (mBooks != null) {
                    return mBooks;
                }
                return new ArrayList<>();
            }
        }


        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String packageName = null;
            String[] packages = AIDLService.this.getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            Log.d(TAG, "onTransact: " + packageName);
            if (!PACKAGE_ALLOW.equals(packageName)) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void addBook(Book book) {
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
     */
    @Override
    public void onCreate() {
        Book book = new Book();
        book.setName("From Server Book");
        book.setPrice(28);
        mBooks.add(book);
        super.onCreate();
    }

    /**
     * 当其他组件通过bindService()方法与service相绑定之后，此方法将会被调用。
     * 这个方法有一个IBinder的返回值，这意味着在重写它的时候必须返回一个IBinder对象，
     * 它是用来支撑其他组件与service之间的通信的
     * 如果你不想让这个service被其他组件所绑定，可以通过在这个方法返回一个null值来实现
     *
     * 如果要创建一个支持绑定的service，我们必须要重写它的onBind()方法。这个方法会返回一个IBinder对象，
     * 它是客户端用来和服务器进行交互的接口。
     * 而要得到IBinder接口，我们通常有三种方式：继承Binder类，使用Messenger类，使用AIDL
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, String.format("on bind,intent = %s", intent.toString()));
        return mBookManager;
    }


    /**
     * 当其他组件通过startService()方法启动service时，此方法将会被调用
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
