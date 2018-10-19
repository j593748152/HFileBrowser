package com.hht.filebrowser.imageload;

import java.util.ArrayList;

import com.hht.skin.main.SkinManager;
import com.hht.uc.FileBrowser.R;
import com.hht.filebrowser.imageload.NativeImageLoader.NativeImageCallBack;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageLoadAdapter extends BaseAdapter {

    /**
     * 用来存储图片的选中情况
     */
    private ArrayList<ImageView> mSelectList = new ArrayList<ImageView>();
    private GridView mGridView;
    private Context mContext;
    private ArrayList<String> mList;
    private ArrayList<String> mResultList = new ArrayList<String>();
    protected LayoutInflater mInflater;
    private final int IMAGE_WIDTH = 272 * 2 / 3;
    private final int IMAGE_HEIGHT = 153 * 2 / 3;
    private boolean mIsOnlyOne =true;
    public ImageLoadAdapter(Context context, ArrayList<String> list, GridView mGridView,boolean onlyOne) {
        this.mList = list;
        this.mGridView = mGridView;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mIsOnlyOne = onlyOne;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String path = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gridview_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (CustomImageView) convertView.findViewById(R.id.gridview_imageview);
            viewHolder.mSelectedGrid = (ImageView) convertView.findViewById(R.id.gridview_imageview_selected_bac);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView.setImageResource(R.drawable.default_preview_pic);
        }
        viewHolder.mImageView.setTag(path);
        viewHolder.mSelectedGrid.setTag("skin:file_no_selected:src");
        if(mResultList.contains(path)){
            viewHolder.mSelectedGrid.setTag("skin:file_selected:src");
            viewHolder.mIsSelected = true;
        }
        SkinManager.getInstance().injectSkin(viewHolder.mSelectedGrid);
        mSelectList.add(viewHolder.mSelectedGrid);
        viewHolder.mImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(mIsOnlyOne){
                    for(ImageView image : mSelectList){
                        image.setTag("skin:file_no_selected:src");
                        SkinManager.getInstance().injectSkin(image);
                    }
                    viewHolder.mSelectedGrid.setTag("skin:file_selected:src");
                    SkinManager.getInstance().injectSkin(viewHolder.mSelectedGrid);
                    mResultList.clear();
                    mResultList.add(mList.get(position));
                }else {
                    if (viewHolder.mIsSelected) {
                        viewHolder.mSelectedGrid.setTag("skin:file_no_selected:src");
                        SkinManager.getInstance().injectSkin(viewHolder.mSelectedGrid);
                        viewHolder.mIsSelected = false;
                        mResultList.remove(mList.get(position));
                    } else {
                        viewHolder.mSelectedGrid.setTag("skin:file_selected:src");
                        SkinManager.getInstance().injectSkin(viewHolder.mSelectedGrid);
                        viewHolder.mIsSelected = true;
                        mResultList.add(mList.get(position));
                    }
                }
            }
        });
        // 利用NativeImageLoader类加载本地图片
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, IMAGE_WIDTH, IMAGE_HEIGHT,
                new NativeImageCallBack() {
                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {
                        ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
                        if (bitmap != null && mImageView != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });

        if (bitmap != null) {
            viewHolder.mImageView.setImageBitmap(bitmap);
        } else {
            viewHolder.mImageView.setImageResource(R.drawable.default_preview_pic);
        }
        return convertView;
    }
    /**
     * return all selected image paths
     *
     * @return
     */
    public ArrayList<String> getSelectPath() {
        return mResultList;
    }

    public static class ViewHolder {
        private CustomImageView mImageView;
        private ImageView mSelectedGrid;
        private boolean mIsSelected;
    }

}
