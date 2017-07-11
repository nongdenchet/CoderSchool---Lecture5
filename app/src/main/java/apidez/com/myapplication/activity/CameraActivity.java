package apidez.com.myapplication.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.storage.StorageReference;

import apidez.com.myapplication.R;

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
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        // TODO: require permission
        // TODO: setup storage
    }

    public void openCamera(View view) {
        // TODO: open camera
    }

    public void upload(View view) {
        // TODO: open camera for upload
    }
}
