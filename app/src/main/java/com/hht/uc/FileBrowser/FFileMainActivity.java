package com.hht.uc.FileBrowser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hht.EnumFormatFilterType;
import com.hht.filebrowser.dialog.CustomProgressDialog;
import com.hht.filebrowser.dialog.DeleteFileDialog;
import com.hht.filebrowser.dialog.PopDialog;
import com.hht.filebrowser.files.BuilderUtil;
import com.hht.filebrowser.files.FileText;
import com.hht.filebrowser.files.FileTextAdapter;
import com.hht.filebrowser.files.FileTextManager;
import com.hht.filebrowser.files.LocalStorageSharePreference;
import com.hht.filebrowser.files.OpenFile;
import com.hht.filebrowser.files.SearchFiles;
import com.hht.filebrowser.files.Tools;
import com.hht.filebrowser.utils.HScreenUtils;
import com.hht.filebrowser.view.FBottomView;
import com.hht.filebrowser.view.FilePathView;
import com.hht.filebrowser.view.FormatFilter;
import com.hht.filebrowser.view.SearchBar;
import com.hht.skin.main.SkinManager;
import com.hht.widget.CusImageButton;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by DK-dong on 2014/12/15.
 */
public class FFileMainActivity extends Activity implements View.OnClickListener {
    private final String SYSTEM_PATH = "storage/emulated/0";
    //    private final String SYSTEM_PATH = File.separator;
    public static final String ACTION_USB_LIST = "com.hht.uc.FileBrowser.UsbList";
    private static final String TAG = "FFileMainActivity===";
    private static final String INTERNAL_SDCARD_PATH = "/mnt/mmc_sdcard";
    private final SearchFiles mSearchFiles = new SearchFiles("", "", this, null);
    private final String mWbPath = "/data/media/0/Pictures/Screenshots";
    private Thread mThreadScanAllFiles = null;
    private File mTmpSearchFile;
    private CopyFilesTask mCopyFilesTask;
    private List<String> mUsbs;
    public static List<FileText> mFiles = new ArrayList<FileText>();
    private StorageItemGroup mStorageItemGroup;
    //    private File mCurrentDirectory = new File(SYSTEM_PATH);
    private File mCurrentDirectory = new File(mWbPath);

