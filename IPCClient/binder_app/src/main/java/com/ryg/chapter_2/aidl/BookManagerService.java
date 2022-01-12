package com.ryg.chapter_2.aidl;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 1.服务端
 * 服务端首先要创建一个Service用来监听客户端的连接请求，
 * 然后创建一个AIDL文件，将暴露给客户端的接口在这个AIDL文件中声明，
 * 最后在Service中实现这个AIDL接口即可。
 * <p>
 * 2.客户端
 * 客户端所要做事情就稍微简单一些，首先需要绑定服务端的Service，
 * 绑定成功后，将服务端返回的Binder对象转成AIDL接口所属的类型，
 * 接着就可以调用AIDL中的方法了。
 * <p>
 * 3.AIDL接口的创建
 * 首先看AIDL接口的创建，如下所示，我们创建了一个后缀为AIDL的文件，
 * 在里面声明了一个接口和两个接口方法。
 * IBookManager.aidl
 * package com.ryg.chapter_2.aidl;
 * import com.ryg.chapter_2.aidl.Book;
 * interface IBookManager {
 * List<Book> getBookList();
 * void addBook(in Book book);
 * }
 * <p>
 * 在AIDL文件中，并不是所有的数据类型都是可以使用的，
 * 那么到底AIDL文件支持哪些数据类型呢？如下所示:
 * <p>
 * 1.基本数据类型（int、long、char、boolean、double等）；
 * 2.String和CharSequence；
 * 3.List：只支持ArrayList，里面每个元素都必须能够被AIDL支持；
 * 4.Map：只支持HashMap，里面的每个元素都必须被AIDL支持，包括key和value；
 * 5.Parcelable：所有实现了Parcelable接口的对象；
 * <p>
 * AIDL：所有的AIDL接口本身也可以在AIDL文件中使用。
 * 以上6种数据类型就是AIDL所支持的所有类型，
 * <p>
 * 其中自定义的Parcelable对象和AIDL对象必须要显式import进来，
 * 不管它们是否和当前的AIDL文件位于同一个包内。
 * 比如IBookManager.aidl这个文件，
 * 里面用到了Book这个类，这个类实现了Parcelable接口
 * 并且和IBookManager.aidl位于同一个包中，但是遵守AIDL的规范，
 * 我们仍然需要显式地import进来：
 * import com.ryg.chapter_2.aidl.Book。
 * AIDL中会大量使用到Parcelable，至于如何使用Parcelable接口来序列化对象，
 * 在本章的前面已经介绍过，这里就不再赘述。
 * <p>
 * 另外一个需要注意的地方是，如果AIDL文件中用到了自定义的Parcelable对象，
 * 那么必须新建一个和它同名的AIDL文件，并在其中声明它为Parcelable类型。
 * 在上面的IBookManager.aidl中，我们用到了Book这个类，
 * 所以，我们必须要创建Book.aidl，然后在里面添加如下内容：
 * package com.ryg.chapter_2.aidl;
 * parcelable Book;
 * <p>
 * <p>
 * 我们需要注意，AIDL中每个实现了Parcelable接口的类都需要按照上面那种方式
 * 去创建相应的AIDL文件并声明那个类为parcelable。
 * 除此之外，AIDL中除了基本数据类型，其他类型的参数必须标上方向：in、out或者inout，
 * in表示输入型参数，out表示输出型参数，inout表示输入输出型参数，
 * <p>
 * 我们要根据实际需要去指定参数类型，不能一概使用out或者inout，
 * 因为这在底层实现是有开销的。
 * 最后，AIDL接口中只支持方法，不支持声明静态常量，这一点区别于传统的接口。
 * <p>
 * 为了方便AIDL的开发，建议把所有和AIDL相关的类和文件全部放入同一个包中，
 * 这样做的好处是，当客户端是另外一个应用时，我们可以直接把整个包复制到客户端工程中，
 * 对于本例来说，就是要把com.ryg.chapter_2.aidl这个包和包中的文件原封不动地复制到客户端中。
 * <p>
 * 如果AIDL相关的文件位于不同的包中时，那么就需要把这些包一一复制到客户端工程中，
 * 这样操作起来比较麻烦而且也容易出错。
 * <p>
 * 需要注意的是，AIDL的包结构在服务端和客户端要保持一致，否则运行会出错，
 * 这是因为客户端需要反序列化服务端中和AIDL接口相关的所有类，
 * 如果类的完整路径不一样的话，就无法成功反序列化，程序也就无法正常运行。
 * 为了方便演示，本章的所有示例都是在同一个工程中进行的，
 * 但是读者要理解，一个工程和两个工程的多进程本质是一样的，
 * 两个工程的情况，除了需要复制AIDL接口所相关的包到客户端，其他完全一样，读者可以自行试验。
 * <p>
 * <p>
 * 4.远程服务端Service的实现
 * 上面讲述了如何定义AIDL接口，接下来我们就需要实现这个接口了。
 * 我们先创建一个Service，称为BookManagerService，代码如下：
 * {@link BookManagerService}
 * <p>
 * 下面是一个服务端Service的典型实现，
 * 首先在onCreate中初始化添加了两本图书的信息，
 * 然后创建了一个Binder对象并在onBind中返回它，
 * 这个对象继承自IBookManager.Stub并实现了它内部的AIDL方法，
 * 这个过程在Binder那一节已经介绍过了，这里就不多说了。
 * 这里主要看getBookList和addBook这两个AIDL方法的实现，
 * 实现过程也比较简单，注意这里采用了CopyOnWriteArrayList，
 * 这个CopyOnWriteArrayList支持并发读/写。
 * 在前面我们提到，AIDL方法是在服务端的Binder线程池中执行的，
 * 因此当多个客户端同时连接的时候，会存在多个线程同时访问的情形，
 * 所以我们要在AIDL方法中处理线程同步，
 * 而我们这里直接使用CopyOnWriteArrayList来进行自动的线程同步。
 * <p>
 * 前面我们提到，AIDL中能够使用的List只有ArrayList，
 * 但是我们这里却使用了CopyOnWriteArrayList（注意它不是继承自ArrayList），
 * 为什么能够正常工作呢？这是因为AIDL中所支持的是抽象的List，
 * 而List只是一个接口，因此虽然服务端返回的是CopyOnWriteArrayList，
 * 但是在Binder中会按照List的规范去访问数据并最终形成一个新的ArrayList传递给客户端。
 * 所以，我们在服务端采用CopyOnWriteArrayList是完全可以的。
 * 和此类似的还有ConcurrentHashMap，读者可以体会一下这种转换情形。
 * 然后我们需要在XML中注册这个Service，如下所示。
 * 注意BookManagerService是运行在独立进程中的，它和客户端的Activity不在同一个进程中，
 * 这样就构成了进程间通信的场景。
 */
