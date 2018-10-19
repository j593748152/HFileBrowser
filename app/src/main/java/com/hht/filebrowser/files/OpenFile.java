package com.hht.filebrowser.files;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.io.File;
import java.util.List;

import com.hht.filebrowser.dialog.PopDialog;

/*
 * 
 * 鎵撳紑鏂囦欢
 * */
public class OpenFile {
	public static Intent getHtmlFileIntent(File file) {
		Uri uri = Uri.parse(file.toString()).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(file.toString()).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	public static PopDialog getImageFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "image/*");
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(uri, "image/*");
            i.putExtra("path", file.getAbsolutePath());
            dialog.addViewByResolveInfo(info, i);
        }
		return dialog;
	}

	public static PopDialog getPdfFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/pdf");
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(uri, "application/pdf");
            dialog.addViewByResolveInfo(info, i);
            if(resInfo.size()==1){
               context.startActivity(i);
               return null;
            }
        }
        return dialog;
	}

	public static PopDialog getTextFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "text/plain");
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(uri, "text/plain");
            dialog.addViewByResolveInfo(info, i);
            if(resInfo.size()==1){
                context.startActivity(i);
                return null;
             }
        }
        return dialog;
	}

	public static PopDialog getAudioFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "audio/*");
		PopDialog dialog = new PopDialog(context);
		dialog.addViewPlayMusic(file);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("path", file.getAbsolutePath());
            i.putExtra("oneshot", 0);
            i.putExtra("configchange", 0);
            i.setDataAndType(uri, "audio/*");
            if(resInfo.size()==1){
                context.startActivity(i);
                return null;
             }
            dialog.addViewByResolveInfo(info, i);

        }
        return dialog;
	}

	public static PopDialog getVideoFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "video/*");
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("oneshot", 0);
            i.putExtra("configchange", 0);
            i.putExtra("path", file.getAbsolutePath());
            i.setDataAndType(uri, "video/*");
            if(resInfo.size()==1){
                context.startActivity(i);
                return null;
             }
            dialog.addViewByResolveInfo(info, i);
        }
        return dialog;
	}

	public static PopDialog getChmFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/x-chm");
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(uri, "application/x-chm");
            if(resInfo.size()==1){
                context.startActivity(i);
                return null;
             }
            dialog.addViewByResolveInfo(info, i);
        }
        return dialog;
	}

	public static PopDialog getWordFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/msword");
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(uri, "application/msword");
            if(resInfo.size()==1){
                context.startActivity(i);
                return null;
             }
            dialog.addViewByResolveInfo(info, i);
        }
        return dialog;
	}

	public static PopDialog getExcelFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(uri, "application/vnd.ms-excel");
            if(resInfo.size()==1){
                context.startActivity(i);
                return null;
             }
            dialog.addViewByResolveInfo(info, i);
        }
        return dialog;
	}

	public static PopDialog getPPTFileIntent(Context context,File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(file);
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = new Intent();
            i.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            i.setAction(Intent.ACTION_VIEW);
            i.addCategory("android.intent.category.DEFAULT");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(uri, "application/vnd.ms-powerpoint");
            if(resInfo.size()==1){
                context.startActivity(i);
                return null;
             }
            dialog.addViewByResolveInfo(info, i);
        }
        return dialog;
	}

	public static Intent getApkFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		return intent;
	}
	public static boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.toLowerCase().endsWith(aEnd.toLowerCase()))
				return true;
		}
		return false;
	}

	public static PopDialog getOtherFileIntent(Context context,File file) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PopDialog dialog = new PopDialog(context);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY | 0);
        for (ResolveInfo info : resInfo) {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(info.activityInfo.packageName);
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            if(resInfo.size()==1){
                context.startActivity(i);
                return null;
             }
            dialog.addViewByResolveInfo(info, i);
        }
        return dialog;
	}

}