    private List<File> mTempFilesList = new ArrayList<File>();
    private List<FileText> mSearchResult = Collections.synchronizedList(new LinkedList<FileText>());
    private List<String> mNewUsbList = new ArrayList<String>();
    private FilePathView mFilePathView;
    private LinearLayout mOperationLayout;
    private LinearLayout mOperationPasteLayout;
    private LinearLayout mStorageDeviceLayout;
    private LinearLayout mRootLayout;
    private LinearLayout mStorageView;
    private TextView mTvFolderEmptySearch;
    private TextView mTvFolderEmpty;
    private CusImageButton mBtnOperation;
    private CusImageButton mBtnSelectAll;
    private Button mBtnFindFile;
    private CusImageButton mBtnPaste;
    private GridView mGridView;
    private ListView mListView;
    private ListView mSearchListView;
    private FileTextAdapter mFileTextAdapterGrid;
    private FileTextAdapter mFileTextAdapterList;
    private FormatFilter mFormatFilterBarView;
    private FBottomView mBottomView;
    private FileFilter mFileFilter;
    private View mMainLayout;
    private View mSearchLayout;
    private View mCurrentFilterView;
    private View mCurrentView = null;
    private View mCurrentFormatFilterView;
    private SearchBar mSearchBarTool = null;
    private String mUsbName;
    private String mPath;
    private String mPathScan;
    private String mRootPath;
    private boolean mIsFinishAsyncTask = false;
    private boolean mIsSelectedAll = false;
    private boolean mIsDeleteFlag = true;
    private boolean mIsOperation = false;
    private boolean mIsCopyStatus = false;
    private boolean mIsFilterWb = false;
    private boolean mFileChanged = false;
    private long mWaitTime = 3500;
    private long mTouchTime = 0;
    private int mPositionX;
    private int mSuccess = 0;
    private int mFileNum = 0;
    private int mFail = 0;
    private int mInitSearch = 0;
    private int mFormatThreadNum = 0;
    private int mThreadFilesChange = 0;
    private final int MSG_RELOAD_CURRENT_DIRECTORY = 111;
    private PopDialog mDialog;
    private BroadcastReceiver mFinishReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            FFileMainActivity.this.finish();
        }

    };

    private AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mBtnOperation.setSelected(true);
            mOperationLayout.setVisibility(View.VISIBLE);
            mIsOperation = true;
            for (FileText ft : mFiles) {
                ft.setOperation(true);
            }
            mFormatFilterBarView.childrenClickable(false);
            mTempFilesList.clear();
            mFileNum = 0;
            mFileTextAdapterList.notifyDataSetChanged();
            mFileTextAdapterGrid.notifyDataSetChanged();
            return false;
        }
    };
    private RecursiveFileObserver mRecursiveFileObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_file_main);
        SkinManager.getInstance().register(this);
        init();
        if (mPath.equals("/storage/emulated/0")) {
            /*open(new File(mWbPath));
            mFormatFilterBarView.itemClickStyle(mFormatFilterBarView.getImgWb(), mFormatFilterBarView.getTextWb());
            mRootPath = mWbPath;*/
            //openWhiteboardFiles();
            open(new File(SYSTEM_PATH));
            mRootPath = SYSTEM_PATH;
        } else {
            open(new File(mPath));
            mRootPath = mPath;
        }
        if (mPathScan != null) {
            scanThread(mPathScan);
        }
        HScreenUtils.getInstance().setSysBackground(FFileMainActivity.this, mRootLayout);
    }

    /**
     * 添加存储设备
     *
     * @param list
     */
    private void addAllStorage(List<String> list) {
        View itemLayout;
        int drawableResId = -1;
        String title = null;
        String usbPath = null;
        mStorageView.removeAllViews();
        if (mStorageItemGroup == null)
            mStorageItemGroup = new StorageItemGroup(this);
        mStorageItemGroup.clear();
        for (int i = 0; i < list.size(); i++) {
            mPath = list.get(i);
            usbPath = list.get(i);
            mUsbName = Tools.getUsbDeviceLabel(this, mPath);
            if (mUsbName.equals("0")) {
                drawableResId = R.drawable.btn_system_icon;
                mUsbName = getResources().getString(R.string.system_files);
            } else {
                drawableResId = R.drawable.btn_usb_icon;
            }
            title = mUsbName;
            itemLayout = mStorageItemGroup.addItem(drawableResId, title, usbPath);
            mStorageView.addView(itemLayout);
        }
        if (list.size() > 0) {
            // Just observe internal sdcard
            if (mRecursiveFileObserver == null) {
                mRecursiveFileObserver = new RecursiveFileObserver(list.get(0));
                mRecursiveFileObserver.startWatching();
            }
        }
        mStorageItemGroup.requestFocusQuietly();
    }

    /**
     * 初始化USB
     *
     * @param list
     */
    private void initUsb(List<String> list) {
        int usbNum = list.size();
        if (usbNum > 0) {
            mPathScan = list.get(usbNum - 1);
        }
        if (usbNum == 0) {
            mUsbName = getString(R.string.system_files);
            mPath = SYSTEM_PATH;
        } else if (usbNum == 1 && mPathScan.contains("/mnt/sdcard")) {
            mUsbName = getString(R.string.system_files);
            mPath = SYSTEM_PATH;
        } else {
            addAllStorage(list);
            mPath = list.get(usbNum - 1);
            mUsbName = Tools.getUsbDeviceLabel(this, mPath);
            if (mUsbName.equals("0")) {
                mUsbName = getResources().getString(R.string.system_files);
//                mPath = SYSTEM_PATH;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (getCurrentDirectory() != null && new File(getCurrentDirectory()).exists()) {
//            open(new File(getCurrentDirectory()));
//        }

        Intent intent = getIntent();
        if (intent != null) {
            String path = intent.getStringExtra("path");
            if (path != null) {
                int position = findPositionByPath(path);
                mStorageItemGroup.itemRequestFocus(position);
                open(new File(path));
            }
        }
    }

    private int findPositionByPath(String path) {
        List<String> list = Tools.getStoragePaths(FFileMainActivity.this);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(path)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void init() {
        Tools.mkdir();
        mStorageDeviceLayout = (LinearLayout) findViewById(R.id.storage_device_layout);
        mStorageDeviceLayout.setOnClickListener(this);
        mBottomView = (FBottomView) findViewById(R.id.bottom_view);
        mOperationPasteLayout = (LinearLayout) mBottomView.findViewById(R.id.copy_layout);
        mOperationLayout = (LinearLayout) mBottomView.findViewById(R.id.file_operation_layout);
        mBtnSelectAll = (CusImageButton) mBottomView.findViewById(R.id.file_all);
        mBtnOperation = (CusImageButton) mBottomView.findViewById(R.id.file_operation);
        mBtnPaste = (CusImageButton) mOperationPasteLayout.findViewById(R.id.btn_pause);
        mStorageView = (LinearLayout) findViewById(R.id.fileStorageView);
        mFormatFilterBarView = (FormatFilter) findViewById(R.id.format_filter_bar_second);
        mTvFolderEmpty = (TextView) findViewById(R.id.tv_folder_empty);
        editTextTool = (EditText) findViewById(R.id.et_find);
        mBtnFindFile = (Button) findViewById(R.id.btn_find);
        mRootLayout = (LinearLayout) findViewById(R.id.layout_main);
        mGridView = (GridView) findViewById(R.id.gv_files);
        mListView = (ListView) findViewById(R.id.lv_files);
        mSearchListView = (ListView) findViewById(R.id.searchResultList);
        mMainLayout = findViewById(R.id.mainLayout);
        mSearchLayout = findViewById(R.id.searchLayout);
        mFilePathView = (FilePathView) findViewById(R.id.filePath);
        mTvFolderEmptySearch = (TextView) findViewById(R.id.tv_folder_empty2);
        mTvFolderEmpty.setOnClickListener(this);
        mRootLayout.setOnClickListener(this);
        mStorageView.setOnClickListener(this);
        mFormatFilterBarView.setOnClickListener(this);
        mBottomView.setOnClickListener(this);
        mFilePathView.setOnClickListener(this);
        mSearchLayout.setOnClickListener(this);
        mOperationLayout.setOnClickListener(this);
        mOperationPasteLayout.setOnClickListener(this);
        mFileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.getName().indexOf(".") != 0);
            }
        };

        Intent intent = getIntent();
        Uri strPath = intent.getData();
        if (strPath != null) {
            mUsbs = Tools.getStoragePaths(FFileMainActivity.this);
            mPathScan = strPath.toString();
            mPath = strPath.toString();
            mUsbName = Tools.getUsbDeviceLabel(this, mPath);
            if (mUsbName.equals("0")) {
                mUsbName = getResources().getString(R.string.system_files);
            }
        } else {
            mUsbs = Tools.getStoragePaths(FFileMainActivity.this);
            mNewUsbList = mUsbs;
        }
        initUsb(mUsbs);
        initLeftBar();
        mBtnFindFile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initialSearchLayout();
            }
        });
        editTextTool.setOnFocusChangeListener(mFocusChangeListenerClick);
        mBtnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuPasteFile();
            }
        });

        mFilePathView.setBtnPath1Text(mUsbName);
        mFilePathView.setBtnPath1OnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsOperation) {
                    mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_ALL);
                    initNavigation();
                    open(new File(mPath));
                    mRootPath = mPath;
                }
            }
        });
        mBtnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mIsSelectedAll) {
                    mIsSelectedAll = true;
                    mTempFilesList.clear();
                    for (FileText ft : mFiles) {
                        ft.setSelected(true);
                        mTempFilesList.add(new File(ft.getFilePath()));
                    }
                    mFileTextAdapterList.notifyDataSetChanged();
                    mFileTextAdapterGrid.notifyDataSetChanged();
                    mFileNum = mTempFilesList.size();
                } else {
                    mIsSelectedAll = false;
                    for (FileText ft : mFiles) {
                        ft.setSelected(false);
                        mTempFilesList.clear();
                    }
                    mFileTextAdapterList.notifyDataSetChanged();
                    mFileTextAdapterGrid.notifyDataSetChanged();
                    mFileNum = 0;
                }
            }
        });
        mBtnOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = mBtnOperation.isSelected();
                mBtnOperation.setSelected(!isSelected);
                if (!isSelected) {
                    mOperationLayout.setVisibility(View.VISIBLE);
                    mIsOperation = true;
                    for (FileText ft : mFiles) {
                        ft.setOperation(true);
                    }
                    mFormatFilterBarView.childrenClickable(false);
                } else {
                    mIsOperation = false;
                    for (FileText ft : mFiles) {
                        ft.setOperation(false);
                    }
                    mIsCopyStatus = false;
                    mOperationLayout.setVisibility(View.GONE);
                    mOperationPasteLayout.setVisibility(View.GONE);
                    if (mCurrentFormatFilterView != null) {
                        ffbar_click(mCurrentFormatFilterView);
                    } else {
                        constructList(new File(getCurrentDirectory()).listFiles(mFileFilter));
                    }
                    mFormatFilterBarView.childrenClickable(true);
                }
                mTempFilesList.clear();
                mFileNum = 0;
                mFileTextAdapterList.notifyDataSetChanged();
                mFileTextAdapterGrid.notifyDataSetChanged();
            }
        });

        CusImageButton btnDelete = (CusImageButton) mBottomView.findViewById(R.id.file_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTempFilesList.size() > 0) {
                    final DeleteFileDialog dialog = new DeleteFileDialog(FFileMainActivity.this, R.style.DialogTheme);
                    dialog.show();
                    dialog.setBtnOkClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFail = 0;
                            mSuccess = 0;
                            mIsDeleteFlag = true;
                            for (File pf : mTempFilesList) {
                                if (pf.canWrite()) {
                                    boolean delflag;
                                    if (pf.isDirectory())
                                        delflag = Tools.deleteFolder(pf);
                                    else
                                        delflag = deleteFile(pf);

                                    if (delflag) {
                                        updateLocalSP(pf.getAbsolutePath(), "del");
                                        mSuccess++;
                                    } else {
                                        mFail++;
                                    }
                                } else {
                                    mFail++;
                                }
                                if (!mIsDeleteFlag)
                                    break;
                            }
                            Toast.makeText(FFileMainActivity.this,
                                    getString(R.string.success) + " : " + mSuccess + "  "
                                            + getString(R.string.message_failure) + " : " + mFail,
                                    Toast.LENGTH_LONG).show();
                            if (mCurrentFormatFilterView != null) {
                                ffbar_click(mCurrentFormatFilterView);
                            } else {
                                constructList(new File(getCurrentDirectory()).listFiles(mFileFilter));
                            }
                            for (FileText ft : mFiles) {
                                ft.setOperation(true);
                            }
                            mFileNum = 0;
                            mTempFilesList.clear();
//                            mIsOperation = false;
                            dialog.dismiss();
                        }
                    });
                    dialog.setBtnCancelClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    Toast.makeText(FFileMainActivity.this, getResources().getString(R.string.message_no_file_selected),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        CusImageButton btnCopy = (CusImageButton) mBottomView.findViewById(R.id.file_copy);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTempFilesList.size() > 0) {
                    String pathName = Tools.getUsbDeviceLabel(FFileMainActivity.this, getCurrentDirectory());
                    if (pathName.trim().equals("")) {
                        pathName = "/";
                    }
                    mOperationLayout.setVisibility(View.GONE);
                    mOperationPasteLayout.setVisibility(View.VISIBLE);
                    mIsOperation = false;
                    mIsCopyStatus = true;
                    constructList(new File(getCurrentDirectory()).listFiles(mFileFilter));
                } else {
                    Toast.makeText(FFileMainActivity.this, getResources().getString(R.string.message_no_file_selected),
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        mBtnFindFile.setFocusable(true);
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_USB_LIST);
        registerReceiver(usbListBroadcastReceiver, intentFilter);

        IntentFilter newUsbIntentFilter = new IntentFilter();
        newUsbIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        newUsbIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        newUsbIntentFilter.addDataScheme("file");
        newUsbIntentFilter.setPriority(1000);
        registerReceiver(newUsbBroadcastReceiver, newUsbIntentFilter);

        IntentFilter finish = new IntentFilter();
        finish.addAction("com.hht.fileBrowser.ACTION.FINISH");
        registerReceiver(mFinishReceiver, finish);
    }

    private void initNavigation() {
        if (mFilePathView.number > 0) {
            for (int i = 0; i <= mFilePathView.number; i++) {
                LinearLayout layoutTop = (LinearLayout) mFilePathView.findViewById(R.id.layout_path_view);
                layoutTop.removeView(mFilePathView.findViewById(i));
            }
            mFilePathView.number = 0;
        }
    }

    private void pathBtnClick(Button btn) {
        int id = btn.getId();
        if (mFilePathView.number > id && !mIsOperation) {
            for (int i = id + 1; i <= mFilePathView.number; i++) {
                LinearLayout layoutTop = (LinearLayout) mFilePathView.findViewById(R.id.layout_path_view);
                layoutTop.removeView(mFilePathView.findViewById(i));
            }
            mFilePathView.number = id;
            open(new File(btn.getTag().toString()));
        }
    }

    private void initSearch() {
        initNavigation();
        mFilePathView.setBtnPath1Clickable(false);
        mBtnOperation.setVisibility(View.GONE);
        mMainLayout.setVisibility(View.GONE);
        mSearchLayout.setVisibility(View.VISIBLE);
//        mFormatFilterBarView.initialStyle();
        mBottomView.startListGridChange(false);
    }

    private void open(final File file) {
        if (file == null) {
            throw new NullPointerException("file is  null");
        }
        Log.e("liushuaikang", "mPathScan = " + file.getAbsolutePath());
        if (file.getAbsolutePath().equals(mPath)) {
            mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_ALL);
        }

        if (file.isDirectory()) {
            this.mCurrentDirectory = file;
            mCurrentFormatFilterView = null;
            constructList(file.listFiles(mFileFilter));

            mFormatFilterBarView.childrenClickable(!mIsOperation && !mIsCopyStatus);
            if (!mStorageItemGroup.isFirstFocus())
                mStorageItemGroup.forceItemSelectFocus();
            mRecursiveFileObserver.openDirectory(file.getAbsolutePath());
        } else if (file.isFile()) {
            openFile(file);
        }
    }



    private void openFile(File file) {
        Intent intent;
        File file2 = file.getAbsoluteFile();
        String fileName = file2.getName();
        try {
            if (OpenFile.checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingImage))) {
                mDialog = OpenFile.getImageFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingWebText))) {
                mDialog = OpenFile.getTextFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingPackage))) {

                intent = OpenFile.getApkFileIntent(file);
                startActivity(intent);

            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingAudio))) {
                mDialog = OpenFile.getAudioFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingVideo))) {
                mDialog = OpenFile.getVideoFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingText))) {
                mDialog = OpenFile.getTextFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingPdf))) {
                //mDialog = OpenFile.getPdfFileIntent(FFileMainActivity.this, file);
                //showDialog(file);
                Log.d("wsldwo","open pdf file:"+file.getAbsolutePath());
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setComponent(new ComponentName("cn.wps.moffice_eng", "cn.wps.moffice.documentmanager.PreStartActivity"));
                intent.setData(Uri.fromFile(file));
                startActivity(intent);
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingWord))) {
                mDialog = OpenFile.getWordFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingExcel))) {
                mDialog = OpenFile.getExcelFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingPPT))) {
                mDialog = OpenFile.getPPTFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            } else if(OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingWhiteboard))){
                Log.d("wsldwo","open whiteboard file:"+file.getAbsolutePath());
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setComponent(new ComponentName("com.eshare.paint", "com.eshare.paint.activity.PaintActivity"));
                intent.setData(Uri.fromFile(file));
                startActivity(intent);
            } else if(OpenFile.checkEndsWithInStringArray(fileName,
                    getResources().getStringArray(R.array.fileEndingCertificate))){
                String action = getIntent().getAction();
                String mime = getMimeType(fileName);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), mime);
                if (action.equals(Intent.ACTION_OPEN_DOCUMENT)) {
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    intent.setComponent(new ComponentName(
                            "com.android.certinstaller",
                            "com.android.certinstaller.CertInstallerMain"));
                    startActivity(intent);
                }
            }
            else {
                mDialog = OpenFile.getOtherFileIntent(FFileMainActivity.this, file);
                showDialog(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mDialog = OpenFile.getOtherFileIntent(FFileMainActivity.this, file);
            showDialog(file);
        }
    }

    private String getMimeType(String fileName) {
        int dot = fileName.lastIndexOf('.');
        String suffix = fileName.substring(dot + 1);
        String mimeType = null;
        if (suffix.equals("crt")) {
            mimeType = "application/x-x509-ca-cert";
        } else if (suffix.equals("pem")) {
            mimeType = "application/x-pem-file";
        } else if (suffix.equals("cer")) {
            mimeType = "application/pkix-cert";
        } else if (suffix.equals("p12")) {
            mimeType = "application/x-pkcs12";
        }
        return mimeType;
    }

    private long lastTime = 0;
    private static final int TIME_DURATION = 500;

    private void showDialog(File file) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime > TIME_DURATION) {
            if (mDialog != null) {
                if (mBottomView.isListStyle()) {
                    mDialog.showAt(mCurrentView, mPositionX, 0);
                } else {
                    mDialog.show(mCurrentView);
                }
            }
            lastTime = currentTime;
        }
    }

    private void constructList(File[] listFiles) {
        this.mFiles.clear();
        String currentIcon;
        String fileSize;
        String fileDate;
        boolean isFile;
        if (listFiles != null) {
            for (File currentFile : listFiles) {
                String fileName = currentFile.getName();
                if (mIsCopyStatus && mFormatFilterBarView.getControlWB()) {
                    mIsFilterWb = true;
                }
                if (mIsFilterWb && (fileName.toLowerCase().equals("screenshot"))) {
                    continue;
                }
                if (currentFile.isDirectory()) {
                    if (currentFile.getAbsolutePath().endsWith("sdcard/huc")) {
                        continue;
                    }
                    currentIcon = "file_icon_folder";
                    isFile = false;
                    fileSize = "";
                } else {
                    if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingImage))) {
                        currentIcon = "file_icon_img";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingAudio))) {
                        currentIcon = "file_icon_audio";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingVideo))) {
                        currentIcon = "file_icon_video";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingPackage))) {
                        currentIcon = "file_icon_apk";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingWebText))) {
                        currentIcon = "file_icon_pdf";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingText))) {
                        currentIcon = "file_icon_text";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingWord))) {
                        currentIcon = "file_icon_word";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingExcel))) {
                        currentIcon = "file_icon_xls";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingPPT))) {
                        currentIcon = "file_icon_ppt";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingPdf))) {
                        currentIcon = "file_icon_pdf";
                    } else if (OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingRar))) {
                        currentIcon = "file_icon_rar";
                    } else if(OpenFile.checkEndsWithInStringArray(fileName,
                            this.getResources().getStringArray(R.array.fileEndingWhiteboard))){
                        currentIcon = "file_icon_whiteboard";
                    } else if(OpenFile.checkEndsWithInStringArray(fileName,
                            getResources().getStringArray(R.array.fileEndingCertificate))){
                        currentIcon = "file_icon_other";
                    } else {
                        currentIcon = "file_icon_other";
                    }
                    isFile = true;
                    fileSize = Tools.FormetFileSize(currentFile.length());
                }
                fileDate = Tools.getFileDate(currentFile.lastModified());

                this.mFiles.add(new FileText(fileName, currentIcon, fileSize, fileDate, currentFile.getAbsolutePath(),
                        isFile, false, mIsOperation));
            }
            mIsFilterWb = false;
        }
        loadDataFromList();
    }

    private void constructList(List<FileText> listFile) {
        if (listFile == null || listFile.size() == 0) {
            mGridView.setAdapter(null);
            mListView.setAdapter(null);
            mTvFolderEmpty.setText(getResources().getString(R.string.folder_empty));
            mTvFolderEmpty.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            this.mFiles.clear();
        } else {
            this.mFiles = new ArrayList<FileText>(listFile);
            loadDataFromList();
        }
    }

    private void loadDataFromList() {
        Collections.sort(this.mFiles, new Comparator<FileText>() {

            @Override
            public int compare(FileText f1, FileText f2) {
                if (!f1.isFile() && f2.isFile()) {
                    return -1;
                } else if (f1.isFile() && !f2.isFile()) {
                    return 1;
                } else if (f1.isFile() && f2.isFile()) {
                    String[] array = f1.getText().split("\\.");
                    String f1Extensions = array[array.length - 1];
                    array = f2.getText().split("\\.");
                    String f2Extensions = array[array.length - 1];
                    int ret = f1Extensions.toLowerCase().compareTo(f2Extensions.toLowerCase());
                    if (ret != 0)
                        return ret;
                }
                return f1.getText().toLowerCase().compareTo(f2.getText().toLowerCase());
            }
        });

        int lvTopPosition = 0;
        int lvTopY = 0;
        int gvTopPosition = 0;
        int gvTopY = 0;
        if (mListView != null) {
            lvTopPosition = mListView.getFirstVisiblePosition();
            View child = mListView.getChildAt(lvTopPosition);
            if (child != null)
                lvTopY = mListView.getChildAt(lvTopPosition).getTop();
        }

        if (mGridView != null) {
            gvTopPosition = mListView.getFirstVisiblePosition();
            View child = mListView.getChildAt(gvTopPosition);
            if (child != null)
                gvTopY = mListView.getChildAt(gvTopPosition).getTop();
        }

        mListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPositionX = (int) event.getX();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FileTextManager.getInstance().setFileTextList(mFiles);
                mCurrentView = view;
                itemClick(position);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FileTextManager.getInstance().setFileTextList(mFiles);
                Log.e("FFileMainActivity", mFiles.get(position).getFilePath());
                mCurrentView = view;
                itemClick(position);
            }
        });
        mListView.setOnItemLongClickListener(mItemLongClickListener);
        mGridView.setOnItemLongClickListener(mItemLongClickListener);
        // 首次切换列表或网格显示时，不希望焦点会落在列表上
        mGridView.setFocusable(false);
        mListView.setFocusable(false);

        mGridView.setOnFocusChangeListener(mFocusChangeListenerClick);
        mListView.setOnFocusChangeListener(mFocusChangeListenerClick);
        mSearchListView.setOnFocusChangeListener(mFocusChangeListenerClick);
        mFileTextAdapterGrid = new FileTextAdapter(this, "grid");
        mFileTextAdapterList = new FileTextAdapter(this, "list");
        mFileTextAdapterGrid.setFiles(this.mFiles);
        mFileTextAdapterList.setFiles(this.mFiles);
        mGridView.setAdapter(mFileTextAdapterGrid);
        mListView.setAdapter(mFileTextAdapterList);

        if (mFileChanged) {
            if (lvTopPosition > 0 || lvTopY > 0)
                mListView.setSelectionFromTop(lvTopPosition, lvTopY);

            if (gvTopPosition > 0 || gvTopY > 0)
                mGridView.setSelectionFromTop(gvTopPosition, gvTopY);
            mFileChanged = false;
        }

        mTvFolderEmpty.setVisibility(View.GONE);
        mSearchListView.setVisibility(View.GONE);
        if (mBottomView.isListStyle()) {
            mListView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
        }

        if (mFileTextAdapterGrid.getCount() == 0) {
            mTvFolderEmpty.setText(getResources().getString(R.string.folder_empty));
            mTvFolderEmpty.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }
        if (mFileTextAdapterList.getCount() == 0) {
            mTvFolderEmpty.setText(getResources().getString(R.string.folder_empty));
            mTvFolderEmpty.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private Dialog progressDialog = null;

    public void itemClick(int position) {
        if (mIsOperation) {
            File tf = new File(mFiles.get(position).getFilePath());
            if (mFiles.get(position).isSelected()) {
                mFiles.get(position).setSelected(false);
                if (mTempFilesList.contains(tf)) {
                    mTempFilesList.remove(tf);
                }
                mFileNum--;
            } else {
                mFiles.get(position).setSelected(true);
                if (!mTempFilesList.contains(tf)) {
                    mTempFilesList.add(tf);
                }
                mFileNum++;
                if (mFileNum == mFiles.size()) {
                    mIsOperation = true;
                }
            }
            mFileTextAdapterList.notifyDataSetChanged();
            mFileTextAdapterGrid.notifyDataSetChanged();
        } else {
            File clickedFile = new File(FFileMainActivity.this.mFiles.get(position).getFilePath());
            if (clickedFile.isDirectory()) {
                final Button button = mFilePathView.addPathButton(clickedFile.getName(), clickedFile.getAbsolutePath());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = button.getId();
                        if (mFilePathView.number > id && !mIsOperation) {
                            pathBtnClick(button);
                        }
                    }
                });
                button.setFocusable(false); //jiangwensong
                mFormatFilterBarView.initialStyle();
            }
            FFileMainActivity.this.open(clickedFile);
        }
    }

    private String getCurrentDirectory() {
        return this.mCurrentDirectory.getAbsolutePath();
    }

    private void copy(File source, File target) {
        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream bufferedOutputStream = null;
        try {
            if (source.isDirectory()) {
                if (!target.exists()) {
                    target.mkdirs();
                }
                File[] files = source.listFiles();
                for (File f : files) {
                    if (f.isFile()) {
                        bufferedInputStream = new BufferedInputStream(new FileInputStream(f));
                        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                                new File(target.getAbsolutePath() + File.separator + f.getName())));
                        byte[] buffer = new byte[2048];
                        // 鏉╂柨娲栫�圭偤妾拠璇插絿閻ㄥ嫬鐡ч懞鍌涙殶閿涘苯顩ч弸婊嗩嚢閸掔増鏋冩禒鑸垫汞鐏忔儳鍨潻鏂挎礀-1
                        int len;
                        while ((len = bufferedInputStream.read(buffer)) != -1) {
                            bufferedOutputStream.write(buffer, 0, len);
                        }
                        bufferedOutputStream.flush();
                    } else {
                        copy(new File(source.getAbsolutePath() + File.separator + f.getName()),
                                new File(target.getAbsolutePath() + File.separator + f.getName()));
                    }
                }
            } else if (source.isFile()) {
                if (!target.exists()) {
                    target.createNewFile();
                }
                bufferedInputStream = new BufferedInputStream(new FileInputStream(source));
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(target));
                byte[] buffer = new byte[2048];
                int len = 0;
                while ((len = bufferedInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, len);
                }
                bufferedOutputStream.flush();
            }
            updateLocalSP(getCurrentDirectory() + File.separator + source.getName(), "add");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean deleteFile(File file) {
        boolean result = false;
        if (file != null) {
            try {
                result = file.delete();
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    private Boolean menuPasteFile() {
        if (mTempFilesList.size() == 0) {
            BuilderUtil.buildInfo(FFileMainActivity.this, getResources().getString(R.string.message_paste2),
                    getResources().getString(R.string.message_paste4));
            return false;
        } else {
            File file = new File(getCurrentDirectory());
            for (File tempfile : mTempFilesList) {
                if (getCurrentDirectory().startsWith(tempfile.toString())) {
                    Toast.makeText(this, getString(R.string.not_paste), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if (file.canWrite()) {
                long fileSizes = 0;
                long availableSize = Tools.getAvailableSize(new File(mPathScan));
                for (final File oldFile : mTempFilesList) {
                    long fileSize = Tools.getDirectorySize(oldFile);
                    fileSizes = fileSizes + fileSize;
                }
                if (availableSize - fileSizes > 1024) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int sameNameNumber = 0;
                            for (final File oldFile : mTempFilesList) {
                                File toFile;
                                if (oldFile.toString().startsWith(getCurrentDirectory())) {
                                    String fileName = Tools.getFileName(oldFile.toString());
                                    String fileSuffix = Tools.getSuffix(oldFile.toString());
                                    toFile = new File(
                                            getCurrentDirectory() + File.separator + fileName + "-(1)" + fileSuffix);
                                } else {
                                    toFile = new File(getCurrentDirectory() + File.separator + oldFile.getName());
                                }

                                if (toFile.exists()) {
                                    sameNameNumber++;
                                }
                            }

                            Message msg = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt("sameNameNumber", sameNameNumber);
                            msg.what = 0x101;
                            msg.setData(bundle);
                            handlerCopy.sendMessage(msg);
                        }
                    }).start();
                } else {
                    Toast.makeText(FFileMainActivity.this, getString(R.string.space_not_enough), Toast.LENGTH_SHORT)
                            .show();
                }

                return true;
            } else {
                Toast.makeText(FFileMainActivity.this, getResources().getString(R.string.message_no_permission),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mTmpSearchFile == null) {
            if (mIsOperation) {
                mIsOperation = false;
                mOperationLayout.setVisibility(View.GONE);
                mOperationPasteLayout.setVisibility(View.GONE);
                mBtnOperation.setSelected(false);
                mTempFilesList.clear();
                mFileNum = 0;
                if (mCurrentFormatFilterView != null) {
                    ffbar_click(mCurrentFormatFilterView);
                } else {
                    constructList(mCurrentDirectory.listFiles());
                }

                mFormatFilterBarView.childrenClickable(true);
            } else {
                if (getCurrentDirectory().equals(mRootPath)) {
                    long currentTime = System.currentTimeMillis();
                    if ((currentTime - mTouchTime) >= mWaitTime) {
                        Toast.makeText(this, getResources().getString(R.string.message_exit), Toast.LENGTH_SHORT)
                                .show();
                        mTouchTime = currentTime;
                    } else {
                        finish();
                    }
                } else {
                    this.upLevel();
                }
            }
        } else {
            if (!this.mCurrentDirectory.equals(mTmpSearchFile)) {
                upLevel();
            } else {
                initialSearchLayout();
            }
        }
    }

    /**
     * 返回上一级目录
     */
    private void upLevel() {
        if (mFilePathView.number > 0) {
            LinearLayout layoutTop = (LinearLayout) mFilePathView.findViewById(R.id.layout_path_view);
            layoutTop.removeView(mFilePathView.findViewById(mFilePathView.number));
            mFilePathView.number--;
        }
        File parentFile = this.mCurrentDirectory.getParentFile();
        mCurrentDirectory = parentFile;
        if (parentFile == null)   finish();//如果父文件夹为空，就退出
        else    this.open(parentFile);
    }

    private void initLeftBar() {

        mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_ALL);
        View.OnClickListener filterClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ffbar_click(view);
            }
        };
        mFormatFilterBarView.selfOnClick(filterClickListener);// 鐠佸墽鐤嗛弽鐓庣础缁涙盯锟藉瀵滈柦顔炬畱閻╂垵鎯夋禍瀣╂
        mFormatFilterBarView.selfOnFocusChange(mFocusChangeListenerClick);
        mBottomView.getLayoutList().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setVisibility(View.VISIBLE);
                mGridView.setVisibility(View.GONE);
                mBottomView.setListStyle(true);
//                loadDataFromList();
            }
        });
        mBottomView.getLayoutGrid().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
                mBottomView.setListStyle(false);
