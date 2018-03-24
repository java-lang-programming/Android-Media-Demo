package java_lang_programming.com.android_media_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java_lang_programming.com.android_media_demo.article80.GrayScaleActivity;
import java_lang_programming.com.android_media_demo.article84.ExifActivity;
import java_lang_programming.com.android_media_demo.article94.java.ImageDecoderActivity;

/**
 * Main
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btnImageSelectionDemo = (Button) findViewById(R.id.btn_image_selection_demo);
        btnImageSelectionDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveImageSelectionDemoActivity();
            }
        });

        Button btnImageSelectionCropDemo = (Button) findViewById(R.id.btn_image_selection_crop_demo);
        btnImageSelectionCropDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveImageSelectionCropDemo();
            }
        });

        Button btnGrayScaleDemo = (Button) findViewById(R.id.btn_gray_scale_demo);
        btnGrayScaleDemo.setOnClickListener(v -> moveGrayScaleActivity());

        Button btnExifDemo = (Button) findViewById(R.id.btn_exif_demo);
        btnExifDemo.setOnClickListener(v -> moveExifActivity());

        // ImageDecoderActivity

//        Button btnExo2Demo = findViewById(R.id.btn_exo2_demo_java);
//        btnExo2Demo.setOnClickListener(v -> moveExoplayer2Activity());

        Button btnImageDecoderDemo = findViewById(R.id.btn_image_decoder_demo);
        btnImageDecoderDemo.setOnClickListener(v -> moveImageDecoderActivity());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void moveImageSelectionDemoActivity() {
        Intent intent = new Intent(this, ImageSelectionDemoActivity.class);
        startActivity(intent);
    }

    private void moveImageSelectionCropDemo() {
        Intent intent = new Intent(this, ImageSelectionCropDemo.class);
        startActivity(intent);
    }

    private void moveGrayScaleActivity() {
        Intent intent = new Intent(this, GrayScaleActivity.class);
        startActivity(intent);
    }

    private void moveExifActivity() {
        Intent intent = new Intent(this, ExifActivity.class);
        startActivity(intent);
    }

//    private void moveExoplayer2Activity() {
//        Intent intent = new Intent(this, Exoplayer2Activity.class);
//        startActivity(intent);
//    }
//
//    private void moveKtExoplayer2Activity() {
//        Intent intent = new Intent(this, KtExoplayer2Activity.class);
//        startActivity(intent);
//    }

    private void moveImageDecoderActivity() {
        Intent intent = new Intent(this, ImageDecoderActivity.class);
        startActivity(intent);
    }
}
