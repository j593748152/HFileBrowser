package com.hht.filebrowser.media;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.hht.uc.FileBrowser.R;
import com.hht.filebrowser.adapter.GalleryAdapter;
import com.hht.filebrowser.media.model.DataLoad;
import com.hht.filebrowser.files.FileText;
import com.hht.filebrowser.files.FileTextManager;
import com.hht.filebrowser.files.OpenFile;
import com.hht.filebrowser.view.PicturePager;
import com.hht.filebrowser.view.ZoomImageView;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageViewer extends Activity implements OnClickListener, OnItemSelectedListener, OnItemClickListener, OnPageChangeListener {
    private Gallery mGallery;
    private List<String> cFlies = new ArrayList<String>();
    private PictureFileFlter fileFlter;
    private int mCurrentPos = -1;// 当前的item
    private PicturePager mViewPager;
    private ImageView[] mImageViews;
    private boolean isShow = false;
    private Animation anim;
    private LinearLayout mSuspendMenu;
    private LinearLayout llMenu;
    private LinearLayout mRotation;
    private LinearLayout mPrint;
    private LinearLayout mExit;
    private int mChangePosition = -1;
    public PagerAdapter mPagerAdapter;
    private String mFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_show);
        fileFlter = new PictureFileFlter();
        for (FileText fileText : FileTextManager.getInstance().getFileTextList()) {
            File file = new File(fileText.getFilePath());
            if (fileFlter.accept(file)) {
                cFlies.add(fileText.getFilePath());
            }
        }
        mGallery = (Gallery) findViewById(R.id.gallery);
        mViewPager = (PicturePager) findViewById(R.id.id_viewpager);
        mSuspendMenu = (LinearLayout) findViewById(R.id.suspend_menu);
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        mRotation = (LinearLayout) findViewById(R.id.operation_rotation);
        mPrint = (LinearLayout) findViewById(R.id.operation_print);
        mExit = (LinearLayout) findViewById(R.id.operation_exit);
        mRotation.setOnClickListener(this);
        mPrint.setOnClickListener(this);
        mExit.setOnClickListener(this);

        mImageViews = new ImageView[4];
        mViewPager.setAdapter(mPagerAdapter = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                int i = position % 4;
                final ZoomImageView imageView = new ZoomImageView(
                        getApplicationContext());
                imageView.setListener(new ZoomImageView.ItemOnClicklistener() {
                    @Override
                    public void ItemClick() {
                        showOrHideGallery();
                    }
                });
                container.addView(imageView);
                Glide.with(ImageViewer.this).load(cFlies.get(position)).into(imageView);
                //loadImage(imageView, position);
                mImageViews[i] = imageView;
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
//                ZoomImageView imageView = (ZoomImageView) object;
//                realeaseImage(imageView);
//                container.removeView((ZoomImageView) object);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return cFlies.size();
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }
        });
        mViewPager.setOnPageChangeListener(this);
    }
    private final int LOAD_IMAGE_MESSAGE = 1;
    private android.os.Handler mHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOAD_IMAGE_MESSAGE:
                    mViewPager.setCurrentItem(mCurrentPos);
                    break;
            }
        }
    };
    /***
     * 释放ImageView的BitMap对象，减少内存使用
     * @param imageView
     */
    private void realeaseImage(ZoomImageView imageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        if (bitmapDrawable != null) {
            Bitmap bm = bitmapDrawable.getBitmap();
            if (bm != null && !bm.isRecycled()) {
                imageView.setImageResource(0);
                bm.recycle();
                bm = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("path");
        if(mCurrentPos == -1){
            mCurrentPos = cFlies.indexOf(mFilePath);
        }
        showFile(cFlies);
    }

    @Override
    protected void onPause() {
        isShow = false;
        super.onPause();
    }

    /**
     * AsyncTask异步加载图片
     *
     * @param imageView
     * @param position
     */
    private void loadImage(final ZoomImageView imageView, int position) {
        DataLoad load = new DataLoad(imageView, cFlies.get(position));
        new AsyncTask<DataLoad, Bitmap, Bitmap>() {
            private ImageView mImageView;

            @Override
            protected Bitmap doInBackground(DataLoad... params) {
                mImageView = params[0].getView();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(params[0].getPath(), options);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                mImageView.setImageBitmap(bitmap);
            }
        }.execute(load);
    }

    public class PictureFileFlter implements FileFilter {
        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return false;
            } else {
                String fileName = file.getName();
                if (OpenFile.checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingImageJPG))
                        || OpenFile.checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingImageBMP))
                        || OpenFile.checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingImagePNG))
                        || OpenFile.checkEndsWithInStringArray(fileName, getResources()
                        .getStringArray(R.array.fileEndingImageGIF))) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    private void showOrHideGallery() {
        if (isShow) {
            anim = AnimationUtils.loadAnimation(ImageViewer.this, R.anim.option_hide);
            anim.setDuration(300);
            mSuspendMenu.startAnimation(anim);
            mGallery.startAnimation(anim);
            llMenu.startAnimation(anim);
            mSuspendMenu.setVisibility(View.GONE);
        } else {
            anim = AnimationUtils.loadAnimation(ImageViewer.this, R.anim.option_show);
            anim.setDuration(300);
            mSuspendMenu.startAnimation(anim);
            mGallery.startAnimation(anim);
            llMenu.startAnimation(anim);
            mSuspendMenu.setVisibility(View.VISIBLE);
        }
        isShow = !isShow;
    }

    private void showFile(List<String> cFlies) {
        GalleryAdapter adapter = new GalleryAdapter(ImageViewer.this, cFlies, mGallery);
        mGallery.setAdapter(adapter);
        mGallery.setCallbackDuringFling(false);
        mGallery.setOnItemSelectedListener(this);
        mGallery.setSelection(mCurrentPos);// 设置一加载Activity就显示的图片为当前图片
        mViewPager.setCurrentItem(mCurrentPos);
        mGallery.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(Math.abs(mCurrentPos - position) < 4){
            mCurrentPos = position;
            mViewPager.setCurrentItem(position);
        }else {
            mCurrentPos = position;
            mHandler.sendEmptyMessageDelayed(LOAD_IMAGE_MESSAGE,1000);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (ZoomImageView.isOperation) {
            mPagerAdapter.notifyDataSetChanged();
            ZoomImageView.isOperation = false;
        }
        mGallery.setSelection(position);
        mCurrentPos = position;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.operation_rotation:
                mChangePosition = mViewPager.getCurrentItem() % 4;
                rotation(mImageViews[mChangePosition]);
                break;
            case R.id.operation_print:
                broadcastToPrint(cFlies.get(mCurrentPos));
                break;
            case R.id.operation_exit:
                finish();
                break;

            default:
                break;
        }
    }
    private ArrayList<String> mPrintList;
    private void broadcastToPrint(String strFilePath)
    {
        if(null == mPrintList){
            mPrintList = new ArrayList<String>();
        }
        mPrintList.clear();
        mPrintList.add(strFilePath);
        File f4 = new File(strFilePath);
        Uri u = Uri.fromFile(f4);
        Intent intent = new Intent("com.hht.printer.prepare");
        ComponentName comp = new ComponentName(
                "com.android.settings",
                "com.android.settings.printer.PrintPreviewActivity");
        if(strFilePath != null ){
            intent.putExtra("going_to_print_list",mPrintList );
        }
        intent.setComponent(comp);
        intent.setData(u);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
    /**
     * 旋转ImageView
     *
     * @param image
     */
    private void rotation(ImageView image) {
        ZoomImageView zoom = (ZoomImageView) image;
        zoom.Rotation();
    }
}
