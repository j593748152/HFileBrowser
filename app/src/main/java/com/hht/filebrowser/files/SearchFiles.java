package com.hht.filebrowser.files;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hht.uc.FileBrowser.R;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by hthwx on 2014/11/18.
 */
public class SearchFiles {
    private File[] mResult;
    private String ｍDir;
    private String mKeyWords;
    private List<File> mFileList = new LinkedList<File>();
    private FileFilter mFileFilter;
    private String[] mPictureType = { "jpg", "jpeg", "png", "bmp", "gif" };
    private String[] mMusicType = { "mp3", "wma", "wav", "ogg", "midi", "mid", "mp2", "ac3" };
    private String[] mVideoType = { "mp4", "mpeg", "rm", "rmvb", "avi", "flv", "mpg", "mkv", "3gp", "swf" };
    private String[] mDocumentType = { "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "txt" };
    private String[] mWhiteboardType = {"evg","ewb","iwb"};
    private boolean mIsIterator = true;
    private ProgressDialog mSearchDialog;
    private Context mContext;
    private List<FileText> mFinalResult = new LinkedList<FileText>();
    private String mCurrentIcon = null;
    private String mFileSize = null;
    private String mFileDate = null;
    private int mFileNumber = 1;
    private int mCurrentCount = 0;
    private LocalStorageSharePreference mLocalPreference;
    private Set<String> mTmpSearchResult = new HashSet<String>();
    private Set<String> mResultSetDocument = new HashSet<String>();
    private Set<String> mResultSetPicture = new HashSet<String>();
    private Set<String> mResultSetMusic = new HashSet<String>();
    private Set<String> mResultSetWhiteboard = new HashSet<String>();
    //private Set<String> mResultSetVideo = new HashSet<String>();
    public Set<String> mSearchFileSet = new HashSet<String>();
    private List<FileText> mSearchFileTextList = new LinkedList<FileText>();

    private boolean mIsThreadStopFlag = false;

    public void setThreadStopFlag(boolean threadStopFlag) {
        this.mIsThreadStopFlag = threadStopFlag;
    }

    public List<FileText> getSearchFileTextList() {
        return mSearchFileTextList;
    }

    public void setSearchFileTextList(List<FileText> searchFileTextList) {
        this.mSearchFileTextList = searchFileTextList;
    }

    public List<FileText> getFinalResult() {
        return mFinalResult;
    }

    public void setFinalResult(List<FileText> finalResult) {
        this.mFinalResult = finalResult;
    }

    public boolean isIterator() {
        return mIsIterator;
    }

    public void setIterator(boolean isIterator) {
        this.mIsIterator = isIterator;
    }

    public SearchFiles(String path, String content) {
        this.ｍDir = path;
        this.mKeyWords = content;
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.canRead();
            }
        };

    }

    public SearchFiles(String path, String content, Context context) {
        this.ｍDir = path;
        this.mKeyWords = content.trim();
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.canRead();
            }
        };
        this.mContext = context;
    }

    public SearchFiles(String path, String content, Context context, ProgressDialog sPDalog) {
        this.ｍDir = path;
        this.mKeyWords = content.trim();
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.canRead() && file.getName().indexOf(".") != 0);
            }
        };
        this.mContext = context;
        this.mSearchDialog = sPDalog;
    }

    private void findFolder(File file, boolean iter, int flag) {
        if (file.isDirectory() && file.getPath().indexOf("/.") == -1) {
            File[] fileArr = file.listFiles(mFileFilter);
            if (fileArr != null && fileArr.length > 0) {
                for (File f : fileArr) {
                    if (f.getName().contains(mKeyWords)) {
                        this.mFileList.add(f);
                        mCurrentCount++;
                        Message msg = new Message();
                        msg.what = 101;
                        msgHandler.sendMessage(msg);
                        storeData(f);
                    }
                    if (iter == true && f.isDirectory()) {
                        findFolder(f, iter, -1);
                    }
                }
            }
        }
    }

    public File[] findByName(File file) {
        if (mFileList.size() > 0) {
            mFileList.clear();
        }
        if (file == null) {
            file = getFile();
        }
        if (mKeyWords != null && !mKeyWords.equals("")) {
            try {
                findFolder(file, false, 1);
                if (mFileList.size() > 0) {
                    mResult = mFileList.toArray(new File[0]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("search files error", "~~~~~~~~keywords is null or ''~~~~~~~~~");
        }
        return mResult;
    }

    // ����Ѱ��
    public File[] findByNameAll(File file) {
        if (mFileList.size() > 0) {
            mFileList.clear();
        }
        if (file == null) {
            file = getFile();
        }
        if (mKeyWords != null && !mKeyWords.equals("")) {
            try {
                findFolder(file, true, 1);
                mResult = mFileList.toArray(new File[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("search files error", "~~~~~~~~keywords is null or ''~~~~~~~~~");
        }
        return mResult;
    }

    /**
     * @return File or null
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public File getFile() {
        if (ｍDir == null || ｍDir.isEmpty() || ｍDir.equals("")) {
            ｍDir = File.separator;
        }
        try {
            return new File(ｍDir);
        } catch (Exception e) {
            Log.e("search files error", "~~~~dir is not a directory~~~~~");
            return null;
        }
    }

    public File[] findByType(File file, String type) {
        if (mFileList.size() > 0) {
            mFileList.clear();
        }
        if (file == null) {
            file = getFile();
        }
        List<String> typeList = new LinkedList<String>();
        try {
            if (type.equals("picture")) {
                typeList = Arrays.asList(mPictureType);
            } else if (type.equals("music")) {
                typeList = Arrays.asList(mMusicType);
            } else if (type.equals("video")) {
                typeList = Arrays.asList(mVideoType);
            } else if (type.equals("document")) {
                typeList = Arrays.asList(mDocumentType);
            } else if (type.equals("whiteboard")){
                typeList = Arrays.asList(mWhiteboardType);
            }

            findByType(file, typeList, mIsIterator, 1);
            if (mFileList.size() > 0) {
                mResult = mFileList.toArray(new File[0]);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return mResult;
    }

    private void findByType(File file, List<String> typeList, boolean iter, int flag) throws SecurityException {
        if (file.exists() && file.isDirectory() && file.getPath().indexOf("/.") == -1) {
            File[] fileArr = file.listFiles(mFileFilter);
            if (fileArr != null && fileArr.length > 0) {
                int i = 0;
                int m = fileArr.length;
                for (File f : fileArr) {
                    if (typeList == null || typeList.isEmpty() || typeList.size() == 0) {
                        this.mFileList.add(f);
                    }
                    mFileDate = Tools.getFileDate(f.lastModified());
                    if (iter == true && f.isDirectory()) {
                        findByType(f, typeList, iter, 0);
                    } else if (f.isFile() && f.getPath().lastIndexOf(".") > 0) {
                        if (typeList.contains(f.getPath().substring(f.getPath().lastIndexOf(".") + 1))) {
                            this.mFileList.add(f);
                            mCurrentCount++;
                            Message msg = new Message();
                            msg.what = 101;
                            msgHandler.sendMessage(msg);
                            storeData(f);
                        }
                    }
                }
            }
        }
    }

    private void storeData(File f) {
        boolean isFile = false;
        if (f.isDirectory()) {
            mCurrentIcon = "file_icon_folder";
            mFileSize = "";
            isFile = false;
        } else {
            isFile = true;
            String fileName = f.getName();
            if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingImage))) {
                mCurrentIcon = "file_icon_other";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingAudio))) {
                mCurrentIcon = "file_icon_music";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingVideo))) {
                mCurrentIcon = "file_icon_other";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingPackage))) {
                mCurrentIcon = "file_icon_other";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingWebText))) {
                mCurrentIcon = "file_icon_other";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingText))) {
                mCurrentIcon = "file_icon_other";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingWord))) {
                mCurrentIcon = "file_icon_word";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingExcel))) {
                mCurrentIcon = "file_icon_excel";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingPPT))) {
                mCurrentIcon = "file_icon_ppt";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingPdf))) {
                mCurrentIcon = "file_icon_pdf";
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    mContext.getResources().getStringArray(R.array.fileEndingRar))) {
                mCurrentIcon = "file_icon_other";
            } else {
                mCurrentIcon = "file_icon_other";
            }
            mFileSize = Tools.FormetFileSize(f.length());
        }
        mFileDate = Tools.getFileDate(f.lastModified());
        this.mFinalResult.add(new FileText(f.getName().replace("/", ""), mCurrentIcon, mFileSize, mFileDate,
                f.getAbsolutePath(), isFile, false));
    }

    private Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 101) {
                mSearchDialog.setTitle("�ѷ���" + mCurrentCount + "���ļ�");
            }
        }
    };

    public long getDirectorySize(File f) {
        long size = 0;
        try {
            if (f.exists()) {
                File flist[] = f.listFiles(mFileFilter);
                for (int i = 0; i < flist.length; i++) {
                    if (flist[i].isDirectory()) {
                        size = size + getDirectorySize(flist[i]);
                    } else {
                        size = size + flist[i].length();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    public int getAllFilesNumber() {
        File countFile = new File(this.ｍDir);
        if (countFile.isFile()) {
            mFileNumber = 1;
        } else {
            getFilesNumber(countFile);
        }
        return mFileNumber;
    }

    private void getFilesNumber(File nFile) {
        if (nFile.isDirectory()) {
            File[] list = nFile.listFiles(mFileFilter);
            if (list != null) {
                mFileNumber = mFileNumber + list.length;
                Message msg = new Message();
                msg.what = 102;
                msgHandler.sendMessage(msg);
                for (File f : list) {
                    if (f.isDirectory()) {
                        getFilesNumber(f);
                    }
                }
            }
        }
    }

    public void updateLS(String fileName, String lsspName, String updateType) {
        String dataSetName = "";
        if (fileName != null && !fileName.equals("")) {
            if (fileName.lastIndexOf(".") > 0) {
                String formatStr = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(); // ��ȡ�ļ���ʽ
                if (Arrays.asList(mDocumentType).contains(formatStr)) {
                    // resultSetDocument.add(f.getAbsolutePath());
                    dataSetName = "document";
                } else if (Arrays.asList(mPictureType).contains(formatStr)) {
                    dataSetName = "picture";
                } else if (Arrays.asList(mMusicType).contains(formatStr)) {
                    dataSetName = "music";
                } else if (Arrays.asList(mVideoType).contains(formatStr)) {
                    dataSetName = "video";
                } else if (Arrays.asList(mWhiteboardType).contains(formatStr)){
                    dataSetName = "whiteboard";
                }
                if (!dataSetName.equals("")) {
                    updateLocalStorage(fileName, lsspName, dataSetName, updateType);
                }
            }
        }
    }

    public void updateLocalStorage(String fileName, String lsspName, String setName, String updateType) {
        mLocalPreference = new LocalStorageSharePreference(mContext, lsspName, Activity.MODE_PRIVATE);// 实例化本地存储文件类
        Set<String> resultSet = mLocalPreference.getSetValue(setName, null);
        if (resultSet == null)
            resultSet = new HashSet<String>();
        else {
            mLocalPreference.removeSetValue(setName);
        }

        if (updateType != null && updateType.equals("del")) {
            for (String fName : resultSet) {
                if (fName.equals(fileName)) {
                    resultSet.remove(fileName);
                    break;
                }
            }
        } else if (updateType != null && updateType.equals("add")) {
            try {
                if (!resultSet.contains(fileName)) {
                    resultSet.add(fileName);

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        mLocalPreference.insertSetValue(setName, resultSet);

    }

    /**
     * 
     * @param fileName
     * @param lsspName
     */
    public void searchFilesForLocalStorage(String fileName, String lsspName) {
        Log.d("wsldwo","searchFilesForLocalStorage, fileName:"+fileName+" lsspName:"+lsspName);
        mIsThreadStopFlag = false;
        mResultSetDocument.clear();
        mResultSetMusic.clear();
        mResultSetPicture.clear();
        mResultSetWhiteboard.clear();
        //mResultSetVideo.clear();
        mSearchFileSet.clear();
        findForLocalStorage(new File(fileName));
        if (mIsThreadStopFlag)
            return;
        mLocalPreference = new LocalStorageSharePreference(mContext, lsspName, Activity.MODE_PRIVATE);// ʵ�������ش洢�ļ���
        mLocalPreference.clear();
        if (mResultSetDocument != null && mResultSetDocument.size() > 0) {
            mLocalPreference.insertSetValue("document", mResultSetDocument);
        }
        if (mResultSetPicture != null && mResultSetPicture.size() > 0) {
            mLocalPreference.insertSetValue("picture", mResultSetPicture);
        }
        if (mResultSetMusic != null && mResultSetMusic.size() > 0) {
            mLocalPreference.insertSetValue("music", mResultSetMusic);
        }
        if(mResultSetWhiteboard != null && mResultSetWhiteboard.size() > 0){
            mLocalPreference.insertSetValue("whiteboard", mResultSetWhiteboard);
        }
//        if (mResultSetVideo != null && mResultSetVideo.size() > 0) {
//            mLocalPreference.insertSetValue("video", mResultSetVideo);
//        }
    }

    public String updateValue(String path) {
        Set<String> results = null;
        String filterType = null;
        File f = new File(path);
        if (f.getPath().lastIndexOf(".") > 0) {
            String formatStr = f.getPath().substring(f.getPath().lastIndexOf(".") + 1).toLowerCase(); // ��ȡ�ļ���ʽ
            if (Arrays.asList(mDocumentType).contains(formatStr)) {
                results = mResultSetDocument;
                filterType = "document";
            } else if (Arrays.asList(mPictureType).contains(formatStr)) {
                results = mResultSetPicture;
                filterType = "picture";
            } else if (Arrays.asList(mMusicType).contains(formatStr) || Arrays.asList(mVideoType).contains(formatStr)) {
                results = mResultSetMusic;
                filterType = "music";
            } else if (Arrays.asList(mWhiteboardType).contains(formatStr)) {
                results = mResultSetWhiteboard;
                filterType = "whiteboard";
            }
        }

        if (results == null)
            return filterType;

        if (!results.contains(path)) {
            results.add(path);
        } else {
            if (!f.exists()) {
                results.remove(path);
            }
        }
        if (filterType != null) {
            mLocalPreference.removeSetValue(filterType);
            mLocalPreference.insertSetValue(filterType, results);
        }
        return filterType;
    }

    /**
     * @param file
     */
    private void findForLocalStorage(File file) {
        Log.d("wsldwo","findForLocalStorage, file:"+file.getAbsolutePath());
        if (mIsThreadStopFlag){
            Log.d("wsldwo","findForLocalStorage, mIsThreadStopFlag == true, return !!!!!!!!!!!!");
            return;
        }
        if (file.isDirectory() && file.getPath().indexOf("/.") == -1) {
            File[] fileArr = file.listFiles(mFileFilter);
            if (fileArr != null && fileArr.length > 0) {
                for (File f : fileArr) {
                    if (f.getAbsolutePath().endsWith("sdcard/huc")) {
                        continue;
                    }
                    synchronized (mSearchFileSet) {
                        mSearchFileSet.add(f.getAbsolutePath());
                    }

                    if (f.isDirectory()) {
                        findForLocalStorage(f);
                    } else if (f.isFile() && f.getPath().lastIndexOf(".") > 0) {
                        String formatStr = f.getPath().substring(f.getPath().lastIndexOf(".") + 1).toLowerCase(); // ��ȡ�ļ���ʽ
                        if (Arrays.asList(mDocumentType).contains(formatStr)) {
                            mResultSetDocument.add(f.getAbsolutePath());
                            Log.d("wsldwo","add document file:"+f.getAbsolutePath());
                        } else if (Arrays.asList(mPictureType).contains(formatStr)) {
                            mResultSetPicture.add(f.getAbsolutePath());
                            Log.d("wsldwo","add picture file:"+f.getAbsolutePath());
                        } else if (Arrays.asList(mMusicType).contains(formatStr)||Arrays.asList(mVideoType).contains(formatStr)) {
                            mResultSetMusic.add(f.getAbsolutePath());
                            Log.d("wsldwo","add music file:"+f.getAbsolutePath());
                        } else if (Arrays.asList(mWhiteboardType).contains(formatStr)){
                            mResultSetWhiteboard.add(f.getAbsolutePath());
                            Log.d("wsldwo","add whiteboard file:"+f.getAbsolutePath());
                        }
                        //else if (Arrays.asList(mVideoType).contains(formatStr)) {
                          //  mResultSetVideo.add(f.getAbsolutePath());
                      //  }
                    }
                }
            }
        }
    }

    public Set<String> getTmpSearchResult() {
        return mTmpSearchResult;
    }

    public void setTmpSearchResult(Set<String> tmpSearchResult) {
        this.mTmpSearchResult = tmpSearchResult;
    }

    

    public void searchFile(String keyWords) {
        mTmpSearchResult.clear();
        if (mSearchFileSet.size() > 0) {
            synchronized (mSearchFileSet) {
                Iterator itor = mSearchFileSet.iterator();
                while (itor.hasNext()) {
                    String sm = itor.next().toString();
                    if (sm.lastIndexOf("/") > 0) {
                        String tmpName = sm.substring(sm.lastIndexOf("/")); // ��ȡ�ļ�����ȡ��·��
                        if (tmpName.toLowerCase().contains(keyWords.toLowerCase())) {
                            mTmpSearchResult.add(sm);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param file
     */
    public void findAll(File file) {
        if (file.isDirectory() && file.getPath().indexOf("/.") == -1) // ���Ե��ļ��������ļ�/�ļ��У�
        {
            File[] fileArr = file.listFiles(mFileFilter);// ��ȡ��ǰĿ¼�µ��ļ�
            if (fileArr != null && fileArr.length > 0) {
                synchronized (mSearchFileSet) {
                    for (File f : fileArr) {
                        mSearchFileSet.add(f.getAbsolutePath());
                    }
                }
            }
        }
    }

    static class MyFilter implements FilenameFilter {
        private String type;
        public MyFilter(String type) {
            this.type = type;
        }
        public boolean accept(File dir, String name) {
            if (name.indexOf(".") > 0)
                return name.endsWith(type);
            else
                return false;
        }
    }

    static class MyFilter1 implements FilenameFilter {
        public boolean accept(File dir, String name) {
            if (name.indexOf(".") != 0)
                return true;
            else
                return false;
        }
    }
}
