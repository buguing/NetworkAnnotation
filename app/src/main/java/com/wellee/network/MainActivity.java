package com.wellee.network;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.wellee.library.NetworkManager;
import com.wellee.library.annotation.Network;
import com.wellee.library.constant.Constant;
import com.wellee.library.type.NetType;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetworkManager.getDefault().registerObserver(this);
    }

    @Network(netType = NetType.AUTO)
    public void network(NetType netType) {
        switch (netType) {
            case WIFI:
                Log.e(Constant.LOG_TAG, "MainActivity网络类型：WiFi");
                break;
            case CMNET:
            case CMWAP:
                Log.e(Constant.LOG_TAG, "MainActivity网络连接成功，网络类型：" + netType.name());
                break;
            case NONE:
                Log.e(Constant.LOG_TAG, "MainActivity没有网络");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.getDefault().unRegisterObserver(this);
        NetworkManager.getDefault().unRegisterAllObserver();
    }
}
