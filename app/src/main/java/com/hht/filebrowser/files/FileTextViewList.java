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
 * 涓�涓甫鍥炬爣鐨勬枃浠惰鍥�
 * 
 * @author Administrator
 *
 */
public class FileTextViewList extends LinearLayout {
    private View mView;
    private Context mContext;
    private TextView mTvFileName;
    private TextView mTvFileSize;
    private TextView mTvFileDate;
    private ImageView mIvFileIcon;
    private ImageView mIvFileSelect;
    public FileTextViewList(Context context) {
        super(context);
    }

    public FileTextViewList(Context context, FileText fileText) {
        super(context);
        mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_file_list, this);
        mIvFileIcon = (ImageView) findViewById(R.id.iv_file_icon);
        mTvFileName = (TextView) findViewById(R.id.tv_file_name);
        mTvFileName.setText(fileText.getText());
        mTvFileName.setSingleLine(true);
        mTvFileSize = (TextView) findViewById(R.id.tv_file_size);
        mTvFileSize.setText(fileText.getFileSize());
        mTvFileDate = (TextView) findViewById(R.id.tv_file_date);
        mTvFileDate.setText(fileText.getFileDate());
        mIvFileSelect = (ImageView) findViewById(R.id.iv_file_select);
    }

    public ImageView getIvFileSelected() {
        return mIvFileSelect;
    }

    public void setText(String text) {
        this.mTvFileName.setText(text);
    }

    public TextView getText() {
        return mTvFileName;
    }

    public void setText(TextView text) {
        this.mTvFileName = text;
    }

    public ImageView getIcon() {
        return mIvFileIcon;
    }

    public void setIcon(ImageView icon) {
        this.mIvFileIcon = icon;
    }

    public void setIcon(Drawable icon) {
        this.mIvFileIcon.setImageDrawable(icon);
    }

    public void setFileSize(String fileSize) {
        this.mTvFileSize.setText(fileSize);
    }

    public void setFileDate(String fileDate) {
        this.mTvFileDate.setText(fileDate);
    }

    public void changeVisibility(Boolean b ,boolean isOperation) {
        if(isOperation){
            mIvFileSelect.setVisibility(VISIBLE);
           if(b){
               mIvFileSelect.setImageResource(R.drawable.file_selected);
           }else {
               mIvFileSelect.setImageResource(R.drawable.file_no_selected);
           }
        }else {
            mIvFileSelect.setVisibility(GONE);
        }
    }

    public void changeSelectViewState(boolean b) {
        mIvFileSelect.setVisibility(VISIBLE);
        if (b) {
            mIvFileSelect.setImageResource(R.drawable.file_selected);
        } else {
            mIvFileSelect.setImageResource(R.drawable.ic_launcher);
        }
    }

    public void changeFileIconMargin() {
        LayoutParams params = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.list_icon_width),
                getResources().getDimensionPixelSize(R.dimen.list_icon_width));
        params.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.margin_15px), 0);
        mIvFileIcon.setLayoutParams(params);
    }
}