public class BookManagerService extends Service {

    private static final String TAG = "BMS";

    private final AtomicBoolean mIsServiceDestroy = new AtomicBoolean(false);

    /**
     * CopyOnWriteArrayList支持并发读/写
     */
    private final CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();

    /**
     * Binder会把客户端传递过来的对象重新转化并生成一个新的对象。
     * 虽然我们在注册和解注册过程中使用的是同一个客户端对象，
     * 但是通过Binder传递到服务端后，却会产生两个全新的对象。
     * 别忘了对象是不能跨进程直接传输的，对象的跨进程传输本质上都是反序列化的过程，
     * 这就是为什么AIDL中的自定义对象都必须要实现Parcelable接口的原因。
     * 那么到底我们该怎么做才能实现解注册功能呢？
     * 答案是使用RemoteCallbackList，这看起来很抽象，不过没关系，请看接下来的详细分析。
     * <p>
     * RemoteCallbackList是系统专门提供的用于删除跨进程listener的接口。
     * Remote-CallbackList是一个泛型，支持管理任意的AIDL接口，
     * 这点从它的声明就可以看出，因为所有的AIDL接口都继承自IInterface接口，读者还有印象吗？
     * <p>
     * public class RemoteCallbackList<E extends IInterface>
     * 它的工作原理很简单，在它的内部有一个Map结构专门用来保存所有的AIDL回调，这个Map的key是IBinder类型，value是Callback类型，如下所示。
     * <p>
     * ArrayMap<IBinder, Callback> mCallbacks = new ArrayMap<IBinder, Callback>();
     * 其中Callback中封装了真正的远程listener。
     * 当客户端注册listener的时候，它会把这个listener的信息存入mCallbacks中，其中key和value分别通过下面的方式获得：
     * <p>
     * IBinder key= listener.asBinder()
     * Callback value = new Callback(listener, cookie)
     * <p>
     * 到这里，读者应该都明白了，虽然说多次跨进程传输客户端的同一个对象会在服务端生成不同的对象，
     * 但是这些新生成的对象有一个共同点，那就是它们底层的Binder对象是同一个，
     * 利用这个特性，就可以实现上面我们无法实现的功能。
     * 当客户端解注册的时候，我们只要遍历服务端所有的listener，
     * 找出那个和解注册listener具有相同Binder对象的服务端listener并把它删掉即可，
     * 这就是RemoteCallbackList为我们做的事情。
     * 同时RemoteCallbackList还有一个很有用的功能，
     * 那就是当客户端进程终止后，它能够自动移除客户端所注册的listener。
     * 另外，RemoteCallbackList内部自动实现了线程同步的功能，
     * 所以我们使用它来注册和解注册时，不需要做额外的线程同步工作。
     * 由此可见，RemoteCallbackList的确是个很有价值的类，下面就演示如何使用它来完成解注册。
     * <p>
     * RemoteCallbackList使用起来很简单，我们要对BookManagerService做一些修改，
     * 首先要创建一个RemoteCallbackList对象来替代之前的CopyOnWriteArrayList，如下所示。
     * private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();
     * <p>
     * <p>
     * 使用RemoteCallbackList，有一点需要注意，我们无法像操作List一样去操作它，
     * 尽管它的名字中也带个List，但是它并不是一个List。
     * 遍历RemoteCallbackList，必须要按照下面的方式进行，
     * 其中beginBroadcast和beginBroadcast必须要配对使用，哪怕我们仅仅是想要获取RemoteCallbackList中的元素个数，这是必须要注意的地方。
     * <p>
     * final int N = mListenerList.beginBroadcast();
     * for (int i = 0; i < N; i++) {
     * _____IOnNewBookArrivedListener l = mListenerList.getBroadcastItem(i);
     * ___________if (l != null) {
     * ______________//TODO xxx
     * ____________}
     * }
     * mListenerList.finishBroadcast();
     * <p>
     * 由于服务端的方法本身就运行在服务端的Binder线程池中，所以服务端方法本身就可以执行大量耗时操作，
     * 这个时候切记不要在服务端方法中开线程去进行异步任务，
     * 除非你明确知道自己在干什么，否则不建议这么做。
     */
    private final RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();


    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "android"));
        mBookList.add(new Book(2, "ios"));
        new Thread(new ServiceWorker()).start();
    }

    @SuppressLint("WrongConstant")
    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("com.ryg.chapter_2.permission.ACCESS_BOOK_SERVICE");
        Log.d(TAG, "onBind check=" + check);
        if (check == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return mBinder;
    }

    private final Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() {
            //SystemClock.sleep(5_000);
            return mBookList;
        }

        @Override
        public void addBook(Book book) {
            mBookList.add(book);
        }

        @SuppressLint("WrongConstant")
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            int check = checkCallingOrSelfPermission("com.ryg.chapter_2.permission.ACCESS_BOOK_SERVICE");
            Log.d(TAG, "onTransact# check=" + check);
            if (check == PackageManager.PERMISSION_DENIED) {
                return false;
            }

            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            Log.d(TAG, "onTransact# " + packageName);
            if (!packageName.startsWith("com.ryg")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) {
            mListenerList.register(listener);
            final int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "registerListener, current size:" + N);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) {
            boolean success = mListenerList.unregister(listener);
            if (success) {
                Log.d(TAG, "unregister success.");
            } else {
                Log.d(TAG, "not found, can not unregister.");
            }
            final int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "unregisterListener, current size:" + N);
        }
    };


    @Override
    public void onDestroy() {
        mIsServiceDestroy.set(true);
        super.onDestroy();
    }

    /**
     * 用户不想时不时地去查询图书列表了，太累了，于是，他去问图书馆，
     * "当有新书时能不能把书的信息告诉我呢？"。
     * 大家应该明白了，这就是一种典型的观察者模式，每个感兴趣的用户都观察新书，
     * 当新书到的时候，图书馆就通知每一个对这本书感兴趣的用户，这种模式在实际开发中用得很多，
     * 下面我们就来模拟这种情形。
     * 首先，我们需要提供一个AIDL接口，每个用户都需要实现这个接口并且向图书馆申请新书的提醒功能，
     * 当然用户也可以随时取消这种提醒。之所以选择AIDL接口而不是普通接口，是因为AIDL中无法使用普通接口。
     * 这里我们创建一个IOnNewBookArrivedListener.aidl文件，
     * 我们所期望的情况是：当服务端有新书到来时，就会通知每一个已经申请提醒功能的用户。
     * 从程序上来说就是调用所有IOnNew BookArrivedListener对象中的onNewBookArrived方法，并把新书的对象通过参数传递给客户端，内容如下所示。
     */
    private void onNewBookArrived(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener l = mListenerList.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.onNewBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            // do background processing here.....
            while (!mIsServiceDestroy.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book#" + bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
