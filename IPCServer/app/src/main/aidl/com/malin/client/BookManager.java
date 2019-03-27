/*
 * This file is auto-generated.  DO NOT MODIFY.
 * 将自动生成的文件拷贝到这里, 方便Debug调试,这里修改了几个变量名,其他保持不变
 * Original file: /Users/malin/malin_review/AIDLDemo/client/app/src/main/aidl/com/malin/client/BookManager.aidl
 */
package com.malin.client;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * IInterface代表的就是远程server对象具有什么能力。具体来说，就是aidl里面的接口
 */
public interface BookManager extends android.os.IInterface {

    List<Book> getBooks() throws android.os.RemoteException;

    void addBook(com.malin.client.Book book) throws android.os.RemoteException;


    /**
     * Local-side IPC implementation stub class.
     * Java层的Binder类，代表的其实就是Binder本地对象。
     * Stub类 继承了Binder, 说明它是一个Binder本地对象，
     * 它实现了IInterface接口，表明它具有远程Server承诺给Client的能力
     * Stub是一个抽象类，具体的IInterface的相关实现需要我们手动完成，这里使用了策略模式。
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
         * <p>
         * 试着查找Binder本地对象，如果找到，说明Client和Server都在同一个进程，
         * 这个参数直接就是本地对象，直接强制类型转换然后返回，
         * <p>
         * 如果找不到，说明是远程对象（处于另外一个进程）那么就需要创建一个Binder代理对象，
         * 让这个Binder代理实现对于远程对象的访问。
         * <p>
         * 一般来说，如果是与一个远程Service对象进行通信，那么这里返回的一定是一个Binder代理对象，
         * 这个IBinder参数的实际上是BinderProxy
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
                /*
                 * 在Server进程里面，onTransact根据调用号（每个AIDL函数都有一个编号，在跨进程的时候，不会传递函数，而是传递编号指明调用哪个函数）调用相关函数；
                 * 在这个例子里面，调用了Binder本地对象的getBooks方法；
                 * 这个方法将结果返回给驱动，驱动唤醒挂起的Client进程里面的线程并将结果返回。于是一次跨进程调用就完成了
                 */
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
         * Binder代理对象
         * 实现了IInterface并持有了IBinder引用
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
