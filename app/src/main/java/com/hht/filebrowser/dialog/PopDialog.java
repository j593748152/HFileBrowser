package com.hht.filebrowser.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.hht.filebrowser.media.MusicDialog;
import com.hht.uc.FileBrowser.R;
import com.hht.widget.CusImageButton;

import java.io.File;

public class PopDialog {
    private PopupWindow mPopupWindow;
    private Context mContext;
    private LayoutInflater mInflater;
    private LinearLayout mLayout;

    public PopDialog(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        LinearLayout linearLayout = (LinearLayout) mInflater.inflate(R.layout.open_dialog_layout, null);
        mPopupWindow = new PopupWindow(linearLayout, 639, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mLayout = (LinearLayout) linearLayout.findViewById(R.id.layout);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void addViewByResolveInfo(ResolveInfo info, final Intent intent) {


        String pn = info.activityInfo.packageName;

        Log.d("wjq", "addViewByResolveInfo:  activityinfo" +pn);

        if("com.rcreations.send2printer".equals(pn)){
            //Do Nothing

        }else{
            LinearLayout ll = (LinearLayout) mInflater.inflate(R.layout.open_item_layout, null);
            Resources resources = mContext.getResources();
            CusImageButton button = null;
            Drawable drawable = null;
            String title = null;

            if("com.android.gallery3d".equals(pn)){
                drawable = resources.getDrawable(R.drawable.view_gallery2);
                title = info.activityInfo.loadLabel(mContext.getPackageManager()).toString();
            } else if("com.mobisystems.office".equals(pn)){
                drawable = resources.getDrawable(R.drawable.view_office);
                title = info.activityInfo.loadLabel(mContext.getPackageManager()).toString();
            }else if("com.jrm.localmm".equals(pn)){
                drawable = resources.getDrawable(R.drawable.view_localmm);
                title = info.activityInfo.loadLabel(mContext.getPackageManager()).toString();
            }else if("com.hht.uc.FileBrowser".equals(pn)){
                drawable = resources.getDrawable(R.drawable.view_folder);
                title = "Folder";
            }else if("com.hht.whiteboard".equals(pn)){
                drawable = resources.getDrawable(R.drawable.view_vboard);
                title = info.activityInfo.loadLabel(mContext.getPackageManager()).toString();
            }else if("com.android.settings".equals(pn)){
                drawable = resources.getDrawable(R.drawable.view_setting);
                title = info.activityInfo.loadLabel(mContext.getPackageManager()).toString();
            }else if("com.android.music".equals(pn)){
                drawable = resources.getDrawable(R.drawable.view_music);
                title = info.activityInfo.loadLabel(mContext.getPackageManager()).toString();
            }else{
                drawable = info.loadIcon(mContext.getPackageManager());
                title = info.activityInfo.loadLabel(mContext.getPackageManager()).toString();
            }
            button = new CusImageButton(mContext, drawable, title, true);
            mLayout.addView(button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(intent);
                    mPopupWindow.dismiss();
                }
            });
        }
    }

    /**
     * 将文件浏览器自带的音频播放功能添加进去
     *
     * @param file
     */
    public void addViewPlayMusic(final File file) {
        LinearLayout ll = (LinearLayout) mInflater.inflate(R.layout.open_item_layout, null);
        ImageView iv = (ImageView) ll.findViewById(R.id.iv);
        TextView tv = (TextView) ll.findViewById(R.id.tv);
        iv.setImageResource(R.drawable.ic_launcher);
        tv.setText(mContext.getString(R.string.audio));
        mLayout.addView(ll);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicDialog dialog = new MusicDialog(mContext, file, R.style.CustomDialogTheme);
                dialog.show();
                mPopupWindow.dismiss();
            }
        });
    }

    public void show(View view) {
        if (mPopupWindow != null) {
            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAsDropDown(view, -getPopWidth() / 3, 0);
            }
        }
    }

    public void dismiss() {
        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            }
        }
    }

    public void showAt(View view, int offX, int offY) {
        if (mPopupWindow != null) {
            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAsDropDown(view, offX - getPopWidth() / 2, offY);
            }
        }
    }

    public int getPopWidth() {
        mPopupWindow.getContentView().measure(0, 0);
        int width = mPopupWindow.getContentView().getMeasuredWidth();
        return width;
    }
}
