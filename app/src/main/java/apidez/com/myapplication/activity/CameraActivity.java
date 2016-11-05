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

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import apidez.com.myapplication.R;
import apidez.com.myapplication.api.ImgurApi;
import apidez.com.myapplication.model.ImageResponse;
import apidez.com.myapplication.utils.BitmapScaler;
import apidez.com.myapplication.utils.FileUtils;
import apidez.com.myapplication.utils.PermissionUtils;
import apidez.com.myapplication.utils.RetrofitUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends AppCompatActivity {
    public final String APP_TAG = "CoderSchool";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1000;
    public final static int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 2000;
    public String photoFileName = "photo.jpg";
    private ImageView ivPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        PermissionUtils.requestExternal(this);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
    }

    public void openCamera(View view) {
        openCamera(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void upload(View view) {
        openCamera(UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE);
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
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                takeImage();
            } else if (requestCode == UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE) {
                uploadImage();
            }
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private void openCamera(int requestCode) {
        if (PermissionUtils.checkExternal(this)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, requestCode);
            }
        } else {
            PermissionUtils.requestExternal(this);
        }
    }

    private void takeImage() {
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

    private void uploadImage() {
        File file = new File(getPhotoFileUri(photoFileName).getPath());
        RetrofitUtils.get(getString(R.string.IMGUR_CLIENT_ID))
                .create(ImgurApi.class)
                .create(FileUtils.partFromFile(file), FileUtils.requestBodyFromFile(file))
                .enqueue(new Callback<ImageResponse>() {
                    @Override
                    public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                        ImageResponse imageResponse = response.body();
                        Glide.with(CameraActivity.this)
                                .load(imageResponse.getData().getLink())
                                .into(ivPreview);
                    }

                    @Override
                    public void onFailure(Call<ImageResponse> call, Throwable t) {
                        Toast.makeText(CameraActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
