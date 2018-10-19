package com.hht.filebrowser.imageload;

import java.util.ArrayList;

import com.hht.skin.main.SkinManager;
import com.hht.uc.FileBrowser.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class ImageLoaderActivity extends Activity implements OnClickListener {
	private static final int SCAN_OK = 0x1111;
	private static final int IMAGE_LOAD_RESULT = 0x1112;
	private ImageLoadAdapter mAdapter;
	private GridView mGridView;
	private TextView mBacCancel;
	private TextView mBacConfirm;
	private ArrayList<String> mPathList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.whiteboard_bac_addmore);
		SkinManager.getInstance().register(this);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.gravity = Gravity.TOP;
		params.y = 72;
		getWindow().setAttributes(params);
		mGridView = (GridView) findViewById(R.id.bac_addmore_gridview);
		mBacCancel = (TextView) findViewById(R.id.user_difined_bac_cancel);
		mBacConfirm = (TextView) findViewById(R.id.user_difined_bac_confirm);
		mBacCancel.setOnClickListener(this);
		mBacConfirm.setOnClickListener(this);
	}

	private void loadImages() {
		mPathList = new ArrayList<String>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = getContentResolver();
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);

				if (mCursor == null) {
					return;
				}
				while (mCursor.moveToNext()) {
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					if (!mPathList.contains(path)) {
						mPathList.add(path);
					}
				}
				mScanHandler.sendEmptyMessage(SCAN_OK);
				mCursor.close();
			}
		}).start();
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}
	private boolean mIsOnlyOne = true;
	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if (intent != null) {
			mIsOnlyOne= intent.getBooleanExtra("onlyOne", true);
		}
		loadImages();
	}

	private Handler mScanHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case SCAN_OK:
				mAdapter = new ImageLoadAdapter(ImageLoaderActivity.this, mPathList, mGridView,mIsOnlyOne);
				mGridView.setAdapter(mAdapter);
				break;
			}
			return true;
		}
	});

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_difined_bac_cancel:
			ImageLoaderActivity.this.finish();
			break;
		case R.id.user_difined_bac_confirm:
			Intent intent = new Intent();
			ArrayList<String> pathList = mAdapter.getSelectPath();
			intent.putStringArrayListExtra("path",pathList);
			ImageLoaderActivity.this.setResult(IMAGE_LOAD_RESULT, intent);
			ImageLoaderActivity.this.finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		SkinManager.getInstance().unregister(this);
		super.onDestroy();
	}
}
