package com.wellee.library;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wellee.library.annotation.Network;
import com.wellee.library.bean.MethodManager;
import com.wellee.library.constant.Constant;
import com.wellee.library.type.NetType;
import com.wellee.library.util.NetworkUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author : liwei
 * 创建日期 : 2019/12/26 13:32
 * 邮   箱 : liwei@worken.cn
 * 功能描述 :
 */
public class NetStateReceiver extends BroadcastReceiver {

    private NetType netType;
    private Map<Object, List<MethodManager>> networkMap;

    public NetStateReceiver() {
        netType = NetType.NONE;
        networkMap = new HashMap<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            Log.e(Constant.LOG_TAG, "未知广播异常");
            return;
        }

        if (intent.getAction().equalsIgnoreCase(Constant.ANDROID_NET_CHANGE_ACTION)) {
            Log.e(Constant.LOG_TAG, "网络状态改变");
            netType = NetworkUtils.getNetType();
            if (NetworkUtils.isNetworkAvailable()) {
                Log.e(Constant.LOG_TAG, "网络连接成功");
            } else {
                Log.e(Constant.LOG_TAG, "无网络连接");
            }
        }
        post(netType);
    }

    private void post(NetType netType) {
        Set<Object> objects = networkMap.keySet();
        for (Object object : objects) {
            List<MethodManager> methodManagers = networkMap.get(object);
            if (methodManagers != null) {
                for (MethodManager methodManager : methodManagers) {
                    Class<?> parameterType = methodManager.getType();
                    if (parameterType.isAssignableFrom(netType.getClass())) {
                        switch (methodManager.getNetType()) {
                            case AUTO:
                                invoke(methodManager.getMethod(), object, netType);
                                break;
                            case WIFI:
                                if (netType == NetType.WIFI || netType == NetType.NONE) {
                                    invoke(methodManager.getMethod(), object, netType);
                                }
                                break;
                            case CMNET:
                                if (netType == NetType.CMNET || netType == NetType.NONE) {
                                    invoke(methodManager.getMethod(), object, netType);
                                }
                                break;
                            case CMWAP:
                                if (netType == NetType.CMWAP || netType == NetType.NONE) {
                                    invoke(methodManager.getMethod(), object, netType);
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    private void invoke(Method method, Object object, NetType netType) {
        try {
            method.invoke(object, netType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerObserver(Object object) {
        List<MethodManager> methodList = networkMap.get(object);
        if (methodList == null) {
            methodList = findAnnotationMethod(object);
            networkMap.put(object, methodList);
        }
    }

    private List<MethodManager> findAnnotationMethod(Object object) {
        List<MethodManager> methodManagers = new ArrayList<>();

        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Network network = method.getAnnotation(Network.class);
            if (network != null) {
                Type returnType = method.getGenericReturnType();
                if (!"void".equals(returnType.toString())) {
                    throw new IllegalArgumentException(method.getName() + "the method return type must be void");
                }

                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1) {
                    throw new IllegalArgumentException(method.getName() + "the number of arguments to the method must be 1");
                }
                MethodManager methodManager = new MethodManager(method, parameterTypes[0], network.netType());
                methodManagers.add(methodManager);
            }
        }
        return methodManagers;
    }

    public void unRegisterObserver(Object object) {
        if (!networkMap.isEmpty()) {
            networkMap.remove(object);
            Log.e(Constant.LOG_TAG, object.getClass().getName() + "注销成功");
        }
    }

    public void unRegisterAllObserver() {
        if (!networkMap.isEmpty()) {
            networkMap.clear();
        }
        NetworkManager.getDefault().getApplication().unregisterReceiver(this);
        networkMap = null;
        Log.e(Constant.LOG_TAG, "注销所有成功");
    }
}
