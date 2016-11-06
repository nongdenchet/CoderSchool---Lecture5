package apidez.com.myapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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
    private ImageView ivPreview;
    private String mCurrentPhotoPath;

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

    private void openCamera(int requestCode) {
        if (PermissionUtils.checkExternal(this)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = FileUtils.createPhotoFile(this);
            mCurrentPhotoPath = file.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, requestCode);
            }
        } else {
            PermissionUtils.requestExternal(this);
        }
    }

    private void takeImage() {
        Bitmap rotatedBitmap = FileUtils.rotateBitmapOrientation(mCurrentPhotoPath);
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rotatedBitmap, 500);
        try {
            FileUtils.store(resizedBitmap, mCurrentPhotoPath);
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        ivPreview.setImageBitmap(resizedBitmap);
    }

    private void uploadImage() {
        File file = new File(mCurrentPhotoPath);
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
