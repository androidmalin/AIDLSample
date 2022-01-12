/*
 * This file is auto-generated.  DO NOT MODIFY.
 * 将自动生成的文件拷贝到这里, 方便Debug调试,这里修改了几个变量名,其他保持不变
 * Original file: /Users/malin/malin_review/AIDLDemo/client/app/src/main/aidl/com/malin/client/BookManager.aidl
 */
package com.malin.client;

import androidx.annotation.NonNull;

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
    /**
     * 2、AIDL接口中肯定有一个静态实现类Stub
     * <p>
     * 这个类必须实现Binder类，以及本身的AIDL接口类型。那么这个类就具备了Binder类中的四个功能：
     * 1. 可以将Binder对象转化成AIDL对象，调用asInterface方法，可以看到这个方法其实和上面的asBinder方法对立的
     * 2. 通信方法onTransact实现，这个方法是最核心的用于通信之间的逻辑实现
     * 3. 通过queryLocalInterface方法可以根据类的描述符(字符串可以唯一标识这个远端服务的名称即可)获取到对应的AIDL对象(其实是IInterface类型的)
     * 4. 在构造方法中必须调用Binder中的attachInterface方法把当前服务对象和描述符进行关联
     */
    abstract class Stub extends android.os.Binder implements com.malin.client.BookManager {
        /**
         * Binder的唯一标识，一般用当前Binder的类名表示
         */
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
         * 用于将服务端的Binder对象转换成客户端所需的AIDL接口类型的对象，这种转换过程是区分进程的，
         * 如果客户端和服务端位于同一进程，那么此方法返回的就是服务端的Stub对象本身，否则返回的是系统封装后的Stub.proxy对象。
         * Android开发艺术探索
         *
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
        static com.malin.client.BookManager asInterface(android.os.IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            android.os.IInterface iin = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iin instanceof BookManager) {
                return (com.malin.client.BookManager) iin;
            }
            return new com.malin.client.BookManager.Stub.Proxy(iBinder);
        }

        /**
         * 此方法用于返回当前Binder对象。
         */
        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        /**
         * 服务端的一般工作流程：
         * <p>
         * 1，获取客户端传过来的数据，根据方法 ID 执行相应操作。
         * 2，将传过来的数据取出来，调用本地写好的对应方法。
         * 3，将需要回传的数据写入 reply 流，传回客户端。
         * <p>
         * Android开发艺术探索
         * 这个方法运行在服务端中的Binder线程池中，当客户端发起跨进程请求时，
         * 远程请求会通过系统底层封装后交由此方法来处理。
         * 该方法的原型为public Boolean onTransact（int code,android.os.Parcel data,android.os.Parcel reply,int flags）。
         * 服务端通过code可以确定客户端所请求的目标方法是什么，接着从data中取出目标方法所需的参数（如果目标方法有参数的话），然后执行目标方法。
         * 当目标方法执行完毕后，就向reply中写入返回值（如果目标方法有返回值的话），onTransact方法的执行过程就是这样的。
         * 需要注意的是，如果此方法返回false，那么客户端的请求会失败，
         * 因此我们可以利用这个特性来做权限验证，毕竟我们也不希望随便一个进程都能远程调用我们的服务。
         * <p>
         */
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
                    //从输入的data流中读取book数据，并将其赋值给book
                    if ((0 != data.readInt())) {
                        book = com.malin.client.Book.CREATOR.createFromParcel(data);
                    } else {
                        book = null;
                    }
                    //在这里才是真正的开始执行实际的逻辑，调用服务端写好的实现
                    this.addBook(book);
                    reply.writeNoException();
                    //在这里，book是方法的传入参数，故服务端的实现里对传参做出的任何修改，
                    //都会在book中有所体现，将其写入reply流，就有了将这些修改传回客户端的前提
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
         * <p>
         * Proxy 类的方法里面一般的工作流程
         * 1，生成 _data 和 _reply 数据流，并向 _data 中存入客户端的数据。
         * 2，通过 transact() 方法将它们传递给服务端，并请求服务端调用指定方法。
         * 3，接收 _reply 数据流，并从中取出服务端传回来的数据。
         */
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

            /**
             * 这个方法运行在客户端，当客户端远程调用此方法时，
             * 它的内部实现是这样的：
             * 首先创建该方法所需要的输入型Parcel对象_data、输出型Parcel对象_reply和返回值对象List；
             * 然后把该方法的参数信息写入_data中（如果有参数的话）；
             * 接着调用transact方法来发起RPC（远程过程调用）请求，同时当前线程挂起；
             * 然后服务端的onTransact方法会被调用，直到RPC过程返回后，当前线程继续执行，
             * 并从_reply中取出RPC过程的返回结果；
             * 最后返回_reply中的数据。
             * <p>
             * Android开发艺术探索
             */
            @Override
            public List<Book> getBooks() throws android.os.RemoteException {
                //很容易可以分析出来
                // _data用来存储流向服务端的数据流，
                // _reply用来存储服务端流回客户端的数据流
                // Parcel 是一个用来存放和读取数据的容器
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                List<Book> _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);

                    //调用 transact() 方法将方法id和两个 Parcel 容器传过去
                    //调用这个方法之后，客户端将会挂起当前线程，等候服务端执行完相关任务后通知并接收返回的 _reply 数据流
                    mRemote.transact(Stub.TRANSACTION_getBooks, _data, _reply, 0);
                    _reply.readException();

                    //从_reply中取出服务端执行方法的结果
                    _result = _reply.createTypedArrayList(com.malin.client.Book.CREATOR);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            /**
             * 这个方法运行在客户端，
             * 它的执行过程和getBookList是一样的，addBook没有返回值，所以它不需要从_reply中取出返回值。
             * 有两点还是需要额外说明一下：
             * 首先，当客户端发起远程请求时，由于当前线程会被挂起直至服务端进程返回数据，
             * 所以如果一个远程方法是很耗时的，那么不能在UI线程中发起此远程请求；
             * 其次，由于服务端的Binder方法运行在Binder的线程池中，
             * 所以Binder方法不管是否耗时都应该采用同步的方式去实现，因为它已经运行在一个线程中了。为了更好地说明Binder
             * 了更好地说明Binder，下面给出一个Binder的工作机制图，如图binder_doc.png所示。
             * <p>
             * 从上述分析过程来看，我们完全可以不提供AIDL文件即可实现Binder，
             * 之所以提供AIDL文件，是为了方便系统为我们生成代码。
             * 系统根据AIDL文件生成Java文件的格式是固定的，
             * 我们可以抛开AIDL文件直接写一个Binder出来，接下来我们就介绍如何手动写一个Binder。
             * 还是上面的例子，但是这次我们不提供AIDL文件。
             * 参考上面系统自动生成的BookManager.java这个类的代码，
             * 可以发现这个类是相当有规律的，根据它的特点，我们完全可以自己写一个和它一模一样的类出来，
             * 然后这个不借助AIDL文件的Binder就完成了。
             * 但是我们发现系统生成的类看起来结构不清晰，我们想试着对它进行结构上的调整，
             * 可以发现这个类主要由两部分组成，首先它本身是一个Binder的接口（继承了IInterface），
             * 其次它的内部由个Stub类，这个类就是个Binder。还记得我们怎么写一个Binder的服务端吗？
             * 代码如下所示。
             *
             * <p>
             * Android开发艺术探索
             */
            @Override
            public void addBook(com.malin.client.Book book) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                // _data（包含从客户端流向服务端的book流），
                // _reply（包含从服务端流向客户端的数据流）传入
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
                    //针对_reply的操作，并且将book赋值为_reply流中的数据
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
