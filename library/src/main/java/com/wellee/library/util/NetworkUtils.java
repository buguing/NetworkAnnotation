package com.wellee.library.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.wellee.library.NetworkManager;
import com.wellee.library.type.NetType;

/**
 * @author : liwei
 * 创建日期 : 2019/12/26 13:52
 * 邮   箱 : liwei@worken.cn
 * 功能描述 :
 */
public class NetworkUtils {

    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) NetworkManager.getDefault()
                .getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo[] networkInfos = manager.getAllNetworkInfo();
            for (NetworkInfo networkInfo : networkInfos) {
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static NetType getNetType() {
        ConnectivityManager manager = (ConnectivityManager) NetworkManager.getDefault()
                .getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null) {
                int type = networkInfo.getType();
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    if (networkInfo.getExtraInfo().equalsIgnoreCase("cmnet")) {
                        return NetType.CMNET;
                    } else {
                        return NetType.CMWAP;
                    }
                } else if (type == ConnectivityManager.TYPE_WIFI) {
                    return NetType.WIFI;
                }
            }
        }
        return NetType.NONE;
    }
}
