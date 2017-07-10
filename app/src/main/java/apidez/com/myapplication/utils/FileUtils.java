package apidez.com.myapplication.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.google.android.exoplayer2.util.Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 Created by nongdenchet on 11/5/16.
 */
public class FileUtils {

    public static Uri fromFile(Context context, String path) {
        return fromFile(context, new File(path));
    }

    public static Uri fromFile(Context context, File file) {
        if (Util.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context, "com.example.fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static File createPhotoFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotationAngle = 90;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotationAngle = 180;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotationAngle = 270;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        return Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
    }
}
