package com.hht.filebrowser.media;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.hht.uc.FileBrowser.R;
import com.hht.filebrowser.adapter.MediaAdapter;
import com.hht.filebrowser.media.callback.IMedia;
import com.hht.filebrowser.media.presenter.MediaControl;
import com.hht.skin.main.SkinManager;
import com.mstar.android.media.MMediaPlayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class VideoActivity extends Activity implements IMedia.iMediaView, OnClickListener, OnItemClickListener, OnSeekBarChangeListener {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private SeekBar mSeekBar;
    private ImageView mLastVideo;
    private ImageView mPlayVideo;
    private ImageView mNextVideo;
    private TextView mVideoName;
    private TextView mTvPlayyingTime;
    private TextView mVideoTime;
    private ListView mListView;
    private LinearLayout mOpenList;
    private LinearLayout mExit;
    private String mFilePath;
    private boolean mIsOpen = false;
    private TextView mTvPlay;
    private MediaControl mControl;
    private LinearLayout mNoVideLayout;
    private LinearLayout mOperationLayout;
    private boolean isOperationState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_layout);
        SkinManager.getInstance().register(this);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("path");

        initView();
    }

    public void showDefaultPhoto(boolean isShow) {
        if (isShow) {
            mSurfaceView.setBackgroundResource(R.drawable.bg);
        } else {
            mSurfaceView.setBackground(null);
        }
    }

    private void initView() {
        mNoVideLayout = (LinearLayout) this.findViewById(R.id.nonevideolayout);
        mSurfaceView = (SurfaceView) findViewById(R.id.video_surfaceview);
        mOperationLayout = (LinearLayout) findViewById(R.id.operation_layout);
        mListView = (ListView) findViewById(R.id.list_video_player);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar_video);
        mLastVideo = (ImageView) findViewById(R.id.last_video);
        mPlayVideo = (ImageView) findViewById(R.id.play_video);
        mNextVideo = (ImageView) findViewById(R.id.next_video);
        mVideoName = (TextView) findViewById(R.id.text_video_name);
        mTvPlay = (TextView) findViewById(R.id.text_play_video);
        mTvPlayyingTime = (TextView) findViewById(R.id.text_playying_time);
        mVideoTime = (TextView) findViewById(R.id.text_video_time);
        mOpenList = (LinearLayout) findViewById(R.id.layout_open_video_list);
        mExit = (LinearLayout) findViewById(R.id.exit_layout);
        mSurfaceView.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mPlayVideo.setOnClickListener(this);
        mNextVideo.setOnClickListener(this);
        mLastVideo.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mListView.setOnItemClickListener(this);
        mOpenList.setOnClickListener(this);
        mHolder = mSurfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setFixedSize(176, 144);
        mHolder.setKeepScreenOn(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(new SurfaceCallback());
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(this.getClass().getSimpleName(), "onResume");
        initData();
        sendBroadCastToToolBar(HIDE_TOOLBAR);
    }

    private final String HIDE_TOOLBAR = "com.hht.action.HIDE_TOOLBAR";

    private void sendBroadCastToToolBar(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }

    private void initData() {
        mControl = new MediaControl(VideoActivity.this, mFilePath, false);
        mControl.setCallBackListener(new MediaControl.VideoLister() {
            @Override
            public void videoCallBack(int what) {
                if (what == MMediaPlayer.MEDIA_INFO_VIDEO_UNSUPPORT) {
                    showDefaultPhoto(true);
                } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    showDefaultPhoto(false);
                }
            }
        });
        MediaAdapter adapter = new MediaAdapter(VideoActivity.this, mControl.getVideoFileTextList(mFilePath));
        mListView.setAdapter(adapter);
    }

    private String getCurrentTime(int time) {
        SimpleDateFormat format = null;
        Date date = new Date(time);
        if (time > 1000 * 60 * 60) {
            format = new SimpleDateFormat("hh:mm:ss");
        } else {
            format = new SimpleDateFormat("mm:ss");
        }
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return format.format(date);
    }

    @Override
    public void updateMediaState(boolean state) {
        if (state) {
            mTvPlay.setText("播放");
            mPlayVideo.setImageResource(R.drawable.ic_media_play);
        } else {
            mTvPlay.setText("暂停");
            mPlayVideo.setImageResource(R.drawable.ic_media_pause);
        }
    }

    @Override
    public void updateSeekbar(int position) {
        mSeekBar.setProgress(position);
        mTvPlayyingTime.setText(getCurrentTime(position));
    }

    @Override
    public void initSeekBar(int max, String mediaName, String mediaTime) {
        mSeekBar.setMax(max);
        mVideoName.setText(mediaName);
        mVideoTime.setText(mediaTime);
    }

    @Override
    public void resetView() {
        mTvPlayyingTime.setText("00:00");
        mSeekBar.setProgress(0);
        updateMediaState(true);
    }


    class SurfaceCallback implements Callback {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            mControl.initMedia(holder);
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_surfaceview:
                if (isOperationState) {
                    mOperationLayout.setVisibility(View.GONE);
                    isOperationState = false;
                } else {
                    mOperationLayout.setVisibility(View.VISIBLE);
                    isOperationState = true;
                }
                break;
            case R.id.play_video:
                mControl.playOrPauseMedia();
                break;
            case R.id.last_video:
                mControl.lastMedia(mHolder);
                break;
            case R.id.next_video:
                mControl.nextMedia(mHolder);
                break;
            case R.id.layout_open_video_list:
                makeListEnable();
                break;
            case R.id.exit_layout:
                finish();
                break;
        }
    }

    private void makeListEnable() {
        if (mIsOpen) {
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);
        }
        mIsOpen = !mIsOpen;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mControl.removeHandlerCallback();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mControl.seekMedia(seekBar.getProgress());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mControl.swichMediaByPositon(mHolder, position);
    }

    @Override
    protected void onPause() {
        mControl.realease();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        SkinManager.getInstance().unregister(this);
        super.onDestroy();
    }

}
