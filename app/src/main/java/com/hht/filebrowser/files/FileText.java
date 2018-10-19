package com.hht.filebrowser.files;

import android.graphics.drawable.Drawable;

/**
 * 鏂囦欢绫�
 *
 * @author Administrator
 */
public class FileText implements Comparable<FileText> {
    // 文件名
    private String mText;
    // 文件图标
    private String mIcon;
    //是否是选中状态
    private boolean mIsSelected = false;
    //是否是操作状态
    private boolean mIsOperation = false;
    //文件大小
    private String mFileSize;
    //文件日期
    private String mFileDate;

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    private String filePath;
    private boolean isFile;

    public FileText(String text, String icon, String fileSize, String fileDate, String filePath, boolean isFile, boolean selected) {
        this(text, icon, fileSize, fileDate, filePath, isFile, selected, false);
    }

    public FileText(String text, String icon, String fileSize, String fileDate, String filePath, boolean isFile, boolean selected, boolean isOperation) {
        super();
        this.mText = text;
        this.mIcon = icon;
        this.mFileDate = fileDate;
        this.mFileSize = fileSize;
        this.filePath = filePath;
        this.isFile = isFile;
        this.mIsSelected = selected;
        mIsOperation = isOperation;
    }

    public String getIconTag() {
        return mIcon;
    }

    public String getText() {
        return mText;
    }

    public void setIconTag(String icon) {
        this.mIcon = icon;
    }

    public void setText(String text) {
        this.mText = text;
    }

    @Override
    public int compareTo(FileText another) {
        if (this.mText != null) {
            return this.mText.compareTo(another.getText());
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getFileDate() {
        return mFileDate;
    }

    public void setFileDate(String fileDate) {
        this.mFileDate = fileDate;
    }

    public String getFileSize() {
        return mFileSize;
    }

    public void setFileSize(String fileSize) {
        this.mFileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        this.mIsSelected = selected;
    }

    public boolean isOperation() {
        return mIsOperation;
    }

    public void setOperation(boolean b) {
        this.mIsOperation = b;
    }
}
