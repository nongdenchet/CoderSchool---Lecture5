package apidez.com.myapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import apidez.com.myapplication.R;

public class PlayerActivity extends AppCompatActivity {
    private SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;
    private boolean playWhenReady;
    private long playbackPosition;
    private int currentWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playerView = (SimpleExoPlayerView) findViewById(R.id.video_view);
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void initializePlayer() {
        if (player == null) {
            // TODO: init player
            // TODO: set player for view
        }

        // TODO: resume player
        // TODO: prepare player
    }
}
