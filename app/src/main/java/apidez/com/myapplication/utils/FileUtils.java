package apidez.com.myapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static apidez.com.myapplication.utils.Constant.APP_TAG;

/**
 * Created by nongdenchet on 11/5/16.
 */

public class FileUtils {

    public static MultipartBody.Part partFromFile(File file) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData("image", file.getName(), reqFile);
    }

    public static RequestBody requestBodyFromFile(File file) {
        return RequestBody.create(MediaType.parse("text/plain"), file.getName());
    }

    public static Uri getPhotoFileUri(Context context, String path) {
        File mediaStorageDir = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        return Uri.fromFile(new File(path));
    }

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static File createPhotoFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        return new File(storageDir.getPath() + File.separator + imageFileName);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void store(Bitmap resizedBitmap, String path) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File resizedFile = new File(path);
        resizedFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(resizedFile);
        fos.write(bytes.toByteArray());
        fos.close();
    }

    @SuppressWarnings("ConstantConditions")
    public static Bitmap rotateBitmapOrientation(String photoFilePath) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    }
}
