/**
 * Copyright (C) 2017 Programming Java Android Development Project
 * Programming Java is
 * <p>
 * http://java-lang-programming.com/ja/articles/80
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

package java_lang_programming.com.android_media_demo.article80;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java_lang_programming.com.android_media_demo.R;

/**
 * Screen for Image
 */
public class ImageFragment extends Fragment {
    public static final String TAG = "ImageFragment";
    public static final int REQUEST_CODE_IMAGE_FILTER_DIALOG = 100;

    private ImageView imageView;

    private OnFragmentInteractionListener mListener;

    public static ImageFragment newInstance() {
        ImageFragment fragment = new ImageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setOnClickListener(image -> {
            openDialog();
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Open ImageFilterDialogFragment
     */
    private void openDialog() {
        ImageFilterDialogFragment imageFilterDialogFragment = ImageFilterDialogFragment.newInstance();
        imageFilterDialogFragment.setTargetFragment(this, ImageFragment.REQUEST_CODE_IMAGE_FILTER_DIALOG);
        imageFilterDialogFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_FILTER_DIALOG:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }

                int selectedIndex = data.getIntExtra(ImageFilterDialogFragment.SELECTED_FILTER_NAME, -1);
                switch (selectedIndex) {
                    case 0:
                        grayScale();
                        break;
                    case 1:
                        grayScaleSimpleMeanMethod();
                        break;
                    case 2:
                        grayScaleNTSCCoefMethod();
                        break;
                    case 3:
                        grayScaleClear();
                        break;
                    default:
                        break;
                }

                String[] imageFiltersName = getResources().getStringArray(R.array.image_filters_name);
                Toast.makeText(getActivity().getApplicationContext(),
                        "requestCode : " + requestCode + ", resultCode : " + resultCode + " " +
                                getString(R.string.image_filter_selected_msg, imageFiltersName[data.getIntExtra(ImageFilterDialogFragment.SELECTED_FILTER_NAME, 0)]),
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * グレースケール
     * grayscale
     */
    private void grayScale() {
        long start = System.currentTimeMillis();
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        long end = System.currentTimeMillis();
        imageView.setColorFilter(filter);
        Log.d(TAG, ((end - start) + "ms"));
    }

    /**
     * Bitmapを使ったグレースケール
     * 単純平均法
     * grayscale using Bitmap
     * SimpleMeanMethod
     */
    private void grayScaleSimpleMeanMethod() {
        long start = System.currentTimeMillis();
        Bitmap mutableBitmap = getMutableBitmap();

        int width = mutableBitmap.getWidth();
        int height = mutableBitmap.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                // Returns the Color at the specified location.
                int pixel = mutableBitmap.getPixel(i, j);

                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                int average = (red + green + blue) / 3;
                int gray_rgb = Color.rgb(average, average, average);

                mutableBitmap.setPixel(i, j, gray_rgb);
            }
        }
        imageView.setImageBitmap(mutableBitmap);
        long end = System.currentTimeMillis();
        Log.d(TAG, ((end - start) + "ms"));
    }

    /**
     * Bitmapを使ったグレースケール
     * NTSC 係数による加重平均法
     * grayscale using Bitmap
     * NTSC Coef. method
     */
    private void grayScaleNTSCCoefMethod() {
        long start = System.currentTimeMillis();
        Bitmap mutableBitmap = getMutableBitmap();

        int width = mutableBitmap.getWidth();
        int height = mutableBitmap.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {

                // Returns the Color at the specified location.
                int pixel = mutableBitmap.getPixel(i, j);

                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int blue = Color.blue(pixel);

                // RGB 値をグレースケール値に変換
                double gray_scale_value = 0.2989 * red + 0.5870 * green + 0.1140 * blue;
                int gray_scale = (int) gray_scale_value;

                int gray_rgb = Color.rgb(gray_scale, gray_scale, gray_scale);

                mutableBitmap.setPixel(i, j, gray_rgb);
            }
        }
        imageView.setImageBitmap(mutableBitmap);
        long end = System.currentTimeMillis();
        Log.d(TAG, ((end - start) + "ms"));
    }

    /**
     * グレースケールクリア
     */
    private void grayScaleClear() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.reset();
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
        imageView.setImageResource(R.drawable.sample_20161204_180343);
    }

    /**
     * 変更可能なbitmapを返す
     * Returns changeable bmp
     *
     * @return
     */
    private Bitmap getMutableBitmap() {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        // 変更可能なbitmap
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return mutableBitmap;
    }

    public interface OnFragmentInteractionListener {
    }
}
