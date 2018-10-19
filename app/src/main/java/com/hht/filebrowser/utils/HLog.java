package com.hht.filebrowser.utils;

import android.util.Log;

public class HLog{
    private static boolean mIsDebug = true;
    public static void e(String message){
        if(mIsDebug){
            Log.e("liushuaikang", message);
        }
    }
}
