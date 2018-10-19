package com.hht.filebrowser.media.presenter;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.hht.filebrowser.files.FileText;
import com.hht.filebrowser.files.FileTextManager;
import com.hht.filebrowser.media.VideoActivity;
import com.hht.filebrowser.media.callback.IMedia;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by admin on 2017/4/18.
 */

public class MediaControl implements IMedia.iMediaControl {
    private List<String> mPathList = new ArrayList<String>();
    private MediaPlayer mMediaPlayer;
    private IMedia.iMediaView mView;
    private int mCurrentPosition;
    private boolean mIsRunning;

    /**
     * 初始化MediaPlayer对象并获取当前文件列表
     *
     * @param view
     */
    public MediaControl(IMedia.iMediaView view, String path, boolean isMusic) {
        mMediaPlayer = new MediaPlayer();
        mView = view;
        List<String> mFileTexts = null;
        if (isMusic) {
            mFileTexts = getAudioFileTextList(path);
        } else {
            mFileTexts = getVideoFileTextList(path);
        }
        mCurrentPosition = mFileTexts.indexOf(path);
    }

    public List<String> getVideoFileTextList(String path) {
        if (mPathList != null && mPathList.size() > 0) {
            mPathList.removeAll(mPathList);
        }

        List<FileText> mFileTexts = FileTextManager.getInstance().getFileTextList();
        for (FileText fileText : mFileTexts) {
            if ("file_icon_video".equals(fileText.getIconTag())) {
                mPathList.add(fileText.getFilePath());
            }
        }
        mCurrentPosition = mPathList.indexOf(path);
        return mPathList;
    }

    public List<String> getAudioFileTextList(String path) {
        if (mPathList != null && mPathList.size() > 0) {
            mPathList.removeAll(mPathList);
        }
        List<FileText> mFileTexts = FileTextManager.getInstance().getFileTextList();
        for (FileText fileText : mFileTexts) {
            if ("file_icon_audio".equals(fileText.getIconTag())) {
                mPathList.add(fileText.getFilePath());
            }
        }
        mCurrentPosition = mPathList.indexOf(path);
        return mPathList;
    }

    public List<String> getPathList() {
        return mPathList;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mView.updateSeekbar(msg.what);
        }
    };
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if (mMediaPlayer != null) {
                int position = mMediaPlayer.getCurrentPosition();
                mHandler.sendEmptyMessage(position);
                mHandler.postDelayed(mRunnable, 100);
            }
        }
    };

    @Override
    public void seekMedia(int position) {
        mMediaPlayer.seekTo(position);
        mHandler.post(mRunnable);
    }

    public interface VideoLister {
        void videoCallBack(int what);
    }

    VideoLister mVideoLister;

    public void setCallBackListener(VideoLister listener) {
        mVideoLister = listener;
    }

    @Override
    public void initMedia(SurfaceHolder holder) {
        try {
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mPathList.get(mCurrentPosition));
            if (holder != null) {
                mMediaPlayer.setDisplay(holder);
            }
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new PrepareListener());
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mIsRunning = false;
                    mp.seekTo(0);
                    removeHandlerCallback();
                    mView.resetView();
                }
            });
            mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (mVideoLister != null) {
                        mVideoLister.videoCallBack(what);
                    }
                    return false;
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void switchMedia(SurfaceHolder holder) {
        mIsRunning = false;
        initMedia(holder);
        playOrPauseMedia();
    }

    @Override
    public void playOrPauseMedia() {
        if (mIsRunning) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mView.updateMediaState(true);
            }
        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.start();
                mHandler.post(mRunnable);
                mView.updateMediaState(false);
            }
        }
        mIsRunning = !mIsRunning;
    }

    @Override
    public void realease() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void lastMedia(SurfaceHolder holder) {
        if (mCurrentPosition > 0) {
            mCurrentPosition--;
            switchMedia(holder);
        }
    }

    @Override
    public void nextMedia(SurfaceHolder holder) {
        if (mCurrentPosition < mPathList.size() - 1) {
            mCurrentPosition++;
            switchMedia(holder);
        }
    }

    @Override
    public void removeHandlerCallback() {
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void swichMediaByPositon(SurfaceHolder holder, int position) {
        mCurrentPosition = position;
        switchMedia(holder);
    }

    private String getVideoName(int position) {
        File file = new File(mPathList.get(position));
        return file.getName();
    }

    private String getCurrentTime(int time) {
        SimpleDateFormat format = null;
        Date date = new Date(time);
        if (time > 1000 * 60 * 60) {
            format = new SimpleDateFormat("HH:mm:ss");
        } else {
            format = new SimpleDateFormat("mm:ss");
        }
        format.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        return format.format(date);
    }

    class PrepareListener implements MediaPlayer.OnPreparedListener {
        public void onPrepared(MediaPlayer mp) {
            mIsRunning = true;
            mMediaPlayer.start();
            mHandler.post(mRunnable);
            mView.initSeekBar(mMediaPlayer.getDuration(), getVideoName(mCurrentPosition), getCurrentTime(mMediaPlayer.getDuration()));
        }
    }
}
