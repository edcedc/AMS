package com.yyc.ams.keyctrl;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MyPlaySound {
    private AudioTrack audioTrack;
    private int Hz, waveLen, length;
    private byte[] wave;

    /**
     * 设置频率
     *
     * @param rate
     */
    public MyPlaySound(int rate) {
        if (rate > 0) {
            Hz = rate;
            waveLen = 11100 / Hz;
            length = waveLen * Hz;
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                    AudioFormat.CHANNEL_OUT_STEREO, // CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_8BIT, length, AudioTrack.MODE_STREAM);
            //生成正弦波
            wave = SinWave.sin(wave, waveLen, length);
        } else {
            return;
        }
    }

    public void play() {
        audioTrack.write(wave, 0, length);
        if (audioTrack != null) {
            audioTrack.play();
        }
    }
    public void stop(){
        if (audioTrack != null) {
            audioTrack.stop();
        }
    }
}

