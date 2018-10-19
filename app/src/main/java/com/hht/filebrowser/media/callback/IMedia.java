package com.hht.filebrowser.media.callback;

import android.view.SurfaceHolder;

/**
 * Created by admin on 2017/4/18.
 */

public interface IMedia {
    /**
     * 更新View的接口
     */
    interface iMediaView {
        /**
         * 更新View播放状态
         *
         * @param state
         */
        void updateMediaState(boolean state);

        /**
         * 更新进度条
         *
         * @param position
         */
        void updateSeekbar(int position);

        /**
         * 初始化媒体，设置进度条最大值，当前播放媒体名字，媒体的时长
         *
         * @param max
         * @param mediaName
         * @param mediaTime
         */
        void initSeekBar(int max, String mediaName, String mediaTime);


        void resetView();
    }

    /**
     * 控制播放的接口
     */
    interface iMediaControl {
        /**
         * 滑动SeekBar调节媒体播放进度
         *
         * @param position
         */
        void seekMedia(int position);

        /**
         * 初始化媒体资源
         *
         * @param holder
         */
        void initMedia(SurfaceHolder holder);

        /**
         * 切换媒体资源
         *
         * @param holder
         */
        void switchMedia(SurfaceHolder holder);

        /**
         * 切换播放状态
         */
        void playOrPauseMedia();

        /**
         * 播放结束释放资源
         */
        void realease();

        /**
         * 上一首
         *
         * @param holder
         */
        void lastMedia(SurfaceHolder holder);

        /**
         * 下一首
         *
         * @param holder
         */
        void nextMedia(SurfaceHolder holder);

        /**
         * remove callback 进度条不再更新
         */
        void removeHandlerCallback();

        /**
         * 根据position切换媒体
         *
         * @param holder
         * @param position
         */
        void swichMediaByPositon(SurfaceHolder holder, int position);
    }
}
