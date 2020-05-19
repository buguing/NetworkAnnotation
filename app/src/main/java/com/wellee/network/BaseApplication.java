package com.wellee.network;

import android.app.Application;

import com.wellee.library.NetworkManager;

/**
 * @author : liwei
 * 创建日期 : 2019/12/26 14:45
 * 邮   箱 : liwei@worken.cn
 * 功能描述 :
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkManager.getDefault().init(this);
    }
}
