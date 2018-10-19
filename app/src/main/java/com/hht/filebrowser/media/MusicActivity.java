package com.hht.filebrowser.media;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.hht.uc.FileBrowser.R;
import com.hht.filebrowser.adapter.MediaAdapter;
import com.hht.filebrowser.media.callback.IMedia;
import com.hht.filebrowser.media.presenter.MediaControl;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class MusicActivity extends Activity implements IMedia.iMediaView, View.OnClickListener,AdapterView.OnItemClickListener,SeekBar.OnSeekBarChangeListener{

    private LinearLayout mOpenList;
    private ListView mListView;
    private ImageView mLastMusic;
    private ImageView mPlayMusic;
    private ImageView mNextMusic;
    private ImageView mExitMusic;
    private TextView mTvPlayyingTime;
    private TextView mTvMusicTime;
    private TextView mMusicName;
    private SeekBar mSeekBar;
    private boolean mIsOpen = false;
    private TextView mTvPlay;
    private String mFilePath;
    private MediaControl mControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_layout);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.TOP;
        params.y = 90;
        getWindow().setAttributes(params);
        initView();
    }
    private void initView() {
        mListView = (ListView)findViewById(R.id.list_music_player);
        mSeekBar = (SeekBar)findViewById(R.id.seekbar_music);
        mLastMusic = (ImageView)findViewById(R.id.last_music);
        mPlayMusic = (ImageView)findViewById(R.id.play_music);
        mNextMusic = (ImageView)findViewById(R.id.next_music);
        mExitMusic = (ImageView)findViewById(R.id.exit_music);
        mTvPlay = (TextView)findViewById(R.id.text_play_music);
        mTvPlayyingTime = (TextView)findViewById(R.id.text_playying_time);
        mTvMusicTime = (TextView)findViewById(R.id.text_music_time);
        mMusicName = (TextView)findViewById(R.id.text_music_name);
        mOpenList = (LinearLayout)findViewById(R.id.layout_open_music_list);
        mPlayMusic.setOnClickListener(this);
        mNextMusic.setOnClickListener(this);
        mLastMusic.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mExitMusic.setOnClickListener(this);
        mTvMusicTime.setOnClickListener(this);
        mTvPlayyingTime.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mOpenList.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("path");
        initData();
    }

    private void initData() {
        mControl = new MediaControl(this, mFilePath,true);
        MediaAdapter adapter = new MediaAdapter(MusicActivity.this, mControl.getPathList());
        mListView.setAdapter(adapter);
        mControl.initMedia(null);
        mControl.playOrPauseMedia();
    }
    private String getCurrentTime(int time) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(date);
    }
    @Override
    public void updateMediaState(boolean state) {
        if (state) {
            mTvPlay.setText("播放");
            mPlayMusic.setImageResource(R.drawable.ic_media_play);
        } else {
            mTvPlay.setText("暂停");
            mPlayMusic.setImageResource(R.drawable.ic_media_pause);
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
        mMusicName.setText(mediaName);
        mTvMusicTime.setText(mediaTime);
    }

    @Override
    public void resetView() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_music:
                mControl.playOrPauseMedia();
                break;
            case R.id.last_music:
                mControl.lastMedia(null);
                break;
            case R.id.next_music:
                mControl.nextMedia(null);
                break;
            case R.id.layout_open_music_list:
                makeListEnable();
                break;
            case R.id.exit_music:
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
        mControl.swichMediaByPositon(null, position);
    }
    @Override
    protected void onPause() {
        mControl.realease();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
