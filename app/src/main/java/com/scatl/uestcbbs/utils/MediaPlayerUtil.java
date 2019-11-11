package com.scatl.uestcbbs.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * author: sca_tl
 * description:
 * date: 2019/8/25 16:03
 */
public class MediaPlayerUtil {

    private static volatile MediaPlayerUtil mediaPlayer;
    private MediaPlayer mPlayer;
    public static boolean pause = false;
    private static String current_url = "What a coincidence, you catch me!"; // 当前播放的音频地址

    private MediaPlayerUtil() { }

    public static MediaPlayerUtil getMediaPlayer() {
        if (mediaPlayer == null) {
            synchronized (MediaPlayerUtil.class) {
                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayerUtil();
                }
            }
        }
        return mediaPlayer;
    }


    public void playOrPause(String url) {

        if (url.equals(current_url)) {  //url相同，判断是否是暂停状态
            if (pause) {
                mPlayer.start();
                pause = false;
            } else {
                mPlayer.pause();
                pause = true;
            }
            return;
        }
        stopPlay();
        current_url = url;

        if (mPlayer == null) { mPlayer = new MediaPlayer(); }
        try {

            mPlayer.reset();
            mPlayer.setDataSource(url); //====这种方式只能http，https会抛IO异常
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    current_url = "What a coincidence, you catch me!";
                    if (mPlayer != null) {
                        mPlayer.release();
                        mPlayer = null;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 停止播放音频，lastSongUrl置空
     */
    public void stopPlay() {
        current_url = "What a coincidence, you catch me!";
        try {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

}
