package com.hht.filebrowser.receiver;

import java.util.List;

import com.hht.uc.FileBrowser.FFileMainActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class UsbReceiver extends BroadcastReceiver {
    private final String CLASS_NAME_FILEBROWSER = "com.hht.uc.FileBrowser.FFileMainActivity";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isForeground(context, CLASS_NAME_FILEBROWSER)) {
            Intent start = new Intent(context,FFileMainActivity.class);
            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(start);
        }
    }

    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
