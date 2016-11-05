package apidez.com.myapplication.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import apidez.com.myapplication.R;

public class AudioActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private boolean donePrepare;
    private String url = "https://dl.dropboxusercontent.com/u/10281242/sample_audio.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        setupMedia();
    }

    private void setupMedia() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                donePrepare = true;
                play();
            }
        });
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    public void onPlayClick(View view) {
        play();
    }

    public void play() {
        if (donePrepare) {
            mediaPlayer.start();
        } else {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        }
    }
}
