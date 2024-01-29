package com.yyc.ams.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.yyc.ams.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MusicUtils {

    public static Context context;

    private static boolean shouldPlayBeep;
    private static boolean playBeep = true;
    private static MediaPlayer mediaPlayer;
    private static float speechRate = 1.0f; // 默认语速为正常速度
    private static int playCount = 1; // 默认播放次数为1次

    public static MediaPlayer init(Activity activity){
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            shouldPlayBeep = false;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(player -> player.seekTo(0));
        AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.beep);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            mediaPlayer = null;
        }
        return mediaPlayer;
    }

    public static void play(){
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speechRate));
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(player -> {
                if (--playCount > 0) {
                    player.seekTo(0);
                    player.start();
                }
            });
        }
    }

    public static void stop(){
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public static void clear(){
        if (mediaPlayer != null){
            mediaPlayer = null;
        }
    }

    public static void setSpeechRate(float rate) {
        speechRate = rate;
    }

    public static void setPlayCount(int count) {
        playCount = count;
    }
}