//                loadDataFromList();
            }
        });
    }

    private void ffbar_click(View view) {
        Log.d("wsldwo","ffbar_click, mPath:"+mPath);
        Log.e("Liushuaikang", "click" + mPath);
        if (mPath.equals(SYSTEM_PATH)) {
            mFilePathView.setBtnPath1Text(getString(R.string.system_files));
        } else {
            mFilePathView.setBtnPath1Text(mUsbName);
        }
        mFilePathView.setBtnPath1Clickable(true);
        mTmpSearchFile = null;
        isSearchNull = false;
        mThreadFilesChange = 0;
        mCurrentFormatFilterView = view;
        initNavigation();
        int viewId = view.getId();
        String tmpType = "";
        switch (viewId) {
            case R.id.format_filter_document:
                tmpType = "document";
                mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_DOCUMENT);
                this.mCurrentDirectory = new File(mPath);
                this.mRootPath = mPath;
                break;
            case R.id.format_filter_picture:
                mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_PICTURE);
                tmpType = "picture";
                this.mCurrentDirectory = new File(mPath);
                this.mRootPath = mPath;
                break;
            case R.id.format_filter_media:
                mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_MEDIA);
                tmpType = "music";
                this.mCurrentDirectory = new File(mPath);
                this.mRootPath = mPath;
                break;
