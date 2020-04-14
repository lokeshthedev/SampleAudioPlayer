package com.example.sampleaudioplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import static android.util.Log.v;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;

    private AudioManager mAudioManager;

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focus) {
            if (focus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focus == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                v("AudioFocusListener","Loss Transient");
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            }
            else if(focus == AudioManager.AUDIOFOCUS_GAIN){
                mMediaPlayer.start();
                v("AudioFocusListener","GAIN");
            }
            else if(focus == AudioManager.AUDIOFOCUS_LOSS){
                mMediaPlayer.stop();
                v("AudioFocusListener","LOSS");
                releaseResources();
            }

        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            v("CompletedSong","Song is completed");
            releaseResources();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaPlayer = MediaPlayer.create(this, R.raw.song);

    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseResources();
        v("StoppingActivity","onStop function is called");
    }

    public void playSong(View v){

        int result = mAudioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if(mMediaPlayer == null){
            mMediaPlayer = MediaPlayer.create(this, R.raw.song);
        }

        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            Log.v("mProgressValue",String.valueOf(mMediaPlayer.getCurrentPosition()));
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
        }
    }

    public void pauseSong(View v){
        mMediaPlayer.pause();
        Log.i("PausingSong",String.valueOf(mMediaPlayer.getCurrentPosition()));
    }

    public void stopSong(View v){
        mMediaPlayer.stop();
        v("StoppingSong",String.valueOf(mMediaPlayer.getCurrentPosition()));
        releaseResources();
    }

    public void rewind(View v){
        if(mMediaPlayer != null){
            v("Rewind",String.valueOf(mMediaPlayer.getCurrentPosition()));
            int position = mMediaPlayer.getCurrentPosition()-5000;
            Log.v("Rewind2",String.valueOf(position));
            if(position < 0){
                mMediaPlayer.seekTo(0);
            }
            else {
                mMediaPlayer.seekTo(position);
            }
        }
    }

    public void forward(View v){
        if(mMediaPlayer != null){
            Log.v("Forward",String.valueOf(mMediaPlayer.getCurrentPosition()));
            int position = mMediaPlayer.getCurrentPosition()+5000;
            Log.v("Forward2",String.valueOf(position));
            if(position > mMediaPlayer.getDuration()){
                mMediaPlayer.seekTo(mMediaPlayer.getDuration());
            }
            else {
                mMediaPlayer.seekTo(position);
            }
        }
    }


    public void releaseResources(){
        if(mMediaPlayer != null){
            v("Logging","Releasing resources");
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }
}
