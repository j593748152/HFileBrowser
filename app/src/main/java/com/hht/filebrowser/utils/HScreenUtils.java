package com.hht.filebrowser.utils;

import java.io.File;
import java.lang.ref.WeakReference;
import com.hht.uc.FileBrowser.R;
import com.hht.hardware.impl.HTVManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class HScreenUtils {
    private final static String SYS_BACK_PATH = "/system/edu/sysback/";
    private final static String SYS_BACK_PATH_OVERSEA = "/systema/sysback/";

    private static HScreenUtils mInstance;
    private Bitmap mCurBackground = null;
    private Bitmap mPreBackground = null;

    private HScreenUtils() {}

    public static HScreenUtils getInstance() {
        if (mInstance == null) {
            mInstance = new HScreenUtils();
        }
        return mInstance;
    }

    public void setSysBackground(Context context, View view) {
        String imagePath = Settings.System.getString(context.getContentResolver(), "sysback");
        if (imagePath != null) {
            mPreBackground = mCurBackground;
            mCurBackground = convertToBitmap(imagePath, HScreenUtils.getScreenWidth(context),
                    HScreenUtils.getScreenHeight(context));
            if (mCurBackground != null) {
                view.setBackground(new BitmapDrawable(mCurBackground));
            } else {
//                view.setBackgroundResource(R.drawable.home_wallpaper_background_edu);
            }
            if (mPreBackground != null) {
                mPreBackground.recycle();
                mPreBackground = null;
            }
        } else {
//            view.setBackgroundResource(R.drawable.home_wallpaper_background_edu);
        }
    }

    private Bitmap convertToBitmap(String path, int w, int h) {
        File imgFile = new File(path);
        if (!imgFile.exists()) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

    public void bitmapRecycle() {
        if (mCurBackground != null) {
            mCurBackground.recycle();
            mCurBackground = null;
        }
    }
    /**
     * 获得屏幕高度
     * 
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     * 
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
