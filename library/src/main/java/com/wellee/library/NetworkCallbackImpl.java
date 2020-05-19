package com.wellee.library;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import androidx.annotation.NonNull;

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
 * 创建日期 : 2019/12/26 17:04
 * 邮   箱 : liwei@worken.cn
 * 功能描述 :
 */
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

    private Map<Object, List<MethodManager>> networkMap;

    public NetworkCallbackImpl() {
        networkMap = new HashMap<>();
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        Log.e(Constant.LOG_TAG, "onAvailable已连接");
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        Log.e(Constant.LOG_TAG, "onLost已中断");
        if (!NetworkUtils.isNetworkAvailable()) {
            post(NetType.NONE);
        }
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.e(Constant.LOG_TAG, "onCapabilitiesChanged 网络状态发生更改，类型WIFI");
                post(NetType.WIFI);
            } else {
                Log.e(Constant.LOG_TAG, "onCapabilitiesChanged 网络状态发生更改，类型CELLULAR");
                post(NetworkUtils.getNetType());
            }
        }
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
            com.wellee.library.annotation.Network network = method.getAnnotation(com.wellee.library.annotation.Network.class);
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
}
