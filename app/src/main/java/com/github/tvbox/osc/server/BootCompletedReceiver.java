package com.github.tvbox.osc.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.tvbox.osc.ui.activity.HomeActivity;

/**
 * 需要收到开启自启动权限，才能接收到开机广播
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 开机后执行的代码
        Intent intent2 = new Intent(context, HomeActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
        Log.e("xxx", "接收到开机广播了");
    }
}
