package com.wellee.library;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

/**
 * @author : liwei
 * 创建日期 : 2019/12/26 13:54
 * 邮   箱 : liwei@worken.cn
 * 功能描述 :
 */
public class NetworkManager {

    private static volatile NetworkManager INSTANCE;

    private NetStateReceiver netStateReceiver;
    private Application application;
    private NetworkCallbackImpl networkCallback;

    private NetworkManager(){
        netStateReceiver = new NetStateReceiver();
    }

    public static NetworkManager getDefault() {
        if (INSTANCE == null) {
            synchronized (NetworkManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Application application) {
        this.application = application;
        // 第一种 动态注册广播
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Constant.ANDROID_NET_CHANGE_ACTION);
//        application.registerReceiver(netStateReceiver, filter);

        // 第二种方式 实现NetworkCallback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new NetworkCallbackImpl();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            ConnectivityManager manager = (ConnectivityManager) NetworkManager.getDefault()
                    .getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager != null) {
                manager.registerNetworkCallback(request, networkCallback);
            }
        }
    }

    public Application getApplication() {
        if (application == null) {
            throw new RuntimeException("you must first invoke the init function");
        }
        return application;
    }

    public void registerObserver(Object object) {
        // netStateReceiver.registerObserver(object); //广播方式
        networkCallback.registerObserver(object);
    }

    public void unRegisterObserver(Object object) {
        // netStateReceiver.unRegisterObserver(object); // 广播方式
        networkCallback.unRegisterObserver(object);
    }

    public void unRegisterAllObserver() {
        // netStateReceiver.unRegisterAllObserver(); // 广播方式
    }
}
