package com.ryg.chapter_2.manualbinder;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

public interface IBookManager extends IInterface {

    String DESCRIPTOR = "com.ryg.chapter_2.manualbinder.IBookManager";

    int TRANSACTION_getBookList = IBinder.FIRST_CALL_TRANSACTION;
    int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION + 1;

    List<Book> getBookList() throws RemoteException;

    void addBook(Book book) throws RemoteException;
}
