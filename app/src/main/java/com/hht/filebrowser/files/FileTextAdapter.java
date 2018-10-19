package com.hht.filebrowser.files;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hht.filebrowser.imageload.NativeImageLoader;
import com.hht.skin.main.SkinManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 浣跨敤BaseAdapter瀹炵幇ListView甯冨眬
 *
 * @author Administrator
 *
 */
public class FileTextAdapter extends BaseAdapter {

    private Context mContext;
    private List<FileText> mFiles = new ArrayList<FileText>();
    private String mViewType;

    public FileTextAdapter(Context context) {
        super();
        this.mContext = context;
    }

    public FileTextAdapter(Context context, String viewType) {
        super();
        this.mContext = context;
        this.mViewType = viewType;
    }
    @Override
    public int getCount() {
        return mFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return mFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (mViewType.equals("grid")){
            final FileTextViewGrid fileTextViewGrid;
            if(convertView == null){
                fileTextViewGrid = new FileTextViewGrid(mContext, mFiles.get(position));
            }else{
                fileTextViewGrid = (FileTextViewGrid) convertView;
                fileTextViewGrid.setText(mFiles.get(position).getText());
            }
            String path = mFiles.get(position).getFilePath();
            fileTextViewGrid.setTag(path);
            fileTextViewGrid.getIcon().setTag("skin:"+mFiles.get(position).getIconTag()+":src");
            SkinManager.getInstance().injectSkin(fileTextViewGrid);
            Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, 84, 102, new NativeImageLoader.NativeImageCallBack() {
                @Override
                public void onImageLoader(Bitmap bitmap, String path) {
                    ImageView imageView = fileTextViewGrid.getIcon();
                    imageView.setImageBitmap(bitmap);
                }
            });
            if(bitmap!=null){
                fileTextViewGrid.getIcon().setImageBitmap(bitmap);
            }
            fileTextViewGrid.changeVisibility(mFiles.get(position).isSelected(),mFiles.get(position).isOperation());
            return fileTextViewGrid;
        }
        else {
            FileTextViewList fileTextViewList = null;
            if(convertView == null){
                fileTextViewList = new FileTextViewList(mContext, mFiles.get(position));
            }else{
                fileTextViewList = (FileTextViewList) convertView;
                fileTextViewList.setText(mFiles.get(position).getText());
                fileTextViewList.setFileDate(mFiles.get(position).getFileDate());
                fileTextViewList.setFileSize(mFiles.get(position).getFileSize());
            }
            fileTextViewList.getIcon().setTag("skin:"+mFiles.get(position).getIconTag()+":src");
            SkinManager.getInstance().injectSkin(fileTextViewList);
            fileTextViewList.changeVisibility(mFiles.get(position).isSelected(),mFiles.get(position).isOperation());
            return fileTextViewList;
        }
    }
    public void addItem(FileText fileText) {
        mFiles.add(fileText);
    }

    public boolean allFilesSecletable(){
        return false;
    }

    public List<FileText> getFiles() {
        return mFiles;
    }

    public void setFiles(List<FileText> files) {
        this.mFiles = files;
    }
}
