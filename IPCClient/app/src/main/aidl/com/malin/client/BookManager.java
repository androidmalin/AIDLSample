/*
 * This file is auto-generated.  DO NOT MODIFY.
 * 将自动生成的文件拷贝到这里, 方便Debug调试,这里修改了几个变量名,其他保持不变
 * Original file: /Users/malin/malin_review/AIDLDemo/client/app/src/main/aidl/com/malin/client/BookManager.aidl
 */
package com.malin.client;

import android.support.annotation.NonNull;

import java.util.List;

public interface BookManager extends android.os.IInterface {

    List<Book> getBooks() throws android.os.RemoteException;

    void addBook(com.malin.client.Book book) throws android.os.RemoteException;


    /**
     * Local-side IPC implementation stub class.
     */
    abstract class Stub extends android.os.Binder implements com.malin.client.BookManager {
        private static final String DESCRIPTOR = "com.malin.client.BookManager";

        static final int TRANSACTION_getBooks = android.os.IBinder.FIRST_CALL_TRANSACTION;
        static final int TRANSACTION_addBook = android.os.IBinder.FIRST_CALL_TRANSACTION + 1;

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
            if (iBinder == null) {
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
        public boolean onTransact(int code, @NonNull android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
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

        /**
         * 1，生成 _data 和 _reply 数据流，并向 _data 中存入客户端的数据。
         * 2，通过 transact() 方法将它们传递给服务端，并请求服务端调用指定方法。
         * 3，接收 _reply 数据流，并从中取出服务端传回来的数据。
         */
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
                //Parcel 是一个用来存放和读取数据的容器。我们可以用它来进行客户端和服务端之间的数据传输，当然，它能传输的只能是可序列化的数据
                //_data用来存储流向服务端的数据流，
                //_reply用来存储服务端流回客户端的数据流
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                List<Book> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    //transact():这是客户端和服务端通信的核心方法。调用这个方法之后，客户端将会挂起当前线程，
                    //等候服务端执行完相关任务后通知并接收返回的 _reply 数据流
                    mRemote.transact(Stub.TRANSACTION_getBooks, _data, _reply, 0);// 0 表示数据可以双向流通
                    _reply.readException();
                    //从_reply中取出服务端执行方法的结果
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
                    if (book != null) {
                        _data.writeInt(1);
                        book.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0);
                    _reply.readException();
                    if (0 != _reply.readInt() && book != null) {
                        book.readFromParcel(_reply);
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }


    }

}
