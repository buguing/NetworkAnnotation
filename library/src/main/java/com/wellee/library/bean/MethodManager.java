package com.wellee.library.bean;

import com.wellee.library.type.NetType;

import java.lang.reflect.Method;

/**
 * @author : liwei
 * 创建日期 : 2019/12/26 14:50
 * 邮   箱 : liwei@worken.cn
 * 功能描述 :
 */
public class MethodManager {
    // 方法
    private Method method;
    // 方法参数
    private Class<?> type;
    // 注解参数
    private NetType netType;

    public MethodManager(Method method, Class<?> type, NetType netType) {
        this.method = method;
        this.type = type;
        this.netType = netType;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getType() {
        return type;
    }

    public NetType getNetType() {
        return netType;
    }
}