//            case R.id.format_filter_img_videos:
//            case R.id.format_filter_text_videos:
//                mFormatFilterBarView.itemClickStyle(mFormatFilterBarView.getImgVideo(),
//                        mFormatFilterBarView.getTextVideo());
//                tmpType = "video";
//                this.mCurrentDirectory = new File(mPath);
//                this.mRootPath = mPath;
//                break;
            case R.id.format_filter_all:
                mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_ALL);
                tmpType = "";
                break;
            case R.id.format_filter_wb:
                mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_WHITE_BOARD);
                tmpType = "whiteboard";
                //scan iwb ewb evg
                /*if (!mPath.equals(SYSTEM_PATH)) {
                    this.mCurrentDirectory = new File(mPath);
                    this.mRootPath = mPath;
                }*/
                break;
        }

        if(mPathScan == null){
            Log.i("carl","mPathScan is null");
            Toast.makeText(FFileMainActivity.this, getString(R.string.message_no_storage), Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        File fileScan = new File(mPathScan);
        if (!tmpType.equals("") && !tmpType.equals("whiteboard") && fileScan.isDirectory() && fileScan.listFiles() != null
                && fileScan.listFiles().length > 0) {
            Log.d("wsldwo","filterclick, tmpType:"+tmpType);
            String localFileName = mPathScan;
            mSearchResult.clear();
            mTvFolderEmpty.setText("");
            if (mPathScan.lastIndexOf("/") > 0) {
                localFileName = mPathScan.substring(mPathScan.lastIndexOf("/") + 1);
            }
            getFileFormatDataList(localFileName, tmpType);
            this.mCurrentDirectory = new File(mPath);
            this.mRootPath = mPath;
        } else if (tmpType.equals("whiteboard")) {

                //openEvg(new File(mPath));
            openWhiteboardFiles();

        } else {
            if (mPath != null) {
                mTvFolderEmpty.setText(getString(R.string.folder_empty));
                open(new File(mPath));
                this.mRootPath = mPath;
            } else {
                Toast.makeText(FFileMainActivity.this, getString(R.string.message_no_storage), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        mMainLayout.setVisibility(View.VISIBLE);
        mBtnOperation.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mCurrentFilterView = null;
        mBottomView.startListGridChange(true);
    }

    private void updateFFBar(String path) {
        String tmpType = mSearchFiles.updateValue(path);
        if (tmpType != null) {
            String localFileName = mPathScan;
            if (mPathScan.lastIndexOf("/") > 0) {
                localFileName = mPathScan.substring(mPathScan.lastIndexOf("/") + 1);
            }
            mSearchResult.clear();

            boolean isCurrentFilterFile = false;
            switch (mFormatFilterBarView.getCurrentFilterStyle()) {
                case E_DOCUMENT:
//                    isCurrentFilterFile = tmpType.equals("document");
                    break;
                case E_MEDIA:
                    isCurrentFilterFile = tmpType.equals("music");
                    break;
                case E_PICTURE:
                    isCurrentFilterFile = tmpType.equals("picture");
                    break;
                case E_WHITE_BOARD:
                    isCurrentFilterFile = tmpType.equals("whiteboard");
                    break;
                default:
                    break;
            }
            if (isCurrentFilterFile) {
                getFileFormatDataList(localFileName, tmpType);
            }
        }
    }

    private void updateFotmatFiles(String path) {
        mSearchFiles.updateValue(path);
    }

    private void openWhiteboardFiles(){

        mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_WHITE_BOARD);

        Log.d("wsldwo","-----------------scan USB whiteboard BEGIN----------------");
        String localFileName = mPathScan;
        Log.d("wsldwo","mPathScan:"+mPathScan);
        mSearchResult.clear();
        mTvFolderEmpty.setText("");
        if (mPathScan.lastIndexOf("/") > 0) {
            localFileName = mPathScan.substring(mPathScan.lastIndexOf("/") + 1);
            Log.d("wsldwo","localFileName:"+localFileName);
        }
        getFileFormatDataList(localFileName, "whiteboard");
        this.mCurrentDirectory = new File(mPath);
        this.mRootPath = mPath;

        Log.d("wsldwo","-----------------scan USB whiteboard End----------------");
    }

    private void scanThread(final String localFile) {
        Log.d("wsldwo","scanThread, localFile:"+localFile);
        mFormatFilterBarView.childrenClickable(false);
        String localFileName = "";
        if (localFile.lastIndexOf("/") > 0) {
            localFileName = localFile.substring(localFile.lastIndexOf("/") + 1);
        }
        final String strTmp = localFileName;
        if (mThreadScanAllFiles != null && mThreadScanAllFiles.isAlive()) {
            mSearchFiles.setThreadStopFlag(true);
        }
        mThreadScanAllFiles = new Thread(new Runnable() {
            @Override
            public void run() {
                mSearchFiles.searchFilesForLocalStorage(localFile, strTmp);
                Message msg = new Message();
                msg.what = 110;
                msgHandler.sendMessage(msg);
            }

        });
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mThreadScanAllFiles.start();
        }

    }

    Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 108) {
                constructList(mSearchResult);
            } else if (msg.what == 110 && !mIsOperation && !mIsCopyStatus) {
                mFormatFilterBarView.childrenClickable(true);
                this.removeCallbacks(mThreadScanAllFiles);
            } else if (msg.what == 109) {
                mFormatThreadNum--;
                if (mFormatThreadNum == 0) {
                    if (mSearchLayout.getVisibility() == View.VISIBLE && !isSearchNull) {
                        if (mCurrentFilterView != null) {
                            filterToolClick(mCurrentFilterView);
                        } else {
                            constructListSearch(mSearchResult);
                        }
                    } else {
                        constructList(mSearchResult);
                    }
                }
            } else if (msg.what == MSG_RELOAD_CURRENT_DIRECTORY) {
                open(new File((String) msg.obj));
            }
        }
    };

    public void getFileFormatDataList(String usbName, final String formatStr) {
        if (!mPath.equals(INTERNAL_SDCARD_PATH)) {
            String strTmp = mPathScan;
            if (mPathScan.lastIndexOf("/") > 0) {
                strTmp = mPathScan.substring(mPathScan.lastIndexOf("/") + 1);
            }
            mSearchFiles.searchFilesForLocalStorage(mPathScan, strTmp);
        }
        final LocalStorageSharePreference lssp = new LocalStorageSharePreference(this, usbName, Activity.MODE_PRIVATE);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Set<String> setStr = new HashSet<String>();
                    setStr = lssp.getSetValue(formatStr, setStr);
                    if (setStr.size() > 100) {
                        LayoutInflater inflater = LayoutInflater.from(FFileMainActivity.this);
                        final View progressView = inflater.inflate(R.layout.layout_progress_bar, null);
                        progressDialog = new Dialog(FFileMainActivity.this, R.style.DialogNoOpacity);
                        progressDialog.setCancelable(false);
                        progressDialog.setContentView(progressView);
                        progressDialog.show();
                    }
                    dataSetManagement(setStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private void dataSetManagement(Set<String> setStr) {
        if (setStr != null && setStr.size() > 0) {
            int n = 0;
            Set<String> mSet = new ConcurrentSkipListSet<String>();

            for (String ss : setStr) {
                if (n > 0 && n % 1000 == 0) {
                    mFormatThreadNum++;
                    createThreadList(mSet, this);
                    mSet.clear();
                }
                File file = new File(ss);
                if (file.exists()) {
                    mSet.add(ss);
                }

                n++;
            }
            if (mSet.size() > 0) {
                mFormatThreadNum++;
                createThreadList(mSet, this);
            }
        } else {
            this.constructList(mSearchResult);
        }
    }

    private void createThreadList(Set<String> set, final Context myContext) {
        final List<String> list = new ArrayList<String>(set);
        final int iThreadFlag = mThreadFilesChange;
        new Thread(new Runnable() {
            @Override
            public void run() {
                AtomicReference<String> currentIcon = new AtomicReference<String>();
                String fileSize;
                String fileDate;
                boolean isFile;
                List<FileText> listTmp = new LinkedList<FileText>();
                if (list.size() > 0) {
                    for (String s : list) {
                        if (isSearchNull || iThreadFlag < mThreadFilesChange) {
                            break;
                        }
                        File f = new File(s);
                        String fileName = f.getName();
                        if (f.isDirectory()) {
                            currentIcon.set("file_icon_folder");
                            isFile = false;
                            fileSize = "";
                        } else {
                            if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingImage))) {
                                currentIcon.set("file_icon_img");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingAudio))) {
                                currentIcon.set("file_icon_audio");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingVideo))) {
                                currentIcon.set("file_icon_video");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingPackage))) {
                                currentIcon.set("file_icon_apk");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingWebText))) {
                                currentIcon.set("file_icon_web");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingText))) {
                                currentIcon.set("file_icon_text");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingWord))) {
                                currentIcon.set("file_icon_word");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingExcel))) {
                                currentIcon.set("file_icon_xls");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingPPT))) {
                                currentIcon.set("file_icon_ppt");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingPdf))) {
                                currentIcon.set("file_icon_pdf");
                            } else if (OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingRar))) {
                                currentIcon.set("file_icon_rar");
                            } else if(OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingWhiteboard))){
                                currentIcon.set("file_icon_whiteboard");
                            } else if(OpenFile.checkEndsWithInStringArray(fileName,
                                    myContext.getResources().getStringArray(R.array.fileEndingCertificate))){
                                currentIcon.set("file_icon_certificate");
                            } else {
                                currentIcon.set("file_icon_other");
                            }
                            fileSize = Tools.FormetFileSize(f.length());
                            isFile = true;
                        }
                        fileDate = Tools.getFileDate(f.lastModified());
                        listTmp.add(new FileText(f.getName().replace("/", ""), currentIcon.get(), fileSize, fileDate,
                                f.getAbsolutePath(), isFile, false));
                    }
                    synchronized (this) {
                        mSearchResult.addAll(listTmp);
                    }
                }
                Message msg = new Message();
                msg.what = 109;
                msgHandler.sendMessage(msg);
            }
        }).start();
    }

    private boolean isSearchNull = false;
    private EditText editTextTool;

    public void initialSearchLayout() {
        this.mCurrentDirectory = new File(mPath);
        this.mRootPath = mPath;
        mTmpSearchFile = null;
        mInitSearch = 0;
        constructList(new ArrayList<FileText>());
        initSearch();
        mThreadFilesChange = 100;
        mFilePathView.setBtnPath1Text(getResources().getString(R.string.search));
        if (mSearchBarTool == null) {
            mSearchBarTool = (SearchBar) findViewById(R.id.search_tools);
            View.OnClickListener filterBtnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterToolClick(view);
                }
            };
            mSearchBarTool.selfSetClickListener(filterBtnClickListener);
        }
        mInitSearch++;
        if (editTextTool.getText() != null && !editTextTool.getText().toString().trim().equals("")) {
            isSearchNull = false;
            mThreadFilesChange++;
            mSearchFiles.searchFile(editTextTool.getText().toString());
            mSearchResult.clear();
            if (0 < mSearchFiles.getTmpSearchResult().size()) {
                dataSetManagement(mSearchFiles.getTmpSearchResult());
                if (null != mCurrentFilterView) {
                    filterToolClick(mCurrentFilterView);
                }
            } else {
                if (null != mCurrentFilterView) {
                    filterToolClick(mCurrentFilterView);
                } else {
                    constructListSearch(mSearchResult);
                }
            }
        } else {
            isSearchNull = true;
            mSearchResult.clear();
            constructListSearch(mSearchResult);
        }
        mSearchResult.clear();
        filterToolClick(mSearchBarTool.getAllBtn());
