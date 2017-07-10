package apidez.com.myapplication.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import apidez.com.myapplication.R;
import apidez.com.myapplication.utils.BitmapScaler;
import apidez.com.myapplication.utils.FileUtils;
import apidez.com.myapplication.utils.PermissionUtils;

public class CameraActivity extends AppCompatActivity {
    public final String APP_TAG = "CoderSchool";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1000;
    public final static int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 2000;
    private ImageView ivPreview;
    private String mCurrentPhotoPath;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        PermissionUtils.requestExternal(this);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        mStorageReference = FirebaseStorage.getInstance().getReference();
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileUtils.fromFile(this, file));
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

    @SuppressWarnings("VisibleForTests")
    private void uploadImage() {
        Uri file = FileUtils.fromFile(this, mCurrentPhotoPath);
        mStorageReference.child("images/" + file.getLastPathSegment())
                .putFile(file)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CameraActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Glide.with(CameraActivity.this)
                                .load(taskSnapshot.getDownloadUrl())
                                .into(ivPreview);
                    }
                });
    }
}
