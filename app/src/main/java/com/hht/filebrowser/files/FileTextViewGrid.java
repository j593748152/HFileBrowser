package com.hht.filebrowser.files;

import com.hht.uc.FileBrowser.R;
import com.hht.skin.main.SkinManager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 娑擄拷娑擃亜鐢崶鐐垼閻ㄥ嫭鏋冩禒鎯邦潒閸ワ拷
 * 
 * @author Administrator
 *
 */
public class FileTextViewGrid extends LinearLayout {

    private LayoutInflater mInflater;
    private Context mContext;
    private View mFileGrid;
    private TextView mText;


    private ImageView mIcon;
    private ImageView mIvSelect;

    public FileTextViewGrid(Context context) {
        super(context);
    }

    public FileTextViewGrid(Context context, FileText fileText) {
        super(context);
        this.mContext = context;
        this.setOrientation(VERTICAL);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFileGrid = mInflater.inflate(R.layout.layout_file_grid, this);
        mIcon = (ImageView) mFileGrid.findViewById(R.id.iv_file_image_grid);
        mText = (TextView) mFileGrid.findViewById(R.id.tv_file_name_grid);
        mText.setText(fileText.getText());
        mText.setSingleLine(true);
        mIvSelect = (ImageView) mFileGrid.findViewById(R.id.iv_file_select_grid);
    }

    public void setText(String text) {
        this.mText.setText(text);
    }

    public TextView getText() {
        return mText;
    }

    public void setText(TextView text) {
        this.mText = text;
    }

    public ImageView getIcon() {
        return mIcon;
    }

    public void setIcon(ImageView icon) {
        this.mIcon = icon;
    }

    public void setIcon(Drawable icon) {
        this.mIcon.setImageDrawable(icon);
    }

    public ImageView getIvSelect() {
        return mIvSelect;
    }

    public void setIvSelect(ImageView ivSelect) {
        this.mIvSelect = ivSelect;
    }

    public void setIvSelect(Drawable ivSelect) {
        this.mIvSelect.setImageDrawable(ivSelect);
    }

    public void changeVisibility(Boolean b, boolean isOperation) {
        if (isOperation) {
            mIvSelect.setVisibility(VISIBLE);
            if (b) {
                mIvSelect.setImageResource(R.drawable.file_selected);
            } else {
                mIvSelect.setImageResource(R.drawable.file_no_selected);
            }
        } else {
            mIvSelect.setVisibility(GONE);
        }
    }
}
