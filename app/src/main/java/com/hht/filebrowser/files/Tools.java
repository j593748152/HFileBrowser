package com.hht.filebrowser.files;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.util.Log;

import com.mstar.android.storage.MStorageManager;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tools {
	public static String getFileDate(long date) {
		Date result = new Date(date);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d");
		return df.format(result);
	}

	public static String getFileFullName(String url) {
		try {
			URL u=new URL(url);
			String result=u.getPath();
			if (result.contains("?")) {
				return result.substring(result.lastIndexOf('/') + 1,
						result.lastIndexOf('?'));
			} else
				return result.substring(result.lastIndexOf('/') + 1, result.length());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static long getDirectorySize(File f) {
		long size = 0;
		try {
			if (f.exists()) {
				if (f.isDirectory()) {
					File flist[] = f.listFiles();
					for (File aFlist : flist) {
						size = size + getDirectorySize(aFlist);
					}
				} else {
					size = size + f.length();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	public static String FormetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.##");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "GB";
		}
		return fileSizeString;
	}

	public static String getUsbDeviceLabel(Context context, String strMountPath) {
		String strLabel = "";
		MStorageManager sm = MStorageManager.getInstance(context);
		if (Environment.MEDIA_MOUNTED.equals(sm.getVolumeState(strMountPath))) {
			strLabel = sm.getVolumeLabel(strMountPath);
		}
		if ((strLabel == null) || strLabel.isEmpty()) {
			int subInt = strMountPath.lastIndexOf("/");
			strLabel = strMountPath.substring(subInt + 1);
		}

		return strLabel;
	}

	public static List<String> getStoragePaths(Context cxt) {
		List<String> pathsList = new ArrayList<String>();

		StorageManager storageManager = (StorageManager) cxt.getSystemService(Context.STORAGE_SERVICE);
		try {
			Method method = StorageManager.class.getDeclaredMethod("getVolumePaths");
			method.setAccessible(true);
			Object result = method.invoke(storageManager);
			if (result != null && result instanceof String[]) {
				String[] pathes = (String[]) result;
				StatFs statFs;
				for (String path : pathes) {
					if (!TextUtils.isEmpty(path) && new File(path).exists()) {
						statFs = new StatFs(path);
						if (statFs.getBlockCount() * statFs.getBlockSize() != 0) {
							pathsList.add(path);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			File externalFolder = Environment.getExternalStorageDirectory();
			if (externalFolder != null) {
				pathsList.add(externalFolder.getAbsolutePath());
			}
		}
		return pathsList;
	}

	public static long getAvailableSize(File file){
		long availSize = 0;
		StatFs stat = new StatFs(file.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		availSize = availableBlocks * blockSize;
		return availSize;
	}

	public static String getFileName(String pathandname){

		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if(end != -1){
			return pathandname.substring(start + 1,end);
		}else if (start != -1){
			return pathandname.substring(start + 1, pathandname.length());
		}
		else {
			return "";
		}
	}

	public static String getSuffix(String pathandname){
		int start = pathandname.lastIndexOf(".");
		if(start != -1){
			return pathandname.substring(start, pathandname.length());
		}else{
			return "";
		}
	}
	public static boolean deleteFolder(File folder) {
        boolean result;
        try {
            String[] children = folder.list();
            if (children == null || children.length <= 0) {
                result = folder.delete();
            } else {
                for (String childName : children) {
                    String childPath = folder.getPath() + File.separator + childName;
                    File filePath = new File(childPath);
                    if (filePath.exists() && filePath.isFile()) {
                        result = filePath.delete();
                    } else if (filePath.exists() && filePath.isDirectory()) {
                        result = deleteFolder(filePath);
                    }
                }
                result = folder.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
	
	 public static void mkdir() {
	        File file = new File("/data/media/0", "Pictures");
	        if (!file.exists()) {
	            file.mkdirs();
	        }
	        File fileshots = new File("/data/media/0/Pictures", "Screenshots");
	        if (!fileshots.exists()) {
	            fileshots.mkdirs();
	        }
	    }

}
