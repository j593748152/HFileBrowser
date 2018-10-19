package com.hht.filebrowser.files;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class BuilderUtil {
    private static AlertDialog.Builder mBuilder;

    //构建提示信息对话框
    public static void buildInfo(Activity activity,String title,String message){
        mBuilder = new AlertDialog.Builder(activity);
        mBuilder.setTitle(title);
        mBuilder.setMessage(message);
        mBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.create();
        mBuilder.show();
    }

    //获取builder
    public static AlertDialog.Builder getBuilder(Context context,String title,String message){
        mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle(title);
        mBuilder.setMessage(message);
        return mBuilder;
    }

    //设置取消按钮
    public static void setNegativeButton(AlertDialog.Builder builder){
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }
}