//        editTextTool.setText("");
    }

    private void filterToolClick(View view) {
        mCurrentFilterView = view;
        int viewId = view.getId();
        mSearchBarTool.clickStyleChange(view);
        switch (viewId) {
            case R.id.search_top_btn_all:
                constructListSearch(mSearchResult);
                break;
            case R.id.search_top_btn_doc:
                List<FileText> wordList = searchResultFilter(R.array.fileEndingWord, mSearchResult);
                constructListSearch(wordList);
                break;
            case R.id.search_top_btn_xls:
                List<FileText> excelList = searchResultFilter(R.array.fileEndingExcel, mSearchResult);
                constructListSearch(excelList);
                break;
            case R.id.search_top_btn_ppt:
                List<FileText> pptList = searchResultFilter(R.array.fileEndingPPT, mSearchResult);
                constructListSearch(pptList);
                break;
            case R.id.search_top_btn_pdf:
                List<FileText> pdfList = searchResultFilter(R.array.fileEndingPdf, mSearchResult);
                constructListSearch(pdfList);
                break;
            case R.id.search_top_btn_pic:
                List<FileText> picList = searchResultFilter(R.array.fileEndingImage, mSearchResult);
                constructListSearch(picList);
                break;
            case R.id.search_top_btn_music:
                List<FileText> musicList = searchResultFilter(R.array.fileEndingAudio, mSearchResult);
                constructListSearch(musicList);
                break;
            case R.id.search_top_btn_video:
                List<FileText> videoList = searchResultFilter(R.array.fileEndingVideo, mSearchResult);
                constructListSearch(videoList);
                break;
        }
    }

    private List<FileText> searchResultFilter(int arrayFilterId, List<FileText> result) {
        List<FileText> tmpList = new ArrayList<FileText>();
        for (FileText ft : result)
            if (OpenFile.checkEndsWithInStringArray(ft.getFilePath().toLowerCase(),
                    this.getResources().getStringArray(arrayFilterId))) {
                tmpList.add(ft);
            }
        return tmpList;
    }

    private void constructListSearch(List<FileText> listFile) {
        if (listFile == null || listFile.size() == 0) {
            mSearchListView.setAdapter(null);
            mSearchListView.setVisibility(View.GONE);
            mTvFolderEmptySearch.setVisibility(View.VISIBLE);
            if (mInitSearch > 0) {
                mFilePathView.setBtnPath1Text(getString(R.string.search_result) + ":0");
            }
        } else {
            List<FileText> tmpResult = new ArrayList<FileText>(listFile);
            mFilePathView.setBtnPath1Text(getString(R.string.search_result) + ":" + tmpResult.size());
            loadDataFromListSearch(tmpResult);
        }
    }

    private void loadDataFromListSearch(final List<FileText> searchFiles) {
        Collections.sort(searchFiles, new Comparator<FileText>() {
            @Override
            public int compare(FileText f1, FileText f2) {
                if (!f1.isFile() && f2.isFile()) {
                    return -1;
                } else if (f1.isFile() && !f2.isFile()) {
                    return 1;
                }
                return f1.getText().toLowerCase().compareTo(f2.getText().toLowerCase());
            }
        });
        mSearchListView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPositionX = (int) event.getX();
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        mSearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentView = view;
                File tmpFile = new File(searchFiles.get(i).getFilePath());
                if (tmpFile.isDirectory()) {
                    mSearchLayout.setVisibility(View.GONE);
                    mMainLayout.setVisibility(View.VISIBLE);
                    mFilePathView.setBtnPath1Clickable(true);
                    mFilePathView.setBtnPath1OnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            initSearch();
                        }
                    });

                    mBottomView.startListGridChange(true);

                    final Button button = mFilePathView.addPathButton(tmpFile.getName(), tmpFile.getAbsolutePath());
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pathBtnClick(button);
                        }
                    });
                    mTmpSearchFile = tmpFile;
                }
                open(tmpFile);

            }
        });
        mFileTextAdapterList = new FileTextAdapter(this, "list");
        mFileTextAdapterList.setFiles(searchFiles);
        mSearchListView.setAdapter(mFileTextAdapterList);
        mSearchListView.setVisibility(View.VISIBLE);
        mTvFolderEmptySearch.setVisibility(View.GONE);
        if (searchFiles.size() < 1) {
            mSearchListView.setVisibility(View.GONE);
            mTvFolderEmptySearch.setVisibility(View.VISIBLE);
        }
    }

    private void finishPaste() {
        open(new File(getCurrentDirectory()));
        mFormatFilterBarView.childrenClickable(true);
        mIsCopyStatus = false;
        mIsOperation = false;
        mOperationLayout.setVisibility(View.GONE);
        mOperationPasteLayout.setVisibility(View.GONE);
        mBtnOperation.setSelected(false);
    }

    public void onDestroy() {
        mFiles.clear();
        mCurrentDirectory = new File(SYSTEM_PATH);
        mTempFilesList.clear();
        HScreenUtils.getInstance().bitmapRecycle();
        unregisterReceiver(mFinishReceiver);
        unregisterReceiver(usbListBroadcastReceiver);
        unregisterReceiver(newUsbBroadcastReceiver);
        SkinManager.getInstance().unregister(this);
        mRecursiveFileObserver.stopWatching();
        super.onDestroy();
    }

    private BroadcastReceiver usbListBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_USB_LIST)) {
                final String fullPath = intent.getStringExtra("usb_path");
                final File usbPath;
                if (fullPath.contains("storage/emulated/0") && (!mIsCopyStatus)) {
//                    mFormatFilterBarView.controlWB(true);
                    mIsFilterWb = true;
                    usbPath = new File(SYSTEM_PATH);
                    mUsbName = getString(R.string.system_files);
                } else {
//                    mFormatFilterBarView.controlWB(false);
                    usbPath = new File(fullPath);
                    mUsbName = Tools.getUsbDeviceLabel(FFileMainActivity.this, fullPath);
                    if (mUsbName.equals("0")) {
                        mUsbName = getResources().getString(R.string.system_files);
                    }
                }

                if (usbPath.exists() && usbPath.isDirectory()) {
                    Log.d("wsldwo","usbPath:"+usbPath);
                    initNavigation();
                    mPath = usbPath.toString();
                    mPathScan = fullPath;
                    mCurrentFilterView = null;
                    mSearchLayout.setVisibility(View.GONE);
                    mMainLayout.setVisibility(View.VISIBLE);
                    mBottomView.startListGridChange(true);
                    mBtnOperation.setVisibility(View.VISIBLE);
                    mTmpSearchFile = null;

                    if (usbPath.toString().equals(SYSTEM_PATH)) {
                        //if (mIsOperation) {
                            open(new File(SYSTEM_PATH));
                            mRootPath = SYSTEM_PATH;
                        //} else {
                            //open(new File(SYSTEM_PATH));
                            //mRootPath = SYSTEM_PATH;
                            /*
                            open(new File(mWbPath));
                            mFormatFilterBarView.itemClickStyle(mFormatFilterBarView.getImgWb(),
                                    mFormatFilterBarView.getTextWb());
                            mRootPath = mWbPath;*/
                            //openWhiteboardFiles();
                       // }
                        mFormatFilterBarView.initialStyle();
                    } else {
                        open(usbPath);
                        mRootPath = usbPath.toString();
                    }
                    scanThread(mPathScan);

                    if (mIsOperation && !mIsCopyStatus) {
                        mTempFilesList.clear();
                    }
                    mFilePathView.setBtnPath1Text(mUsbName);
                    mFilePathView.setBtnPath1OnClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!mIsOperation) {
                                mFormatFilterBarView.selectFilterStyle(EnumFormatFilterType.E_ALL);
                                initNavigation();
                                open(usbPath);
                            }
                        }
                    });
                }
            }
        }
    };

    private void updateLocalSP(String fileName, String type) {
        String setName = "";
        String localFileName = mPathScan;
        if (localFileName.endsWith("/")) {
            localFileName = localFileName.substring(0, localFileName.length() - 2);
        }
        if (localFileName.lastIndexOf("/") > 0) {
            localFileName = mPathScan.substring(mPathScan.lastIndexOf("/") + 1);
        }
        if (mCurrentFilterView != null) {

            switch (mCurrentFormatFilterView.getId()) {
                case R.id.format_filter_document:
                    setName = "document";
                    break;
                case R.id.format_filter_picture:
                    setName = "picture";
                    break;
                case R.id.format_filter_media:
                    setName = "music";
                    break;
//                case R.id.format_filter_img_videos:
//                    setName = "video";
//                    break;
                default:
                    break;
            }
            if (!setName.equals("")) {
                mSearchFiles.updateLocalStorage(fileName, localFileName, setName, type);
            }

        } else {
            if (type.equals("add")) {
                if (!mSearchFiles.mSearchFileSet.contains(fileName)) {
                    mSearchFiles.mSearchFileSet.add(fileName);
                }
            } else if (type.equals("del")) {
                mSearchFiles.mSearchFileSet.remove(fileName);
            }
            mSearchFiles.updateLS(fileName, localFileName, type);
        }
    }


    private void setUsbPath() {
        initLeftBar();
        mFilePathView.setBtnPath1Text(mUsbName);
        if (mPath.equals(SYSTEM_PATH)) {
            /*open(new File(mWbPath));
            mFormatFilterBarView.itemClickStyle(mFormatFilterBarView.getImgWb(), mFormatFilterBarView.getTextWb());
            mRootPath = mWbPath;*/
            //openWhiteboardFiles();
            open(new File(SYSTEM_PATH));
            mRootPath = SYSTEM_PATH;
        } else {
            open(new File(mPath));
            mRootPath = mPath;
        }
        scanThread(mPathScan);
        initNavigation();
        mMainLayout.setVisibility(View.VISIBLE);
        mSearchLayout.setVisibility(View.GONE);
        mBottomView.startListGridChange(true);
        mBtnOperation.setVisibility(View.VISIBLE);
    }

    private BroadcastReceiver newUsbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String usbPath = intent.getData().getPath();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                mNewUsbList.add(usbPath);
                initUsb(mNewUsbList);
                setUsbPath();
            } else if (action.equals(Intent.ACTION_MEDIA_REMOVED)) {
                if (mNewUsbList.contains(usbPath)) {
                    mNewUsbList.remove(usbPath);
                    initUsb(mNewUsbList);
                    mIsDeleteFlag = false;
                }
                if (getCurrentDirectory().startsWith(usbPath)) {
                    setUsbPath();
                }

            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_main:
//                finish();
                break;
            case R.id.format_filter_bar_second:
            case R.id.bottom_view:
            case R.id.tv_folder_empty:
            case R.id.layout_top_view:
            case R.id.searchLayout:
                break;
        }
    }

    //jiangwensong
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            // Back和Enter键只在ACTION_UP事件上起作用
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_BACK:
                case KeyEvent.KEYCODE_ESCAPE:
                    return super.dispatchKeyEvent(event);
                default:
                    return true;
            }
        }
        boolean isDeal = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mListView.hasFocus()) {
                    mFormatFilterBarView.requestFilterFocus();
                    isDeal = true;
                } else if (editTextTool.hasFocus()) {
                    mStorageItemGroup.lastItemFocus();
                    isDeal = true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mFormatFilterBarView.hasFocus()) {
                    if (mFileTextAdapterList.getCount() == 0) {
                        isDeal = true;
                    } else {

                    }
                } else if (editTextTool.hasFocus()) {
                    mBtnFindFile.requestFocus();
                    isDeal = true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mBottomView.hasFocus()) {
                    if (mListView.getVisibility() == View.VISIBLE) {
                        mutiItemViewRequestFocus(mListView);
                    } else if (mGridView.getVisibility() == View.VISIBLE) {
                        mutiItemViewRequestFocus(mGridView);
                    }
                    isDeal = true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mStorageItemGroup.isFirstFocus()) {
                    isDeal = mStorageItemGroup.forceItemSelectFocus();
                    break;
                }
                if (mStorageDeviceLayout.hasFocus()) {
                    if (mListView.getVisibility() == View.VISIBLE) {
                        mutiItemViewRequestFocus(mListView);
                    } else if (mGridView.getVisibility() == View.VISIBLE) {
                        mutiItemViewRequestFocus(mGridView);
                    } else if (mSearchListView.getVisibility() == View.VISIBLE) {
                        mutiItemViewRequestFocus(mSearchListView);
                    } else {
                        mFormatFilterBarView.requestFilterFocus();
                    }
                    isDeal = true;
                    mStorageItemGroup.layoutFocusStatus(false);
                } else {
                }
                break;
        }
        return isDeal || super.dispatchKeyEvent(event);
    }

    private void mutiItemViewRequestFocus(AbsListView view) {
        // 这里调用setFocusable是因为首次切换列表或网格显示时，焦点会落在列表上，
        // 因此我需要初始化时，先将列表焦点功能取消，后面需要用上焦点时再设置
        if (!view.isFocusable())
            view.setFocusable(true);
        view.requestFocus();
        view.setSelection(0);
    }

    private View.OnFocusChangeListener mFocusChangeListenerClick = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            /**
             * 被注释代码会影响焦点在文件过滤栏转移到设备栏后，无法将焦点强制定在被选中的设备按钮上
             */
