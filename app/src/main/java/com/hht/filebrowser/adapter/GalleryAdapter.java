package com.hht.filebrowser.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import com.hht.filebrowser.imageload.NativeImageLoader;
import com.hht.uc.FileBrowser.R;

import java.util.List;

/**
 * Created by admin on 2017/4/12.
 */

public class GalleryAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private List<String> files;
    private Context mContext;
    private Gallery mGallerty;

    public GalleryAdapter(Context context, List<String> files, Gallery gallery) {
        super();
        mContext = context;
        this.files = files;
        mGallerty = gallery;
        TypedArray typedArray = mContext.obtainStyledAttributes(R.styleable.Gallery);
        // 设置边框的样式
        mGalleryItemBackground = typedArray.getResourceId(R.styleable.Gallery_android_galleryItemBackground, 0);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int index = position % 8;
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = new ImageView(mContext);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.imageView.getDrawable();
            if (bitmapDrawable != null) {
                Bitmap bm = bitmapDrawable.getBitmap();
                if (bm != null && !bm.isRecycled()) {
                    holder.imageView.setImageResource(0);
                    bm.recycle();
                }
            }
        }
        holder.imageView.setBackgroundResource(R.drawable.gallery_item_background);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.imageView.setLayoutParams(new Gallery.LayoutParams(207, 112));
        holder.imageView.setTag(files.get(position));
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(files.get(position), 207, 112, new NativeImageLoader.NativeImageCallBack() {
            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                ImageView mImageView = (ImageView) mGallerty.findViewWithTag(path);
                if (mImageView != null) {
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });
        if (bitmap == null) {
            holder.imageView.setImageResource(R.drawable.image_loadding);
        } else {
            holder.imageView.setImageBitmap(bitmap);
        }
        return convertView;
    }

    public static class ViewHolder {
        public ImageView imageView;
    }
}
