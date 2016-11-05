package apidez.com.myapplication.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import apidez.com.myapplication.R;
import apidez.com.myapplication.utils.PermissionUtils;

public class VideoActivity extends AppCompatActivity {
    private static final int VIDEO_CAPTURE = 0;
    private VideoView videoView;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        PermissionUtils.requestExternal(this);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
    }

    public void onPlaySampleClick(View view) {
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.small));
        videoView.requestFocus();
        videoView.start();
    }

    public void onStreamClick(View view) {
        videoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
    }

    public void onCapture(View view) {
        if (PermissionUtils.checkExternal(this)) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                File mediaFile = new File(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
                videoUri = Uri.fromFile(mediaFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                startActivityForResult(intent, VIDEO_CAPTURE);
            } else {
                Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == VIDEO_CAPTURE) {
            Toast.makeText(this, "Video has been saved", Toast.LENGTH_LONG).show();
            playbackRecordedVideo();
        }
    }

    private void playbackRecordedVideo() {
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.start();
    }
}