//            if (v.isSelected()){
//                return;
//            }
            int id = v.getId();
            switch (id) {
                case R.id.lv_files:
                    mListView.setSelector(hasFocus ?
                            R.drawable.list_selector : R.drawable.listview_disable_seletcor);
                    break;
                case R.id.gv_files:
                    mGridView.setSelector(hasFocus ?
                            R.drawable.grid_selector : R.drawable.listview_disable_seletcor);
                    break;
                case R.id.searchResultList:
                    mSearchListView.setSelector(hasFocus ?
                            R.drawable.grid_selector : R.drawable.listview_disable_seletcor);
                    break;
            }

            // 如果焦点在设备栏上，则强制让被选中的设备获取焦点
            if (hasFocus && mStorageDeviceLayout.hasFocus()) {
                mStorageItemGroup.forceItemSelectFocus();
            }
        }
    };

    class CopyFilesTask extends AsyncTask<Void, Integer, Boolean> {
        private CustomProgressDialog myCustomProgressDialog;

        public CopyFilesTask() {
            mIsFinishAsyncTask = false;
        }

        public CopyFilesTask(File source, File target) {
        }

        @Override
        protected void onPreExecute() {
            myCustomProgressDialog = new CustomProgressDialog(FFileMainActivity.this,
                    getResources().getString(R.string.paste_loading));
            myCustomProgressDialog.setCancelable(false);
            myCustomProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (mTempFilesList != null) {
                for (final File oldFile : mTempFilesList) {
                    if (!mIsFinishAsyncTask) {
                        File toFile;
                        if (oldFile.toString().startsWith(getCurrentDirectory())) {
                            String fileName = Tools.getFileName(oldFile.toString());
                            String fileSuffix = Tools.getSuffix(oldFile.toString());
                            toFile = new File(getCurrentDirectory() + File.separator + fileName + "-(1)" + fileSuffix);
                        } else {
                            toFile = new File(getCurrentDirectory() + File.separator + oldFile.getName());
                        }
                        copy(oldFile, toFile);
                        updateLocalSP(getCurrentDirectory() + File.separator + toFile.getName(), "add");
                    }
                }
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        protected void onPostExecute(final Boolean result) {

            try {
                Runtime.getRuntime().exec("sync");
            } catch (IOException e) {
                e.printStackTrace();
            }
            execShell("sync");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myCustomProgressDialog.dismiss();
            Toast.makeText(FFileMainActivity.this, getResources().getString(R.string.message_paste), Toast.LENGTH_SHORT)
                    .show();
            finishPaste();
        }

    }

    Handler handlerCopy = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x101) {
                Bundle bundle = msg.getData();
                int number = bundle.getInt("sameNameNumber");
                if (number > 0) {
                    String content = number == 1 ?
                            getResources().getString(R.string.message_paste3) :
                            String.format(getResources().getString(R.string.message_paste_plural), number);
                    AlertDialog.Builder builder = BuilderUtil.getBuilder(FFileMainActivity.this,
                            getResources().getString(R.string.message_paste2),
                            content);
                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mCopyFilesTask != null && mCopyFilesTask.getStatus() != AsyncTask.Status.FINISHED) {
                                mIsFinishAsyncTask = true;
                                mCopyFilesTask.cancel(true);
                            }
                            mCopyFilesTask = new CopyFilesTask();
                            mCopyFilesTask.execute();
                        }
                    });
                    BuilderUtil.setNegativeButton(builder);
                } else {
                    if (mCopyFilesTask != null && mCopyFilesTask.getStatus() != AsyncTask.Status.FINISHED) {
                        mIsFinishAsyncTask = true;
                        mCopyFilesTask.cancel(true);
                    }
                    mCopyFilesTask = new CopyFilesTask();
                    mCopyFilesTask.execute();
                }
            }
            super.handleMessage(msg);
        }
    };

    public static void execShell(String cmd) {

        try {
            Process p = Runtime.getRuntime().exec("su");
            OutputStream outputStream = p.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        Bitmap bmp = BitmapFactory.decodeFile(path, opts);
        if (bmp != null) {
            WeakReference<Bitmap> weak = new WeakReference<Bitmap>(bmp);
            return Bitmap.createScaledBitmap(weak.get(), w, h, true);
        } else {
            return null;
        }
    }

    public class RecursiveFileObserver extends FileObserver {
        Map<String, DirectoryObserver> mObservers;
        String mRootPath;
        String mCurrentPath;
        int mMask;

        public RecursiveFileObserver(String path) {
            this(path, ALL_EVENTS);
        }

        public RecursiveFileObserver(String path, int mask) {
            super(path, mask);
            mRootPath = path;
            mCurrentPath = path;
            mMask = mask;
        }

        @Override
        public void startWatching() {
            if (mObservers != null)
                return;
            mObservers = new ArrayMap<>();
            Stack stack = new Stack();
            stack.push(mRootPath);

            while (!stack.isEmpty()) {
                String temp = (String) stack.pop();
                mObservers.put(temp, new DirectoryObserver(temp, mMask));
                File path = new File(temp);
                File[] files = path.listFiles();
                if (null == files)
                    continue;
                for (File f : files) {
                    if (f.isDirectory() && !f.getName().equals(".") && !f.getName()
                            .equals("..")) {
                        stack.push(f.getAbsolutePath());
                    }
                }
            }
            Iterator<String> iterator = mObservers.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                mObservers.get(key).startWatching();
            }
        }

        @Override
        public void stopWatching() {
            if (mObservers == null)
                return;

            Iterator<String> iterator = mObservers.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                mObservers.get(key).stopWatching();
            }
            mObservers.clear();
            mObservers = null;
        }

        @Override
        public void onEvent(int event, String path) {
            switch (event) {
                case FileObserver.CREATE:
                    File file = new File(path);
                    if (file.isDirectory()) {
                        Stack stack = new Stack();
                        stack.push(path);
                        while (!stack.isEmpty()) {
                            String temp = (String) stack.pop();
                            if (mObservers.containsKey(temp)) {
                                continue;
                            } else {
                                DirectoryObserver sfo = new DirectoryObserver(temp, mMask);
                                sfo.startWatching();
                                mObservers.put(temp, sfo);
                            }
                            File tempPath = new File(temp);
                            File[] files = tempPath.listFiles();
                            if (null == files)
                                continue;
                            for (File f : files) {
                                if (f.isDirectory() && !f.getName().equals(".") && !f.getName()
                                        .equals("..")) {
                                    stack.push(f.getAbsolutePath());
                                }
                            }
                        }
                    }
                    break;
                case 0x40000100: // CREATE_DIR
                    addNewDirObserver(path);
                    break;
                case 0x40000200: // DELETE_DIR
                    removeDirObserver(path);
                    break;
                default:
                    break;
            }
        }

        public void openDirectory(String path) {
            mCurrentPath = path;
        }

        private void addNewDirObserver(String path) {
            File file = new File(path);
            if (file.isDirectory()) {
                Stack stack = new Stack();
                stack.push(path);
                while (!stack.isEmpty()) {
                    String temp = (String) stack.pop();
                    if (mObservers.containsKey(temp)) {
                        continue;
                    } else {
                        DirectoryObserver sfo = new DirectoryObserver(temp, mMask);
                        sfo.startWatching();
                        mObservers.put(temp, sfo);
                    }
                    File tempPath = new File(temp);
                    File[] files = tempPath.listFiles();
                    if (null == files)
                        continue;
                    for (File f : files) {
                        if (f.isDirectory() && !f.getName().equals(".") && !f.getName()
                                .equals("..")) {
                            stack.push(f.getAbsolutePath());
                        }
                    }
                }
            }
        }

        private void removeDirObserver(String path) {
            Stack stack = new Stack();
            stack.push(path);
            while (!stack.isEmpty()) {
                String temp = (String) stack.pop();
                if (mObservers.containsKey(temp)) {
                    mObservers.get(temp).stopWatching();
                    mObservers.remove(temp);
                }
                File tempPath = new File(temp);
                File[] files = tempPath.listFiles();
                if (null == files)
                    continue;
                for (File f : files) {
                    if (f.isDirectory() && !f.getName().equals(".") && !f.getName()
                            .equals("..")) {
                        stack.push(f.getAbsolutePath());
                    }
                }
            }
        }

        private class DirectoryObserver extends FileObserver {
            private String mOBPath;
            private boolean isCreate = false;
            private boolean isMoveFrom = false;

            public DirectoryObserver(String path) {
                super(path);
                mOBPath = path;
            }

            public DirectoryObserver(String path, int mask) {
                super(path, mask);
                mOBPath = path;
            }

            @Override
            public void onEvent(int event, String path) {
                switch (event) {
                    case CREATE:
                        isCreate = true;
                        reloadDirectory();
                        notifyChangeInStorage(path);
                        break;
                    case CLOSE_WRITE:
                        if (isCreate) {
                            isCreate = false;
                            reloadDirectory();
                            notifyChangeInStorage(path);
                        }
                    case MOVED_FROM:
                        isMoveFrom = true;
                        break;
                    case MOVED_TO:
                        if (isMoveFrom) {
                            isMoveFrom = false;
                            reloadDirectory();
                        }
                        break;
                    case DELETE:
                        reloadDirectory();
                        updateFotmatFiles(mOBPath + '/' + path);
                        break;
                    case 0x40000100: // CREATE_DIR
                    case 0x40000200: // DELETE_DIR
                        reloadDirectory();
                        break;
                    default:
                        break;
                }
                String newPath = mOBPath + "/" + path;
                RecursiveFileObserver.this.onEvent(event, newPath);
            }

            private void reloadDirectory() {
                if (mOBPath.equals(RecursiveFileObserver.this.mCurrentPath) &&
                        mOBPath.contains(mPath) &&
                        mFormatFilterBarView.getCurrentFilterStyle().equals(EnumFormatFilterType.E_ALL)) {
                    mFileChanged = true;
                    Message msg = msgHandler.obtainMessage(MSG_RELOAD_CURRENT_DIRECTORY);
                    msg.obj = mOBPath;
                    msgHandler.sendMessage(msg);
                }
            }

            private void notifyChangeInStorage(String path) {
                if (mOBPath.contains(mPath)) {
                    mFileChanged = true;
                    updateFFBar(mOBPath + '/' + path);
                }
            }
        }
    }
}
