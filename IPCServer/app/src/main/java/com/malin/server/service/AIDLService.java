package com.malin.server.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.malin.client.Book;
import com.malin.client.BookManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务端的AIDLService.java
 * https://blog.csdn.net/luoyanglizi/article/details/51980630
 */
public class AIDLService extends Service {

    public final String TAG = "AIDLService";

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

    @Override
    public void onCreate() {
        Book book = new Book();
        book.setName("From Server Book");
        book.setPrice(28);
        mBooks.add(book);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, String.format("on bind,intent = %s", intent.toString()));
        return mBookManager;
    }
}
