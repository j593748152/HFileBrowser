package com.hht.filebrowser.files;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by hthwx on 2014/11/25.
 */
public class LocalStorageSharePreference extends ContextWrapper {
    private Map mMap;
    private SharedPreferences mShare;
    SharedPreferences.Editor mEditor;

    public LocalStorageSharePreference(Context base) {
        super(base);
    }

    public LocalStorageSharePreference(Context base, String fileName, int mode) {
        super(base);
        this.mShare = this.getSharedPreferences(fileName, mode);
        mEditor = mShare.edit();
    }

    public void insertString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public void insertSetValue(String key, Set<String> value) {
        mEditor.putStringSet(key, value);
        mEditor.commit();
    }

    public void removeSetValue(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

    public String getStringValue(String key, String result) {
        result = mShare.getString(key, result);
        return result;
    }

    public Set<String> getSetValue(String key, Set<String> result) {
        result = mShare.getStringSet(key, result);
        return result;
    }

    public void clear() {
        mEditor.clear();
    }

    // 根据文件名获取本地存储数据
    public TreeMap<String, String> getLocalStorageTreeMap() {
        mMap = (Map<String, String>) mShare.getAll();
        TreeMap<String, String> treeMap = new TreeMap<String, String>(mMap);
        return treeMap;
    }
}
