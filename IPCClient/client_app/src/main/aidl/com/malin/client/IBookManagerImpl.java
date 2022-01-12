package com.malin.client;

import android.os.Binder;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class IBookManagerImpl extends Binder implements IBookManager {

    private static final String DESCRIPTOR = "com.malin.client.BookManager";
    static final int TRANSACTION_getBooks = android.os.IBinder.FIRST_CALL_TRANSACTION;
    static final int TRANSACTION_addBook = android.os.IBinder.FIRST_CALL_TRANSACTION + 1;


    public IBookManagerImpl() {
        this.attachInterface(this, DESCRIPTOR);
    }

    static com.malin.client.BookManager asInterface(android.os.IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        android.os.IInterface iin = iBinder.queryLocalInterface(DESCRIPTOR);
        if (iin instanceof BookManager) {
            return (com.malin.client.BookManager) iin;
        }
        return new IBookManagerImpl.Proxy(iBinder);
    }

    @Override
    public android.os.IBinder asBinder() {
        return this;
    }

    @Override
    public boolean onTransact(int code, @NonNull android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
        switch (code) {
            case INTERFACE_TRANSACTION: {
                return true;
            }
            case TRANSACTION_getBooks: {
                data.enforceInterface(DESCRIPTOR);
                List<Book> _result = this.getBooks();
                return true;
            }
            case TRANSACTION_addBook: {
                System.out.println("x");
                return true;
            }
            default: {
                return super.onTransact(code, data, reply, flags);
            }
        }
    }

    private static class Proxy implements com.malin.client.BookManager {

        private final android.os.IBinder mRemote;

        Proxy(android.os.IBinder remote) {
            mRemote = remote;
        }

        @Override
        public android.os.IBinder asBinder() {
            return mRemote;
        }

        @SuppressWarnings("unused")
        public String getInterfaceDescriptor() {
            return DESCRIPTOR;
        }

        @Override
        public List<Book> getBooks() {
            return null;
        }

        @Override
        public void addBook(com.malin.client.Book book) {
        }
    }
}
