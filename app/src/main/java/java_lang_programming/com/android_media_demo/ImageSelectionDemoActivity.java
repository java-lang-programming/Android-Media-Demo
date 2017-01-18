package java_lang_programming.com.android_media_demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 画像選択
 */
public class ImageSelectionDemoActivity extends AppCompatActivity {

    public final static int REQUEST_CODE_CHOOSER = 101;

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
        setContentView(R.layout.activity_image_selection_demo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        selectedImage = (ImageView) findViewById(R.id.selected_image);
        Button btnSelectImage = (Button) findViewById(R.id.btn_select_image);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
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
        ActivityCompat.requestPermissions(this, PERMISSION_READ_EXTERNAL_STORAGE, ImageSelectionDemoActivity.REQUEST_READ_EXTERNAL_STORAGE);
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
            case (ImageSelectionDemoActivity.REQUEST_READ_EXTERNAL_STORAGE):
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ImageSelectionDemoActivity.REQUEST_CODE_CHOOSER):
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, getString(R.string.image_unselected_message), Toast.LENGTH_LONG).show();
                    return;
                }
                Uri result = data.getData();
                selectedImage.setImageURI(result);
            default:
                break;
        }
    }
}
