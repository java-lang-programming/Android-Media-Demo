package java_lang_programming.com.android_media_demo.article85;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import java_lang_programming.com.android_media_demo.ImageSelectionDemoActivity;
import java_lang_programming.com.android_media_demo.R;

/**
 * Screen for Exif
 */
public class ExifActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_CHOOSER = 101;

    /**
     * Id to identify a READ_EXTERNAL_STORAGE permission request.
     */
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 0;

    public static final List<String> types = Collections
            .unmodifiableList(new LinkedList<String>() {
                {
                    add("image/jpeg");
                    add("image/jpg");
                    add("image/png");
                }
            });

    /**
     * Permissions required to read external storage.
     */
    private static String[] PERMISSION_READ_EXTERNAL_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};

    private ImageView selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exif);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedImage = (ImageView) findViewById(R.id.selected_image);
        Button btnSelectImage = (Button) findViewById(R.id.btn_select_image);
        btnSelectImage.setOnClickListener(v -> {
            checkPermission();
        });

    }

    /**
     * Called when the '画像を選択する' button is clicked.
     */
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE permission has not been granted.
            requestExternalStoragePermission();
        } else {
            startExternalAppSelectableImage();
        }
    }

    /**
     * Requests the READ_EXTERNAL_STORAGE permission.
     * the permission is requested directly.
     */
    private void requestExternalStoragePermission() {
        // Contact permissions have not been granted yet. Request them directly.
        ActivityCompat.requestPermissions(this, PERMISSION_READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE);
    }

    /**
     * start ExternalApps if the required READ_EXTERNAL_STORAGE permission has been granted.
     */
    private void startExternalAppSelectableImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, types.toArray());
        }
        startActivityForResult(Intent.createChooser(intent, null), ImageSelectionDemoActivity.REQUEST_CODE_CHOOSER);
    }

    /**
     * Callback received when a permissions request has been completed.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case (REQUEST_READ_EXTERNAL_STORAGE):
                if (verifyPermissions(grantResults)) {
                    startExternalAppSelectableImage();
                } else {
                    Toast.makeText(this, getString(R.string.permissions_not_granted), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     */
    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_CHOOSER):
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, getString(R.string.image_unselected_message), Toast.LENGTH_LONG).show();
                    return;
                }
                Uri result = data.getData();
                Log.d("ExifActivity", result.getPath());
                // TODO
                // 画面回転用の処理(lifecycle components)も記載しておく。
                Bitmap bitmap = getBitmap(getApplicationContext(), result);
                if (bitmap != null) {
                    selectedImage.setImageBitmap(bitmap);
                }
                //getOrientation(result);
                //startCrop(data.getData());
                // selectedImage.setImageURI(data.getData());
                break;
            default:
                break;
        }
    }

    // https://github.com/Yalantis/uCrop/tree/master/ucrop/src/main/java/com/yalantis/ucrop/util
    private int getOrientation(@NonNull Uri uri) {
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);
            ExifInterface exifInterface = new ExifInterface(in);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Log.d("ExifActivity ", "orientation" + orientation);
            // Now you can extract any Exif tag you want
            // Assuming the image is a JPEG or supported raw format
        } catch (IOException e) {
            // Handle any errors
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        return orientation;
    }

    /**
     * getBitmapを取得する
     *
     * @param context
     * @param uri
     * @return
     */
    private
    @Nullable
    Bitmap getBitmap(@NonNull Context context, @NonNull Uri uri) {
        //　説明 1
        final ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.getStackTrace();
            return null;
        }

        // 説明2
        final FileDescriptor fileDescriptor;
        if (parcelFileDescriptor != null) {
            fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        } else {
            // ParcelFileDescriptor was null for given Uri: [" + mInputUri + "]"
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        // 説明3
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        if (options.outWidth == -1 || options.outHeight == -1) {
            // "Bounds for bitmap could not be retrieved from the Uri: [" + mInputUri + "]"
            return null;
        }

        Log.d("ExifActivity", "outWidth : " + options.outWidth);
        Log.d("ExifActivity", "outHeight : " + options.outHeight);

        // 画面サイズと画像サイズを比較
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Log.d("ExifActivity", "width : " + width);
        Log.d("ExifActivity", "height : " + height);

        int reqWidth = Math.min(width, options.outWidth);
        int reqHeight = Math.min(height, options.outHeight);

        Log.d("ExifActivity", "reqWidth : " + reqWidth);
        Log.d("ExifActivity", "reqHeight : " + reqHeight);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

        Log.d("ExifActivity", "inSampleSize : " + options.inSampleSize);

        Bitmap decodeSampledBitmap = null;

        boolean decodeAttemptSuccess = false;
        while (!decodeAttemptSuccess) {
            try {
                decodeSampledBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                decodeAttemptSuccess = true;
            } catch (OutOfMemoryError error) {
                Log.e("", "doInBackground: BitmapFactory.decodeFileDescriptor: ", error);
                options.inSampleSize *= 2;
            }
        }

        Log.d("ExifActivity", "decodeSampledBitmap : getWidth " + decodeSampledBitmap.getWidth());
        Log.d("ExifActivity", "decodeSampledBitmap : getHeight "  + decodeSampledBitmap.getHeight());

        // TODO oritantation

        return decodeSampledBitmap;
    }

    /// resize https://github.com/Yalantis/uCrop/blob/master/ucrop/src/main/java/com/yalantis/ucrop/task/BitmapCropTask.java

    // max bitmap size https://github.com/Yalantis/uCrop/blob/master/ucrop/src/main/java/com/yalantis/ucrop/util/BitmapLoadUtils.java

    public int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width lower or equal to the requested height and width.
            while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


//    private int rotation() {
//
//    }

    // bitmapを
    // https://github.com/Yalantis/uCrop/tree/master/ucrop/src/main/java/com/yalantis/ucrop/model
    // https://github.com/Yalantis/uCrop/blob/master/ucrop/src/main/java/com/yalantis/ucrop/task/BitmapLoadTask.java
//    private Bitmap getBitmap(@NonNull Uri uri) {
//        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = false;
//        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
//        InputStream in = null;
//        try {
//            in = getContentResolver().openInputStream(uri);
//            BitmapFactory.decodeFile(in, options);
//            // BitmapFactory aa = BitmapFactory.decodeStream(in);
//            ExifInterface exifInterface = new ExifInterface(in);
//            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//            Log.d("ExifActivity ", "orientation" + orientation);
//            // Now you can extract any Exif tag you want
//            // Assuming the image is a JPEG or supported raw format
//        } catch (IOException e) {
//            // Handle any errors
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException ignored) {
//                }
//            }
//        }
//
//        return null;
//    }


    private void getData(@NonNull Uri uri) {
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);
            ExifInterface exifInterface = new ExifInterface(in);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Log.d("ExifActivity ", "orientation" + orientation);
            // Now you can extract any Exif tag you want
            // Assuming the image is a JPEG or supported raw format
        } catch (IOException e) {
            // Handle any errors
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

}
