package apidez.com.myapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import apidez.com.myapplication.R;
import apidez.com.myapplication.utils.BitmapScaler;
import apidez.com.myapplication.utils.FileUtils;
import apidez.com.myapplication.utils.PermissionUtils;

public class CameraActivity extends AppCompatActivity {
    public final String APP_TAG = "CoderSchool";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    private ImageView ivPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        PermissionUtils.requestCamera(this);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
    }

    public void openCamera(View view) {
        if (PermissionUtils.checkCamera(this)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        } else {
            PermissionUtils.requestCamera(this);
        }
    }

    public Uri getPhotoFileUri(String fileName) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(APP_TAG, "failed to create directory");
            }
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri takenPhotoUri = getPhotoFileUri(photoFileName);
            Bitmap rotatedBitmap = FileUtils.rotateBitmapOrientation(takenPhotoUri.getPath());
            Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rotatedBitmap, 500);
            try {
                FileUtils.store(resizedBitmap, getPhotoFileUri(photoFileName + "_resized"));
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            ivPreview.setImageBitmap(resizedBitmap);
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
