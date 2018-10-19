package com.hht.filebrowser.files;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.util.*;

/**
 * Created by yunyayishi on 2014/12/15.
 */
public class FileSharePreference extends LocalStorageSharePreference {
    private SharedPreferences mShare;
    private SharedPreferences mShareSearch;
    SharedPreferences.Editor mEditor;
    SharedPreferences.Editor mEditorSearch;
    private String[] mPictureType = { "jpg", "jpeg", "png", "bmp", "gif" };
    private String[] mMusicType = { "mp3", "wma", "wav", "ogg", "midi" };
    private String[] mVideoType = { "mp4", "mpeg", "rm", "rmvb", "avi", "flv" };
    private String[] mDocumentType = { "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt" };

    public FileSharePreference(Context base) {
        super(base);
    }

    public FileSharePreference(Context base, String fileName, int mode) {
        super(base, fileName, mode);
        this.mShareSearch = this.getSharedPreferences(fileName, mode);
        mEditorSearch = mShareSearch.edit();
    }

    /**
     * //增加文件
     * 
     * @param file
     *            文件
     * @param isNew
     *            boolean 如果true，则新增文件，false，为删除文件
     */
    protected void insertNewFile(File file, boolean isNew) {
        String fileName = file.getName();
        // 更新search文件列表
        String filePath = file.getAbsolutePath();
        if (mShareSearch.contains(filePath)) {
            mEditorSearch.remove(filePath);// 如果存在同路径文件数据，则更新
        }
        if (isNew) {
            mEditorSearch.putString(filePath, fileName);
        }
        mEditorSearch.commit();
        if (file.isFile()) {
            if (fileName.indexOf(".") > 0 && fileName.lastIndexOf(".") + 1 < fileName.length()) {
                // 根据文件格式更新分类数据
                String formatStr = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(); // 获取文件格式
                if (Arrays.asList(mDocumentType).contains(formatStr)) {
                    formatInsert("document", filePath, isNew);
                } else if (Arrays.asList(mPictureType).contains(formatStr)) {
                    formatInsert("picture", filePath, isNew);
                } else if (Arrays.asList(mMusicType).contains(formatStr)) {
                    formatInsert("music", filePath, isNew);
                } else if (Arrays.asList(mVideoType).contains(formatStr)) {
                    formatInsert("video", filePath, isNew);
                }
            }
        }
    }

    // 插入新的文件分类数据
    private void formatInsert(String key, String filePath, boolean isNew) {
        Set<String> tmpSet = new HashSet<String>();
        tmpSet = mShare.getStringSet(key, tmpSet);
        boolean flag = false;
        for (String ss : tmpSet) {
            if (ss.toLowerCase().equals(filePath.toLowerCase())) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            tmpSet.add(filePath);
            mEditor.remove(key);
        }
        if (isNew) {
            mEditor.putStringSet(key, tmpSet);
        }
        mEditor.commit();
    }

    // 根据文件名搜索文件
    public Set<String> searchByKeyWords(String keyWords) {
        Set<String> resultSet = new HashSet<String>();
        Map<String, String> tmpMap = (HashMap<String, String>) mShareSearch.getAll();
        for (String sm : tmpMap.keySet()) {
            if (sm.lastIndexOf("/") > 0) {
                if (sm.lastIndexOf("/") + 1 == sm.length()) {
                    sm = sm.substring(0, sm.lastIndexOf("/"));// 如果最后一位为“/”，则去掉
                }
                String tmpName = sm.substring(sm.lastIndexOf("/"));
                if (tmpName != null && tmpName.indexOf(keyWords) > -1) {
                    resultSet.add(sm);
                }
            }
        }
        return resultSet;
    }

}
