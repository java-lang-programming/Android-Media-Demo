/**
 * Copyright (C) 2017 Programming Java Android Development Project
 * Programming Java is
 * <p>
 * http://java-lang-programming.com/ja/articles/94
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java_lang_programming.com.android_media_demo.article94.java;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
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
 * Activity for ImageDecoder Sample
 */
public class ImageDecoderActivity extends AppCompatActivity {

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
    private TextView selectedImageInfo;
    private CheckBox checkedResize;
    private CheckBox checkedCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_decoder);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedImage = findViewById(R.id.selected_image);
        selectedImageInfo = findViewById(R.id.selected_image_info);
        checkedResize = findViewById(R.id.ck_resize);
        checkedCrop = findViewById(R.id.ck_crop);
        Button btnSelectImage = findViewById(R.id.btn_select_image);
        btnSelectImage.setOnClickListener(v ->
                checkPermission()
        );
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                if (resultCode != RESULT_OK || data.getData() == null) {
                    Toast.makeText(this, getString(R.string.image_unselected_message), Toast.LENGTH_LONG).show();
                    return;
                }
                Bitmap bitmap = null;
                // P 以降
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1) {
                    Log.d("ImageDecoderActivity", "ImageDecoder");
                    StringBuilder msg = new StringBuilder();
                    try {
//                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), data.getData()));
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), data.getData()),
                                new ImageDecoder.OnHeaderDecodedListener() {
                                    @Override
                                    public void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                                        if (checkedResize.isChecked()) {
                                            Size size = imageInfo.getSize();
                                            imageDecoder.setResize(size.getWidth() * 2, size.getHeight() * 2);
                                        }

                                        if (checkedCrop.isChecked()) {
                                            Size size = imageInfo.getSize();
                                            imageDecoder.setCrop(new Rect(0, 0, size.getWidth(), size.getHeight() / 2));
                                        }

                                        msg.append("ImageDecoderでbitmapに変換しました。\n");
                                        msg.append("画像サイズ : " + imageInfo.getSize() + "\n");
                                        msg.append("画像種別 : " + imageInfo.getMimeType() + "\n");
                                        msg.append("アニメーション : " + imageInfo.isAnimated() + "\n");
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    selectedImageInfo.setText(msg.toString());
                    // 既存処理
                } else {
                    Log.d("ImageDecoderActivity", "BitmapFactory");
                    bitmap = getBitmap(getApplicationContext(), data.getData());
                    selectedImageInfo.setText("BitmapFactoryでbitmapに変換しました。");
                }

                if (bitmap != null) {
                    selectedImage.setImageBitmap(bitmap);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 画像の向きを取得する
     *
     * @param uri 画像Uri
     * @return 画像の向き
     */
    private int getOrientation(@NonNull Uri uri) {
        int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);
            if (in == null) return orientation;
            ExifInterface exifInterface = new ExifInterface(in);
            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            e.getStackTrace();
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
     * 画像を回す角度を取得する
     *
     * @param exifOrientation 向き
     * @return 画像を回す角度
     */
    public int getRotation(int exifOrientation) {
        int rotation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_TRANSPOSE:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                rotation = 270;
                break;
            default:
                rotation = 0;
        }
        return rotation;
    }

    /**
     * Bitmapを取得する
     *
     * @param context コンテキスト
     * @param uri     画像Uri
     * @return Bitmap
     */
    private
    @Nullable
    Bitmap getBitmap(@NonNull Context context, @NonNull Uri uri) {
        final ParcelFileDescriptor parcelFileDescriptor;
        try {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        } catch (FileNotFoundException e) {
            e.getStackTrace();
            return null;
        }

        final FileDescriptor fileDescriptor;
        if (parcelFileDescriptor != null) {
            fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        } else {
            // ParcelFileDescriptor was null for given Uri: [" + mInputUri + "]"
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        if (options.outWidth == -1 || options.outHeight == -1) {
            // "Bounds for bitmap could not be retrieved from the Uri: [" + mInputUri + "]"
            return null;
        }

        int orientation = getOrientation(uri);
        int rotation = getRotation(orientation);

        Matrix transformMatrix = new Matrix();
        transformMatrix.setRotate(rotation);

        // 画面サイズと画像サイズを比較
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int reqWidth = Math.min(width, options.outWidth);
        int reqHeight = Math.min(height, options.outHeight);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;

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

        // 画像を変形する
        decodeSampledBitmap = transformBitmap(decodeSampledBitmap, transformMatrix);

        return decodeSampledBitmap;
    }

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

    public Bitmap transformBitmap(@NonNull Bitmap bitmap, @NonNull Matrix transformMatrix) {
        try {
            Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), transformMatrix, true);
            if (!bitmap.sameAs(converted)) {
                bitmap = converted;
            }
        } catch (OutOfMemoryError error) {
            Log.e("", "transformBitmap: ", error);
        }
        return bitmap;
    }
}
