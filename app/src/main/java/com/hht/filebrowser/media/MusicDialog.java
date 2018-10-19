package com.hht.filebrowser.media;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.hht.uc.FileBrowser.R;
import com.hht.filebrowser.adapter.MediaAdapter;
import com.hht.filebrowser.media.presenter.MediaControl;
import com.hht.filebrowser.media.callback.IMedia;
import com.hht.skin.main.SkinManager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MusicDialog implements IMedia.iMediaView, OnClickListener, OnItemClickListener, OnSeekBarChangeListener {
    private LinearLayout mMusicLayout;
    private LinearLayout mOpenList;
    private ImageView mLastMusic;
    private ImageView mPlayMusic;
    private ImageView mNextMusic;
    private ImageView mExitMusic;
    private TextView mTvPlayyingTime;
    private TextView mTvMusicTime;
    private TextView mMusicName;
    private ListView mListView;
    private SeekBar mSeekBar;
    private Context mContext;
    private Dialog mDialog;
    private File mFile;
    private boolean mIsOpen = false;
    private TextView mTvPlay;
    private MediaControl mControl;

    public MusicDialog(Context context, File file, int style) {
        mContext = context;
        mFile = file;
        mDialog = new Dialog(mContext, style);
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.gravity = Gravity.TOP;
        params.y = 90;
        mDialog.getWindow().setAttributes(params);
        mDialog.setContentView(R.layout.activity_music_layout);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mMusicLayout != null) {
                    SkinManager.getInstance().unRegisterView(mMusicLayout);
                }
            }
        });
        initView();
        initData();
        SkinManager.getInstance().registerView(mMusicLayout);
    }

    private void initView() {
        mMusicLayout = (LinearLayout) mDialog.findViewById(R.id.music_layout);
        mListView = (ListView) mDialog.findViewById(R.id.list_music_player);
        mOpenList = (LinearLayout) mDialog.findViewById(R.id.layout_open_music_list);
        mLastMusic = (ImageView) mDialog.findViewById(R.id.last_music);
        mPlayMusic = (ImageView) mDialog.findViewById(R.id.play_music);
        mNextMusic = (ImageView) mDialog.findViewById(R.id.next_music);
        mExitMusic = (ImageView) mDialog.findViewById(R.id.exit_music);
        mTvPlay = (TextView) mDialog.findViewById(R.id.text_play_music);
        mTvPlayyingTime = (TextView) mDialog.findViewById(R.id.text_playying_time);
        mTvMusicTime = (TextView) mDialog.findViewById(R.id.text_music_time);
        mSeekBar = (SeekBar) mDialog.findViewById(R.id.seekbar_music);
        mMusicName = (TextView) mDialog.findViewById(R.id.text_music_name);
        mOpenList.setOnClickListener(this);
        mLastMusic.setOnClickListener(this);
        mPlayMusic.setOnClickListener(this);
        mNextMusic.setOnClickListener(this);
        mExitMusic.setOnClickListener(this);
        mTvMusicTime.setOnClickListener(this);
        mTvPlayyingTime.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mControl.realease();
            }
        });
    }

    /**
     * 控制歌曲列表是否显示
     */
    public void makeListEnable() {
        if (mIsOpen) {
            mListView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.VISIBLE);
        }
        mIsOpen = !mIsOpen;
    }


    /***
     * 初始化数据
     */
    private void initData() {
        mControl = new MediaControl(this, mFile.getAbsolutePath().toString(), true);
        MediaAdapter adapter = new MediaAdapter(mContext, mControl.getAudioFileTextList(mFile.getAbsolutePath().toString()));
        mListView.setAdapter(adapter);
        mControl.initMedia(null);
        mControl.playOrPauseMedia();
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
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
                //Toast.makeText(mContext, "Toast", Toast.LENGTH_SHORT).show();
                makeListEnable();
                break;
            case R.id.exit_music:
                dismiss();
                break;
            default:
                break;
        }
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
        mTvPlayyingTime.setText("00:00");
        mSeekBar.setProgress(0);
        updateMediaState(true);
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
}
