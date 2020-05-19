package com.wellee.library.listener;

import com.wellee.library.type.NetType;

/**
 * @author : liwei
 * 创建日期 : 2019/12/26 13:39
 * 邮   箱 : liwei@worken.cn
 * 功能描述 :
 */
public interface NetWorkListener {
    void onConnect(NetType type);
    void onDisConnect();
}
