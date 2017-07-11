package apidez.com.myapplication.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by nongdenchet on 11/4/16.
 */

public class PermissionUtils {
    public static final int REQUEST_LOCATION = 1000;
    public static final int REQUEST_CAMERA = 2000;

    public static void requestExternal(Activity context) {
        if (!checkExternal(context)) {
            ActivityCompat.requestPermissions(context, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CAMERA);
        }
    }

    public static boolean checkExternal(Context context) {
        return !(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED);
    }
}
