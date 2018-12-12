/*
 * This file is auto-generated.  DO NOT MODIFY.
 * 将自动生成的文件拷贝到这里, 方便Debug调试,这里修改了几个变量名,其他保持不变
 * Original file: /Users/malin/malin_review/AIDLDemo/client/app/src/main/aidl/com/malin/client/BookManager.aidl
 */
package com.malin.client;

import java.util.List;

public interface BookManager extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements com.malin.client.BookManager {
        private static final String DESCRIPTOR = "com.malin.client.BookManager";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.malin.client.BookManager interface,
         * generating a proxy if needed.
         */
        public static com.malin.client.BookManager asInterface(android.os.IBinder iBinder) {
            if ((iBinder == null)) {
                return null;
            }
            android.os.IInterface iin = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iin != null && iin instanceof BookManager) {
                return (com.malin.client.BookManager) iin;
            }
            return new com.malin.client.BookManager.Stub.Proxy(iBinder);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_getBooks: {
                    data.enforceInterface(descriptor);
                    List<Book> _result = this.getBooks();
                    reply.writeNoException();
                    reply.writeTypedList(_result);
                    return true;
                }
                case TRANSACTION_addBook: {
                    data.enforceInterface(descriptor);
                    com.malin.client.Book book;
                    if ((0 != data.readInt())) {
                        book = com.malin.client.Book.CREATOR.createFromParcel(data);
                    } else {
                        book = null;
                    }
                    this.addBook(book);
                    reply.writeNoException();
                    if ((book != null)) {
                        reply.writeInt(1);
                        book.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                    } else {
                        reply.writeInt(0);
                    }
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements com.malin.client.BookManager {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public List<Book> getBooks() throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                List<Book> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    mRemote.transact(Stub.TRANSACTION_getBooks, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createTypedArrayList(com.malin.client.Book.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            @Override
            public void addBook(com.malin.client.Book book) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((book != null)) {
                        _data.writeInt(1);
                        book.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                    _reply.readException();
                    if ((0 != _reply.readInt())) {
                        book.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_getBooks = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_addBook = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    public List<Book> getBooks() throws android.os.RemoteException;

    public void addBook(com.malin.client.Book book) throws android.os.RemoteException;
}
