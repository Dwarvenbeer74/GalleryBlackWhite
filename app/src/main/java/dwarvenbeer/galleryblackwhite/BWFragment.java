package dwarvenbeer.galleryblackwhite;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class BWFragment extends Fragment implements View.OnClickListener{
    static final int GALLERY_REQUEST = 1;
    static final String LOG_TAG = "GalleryBlackWhite";

    private Bitmap mBitmap;
    private ImageView mImageView;
    private Button btnPick, btnBw;
    private SeekBar mSeekBar;
    private Uri selectedImage;

    public static BWFragment newInstance() {
        return new BWFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        mImageView = (ImageView)view.findViewById(R.id.imageView);
        Log.d(LOG_TAG, "mImageView found");
        btnPick = (Button)view.findViewById(R.id.btn_pick);
        btnPick.setOnClickListener(this);
        btnBw = (Button)view.findViewById(R.id.btn_bw);
        btnBw.setOnClickListener(this);
        mSeekBar = (SeekBar)view.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.setProgress(256);

        return view;
    }

    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadBitmapSat();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pick:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                break;
            case R.id.btn_bw:
                loadBitmapSat();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        mBitmap = bitmap;
                        loadBitmapSat();
                    } catch (IOException ioe) {
                        Log.e(LOG_TAG, "Error: " + ioe);
                    }
                }
        }
    }

    private void loadBitmapSat() {
        if (mBitmap != null) {
            int progressSat = mSeekBar.getProgress();
            float sat = (float) progressSat / 256;
            Log.d(LOG_TAG, "sat= " + sat);
            //satText.setText("Saturation: " + String.valueOf(sat));
            //imageResult.setImageBitmap(updateSat(bitmapMaster, sat));
            mImageView.setImageBitmap(makeBitmapBw(mBitmap, sat));
        }
    }

    private Bitmap makeBitmapBw(Bitmap src, float settingSat) {

        int w = src.getWidth();
        int h = src.getHeight();

        float[] cmDataBw = new float[]{
                0.3f, 0.59f, 0.11f, 0, 0,
                0.3f, 0.59f, 0.11f, 0, 0,
                0.3f, 0.59f, 0.11f, 0, 0,
                0, 0, 0, 1, 0,};

        Bitmap bitmapResult =
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();
        //ColorMatrix colorMatrix = new ColorMatrix(cmDataBw);
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(settingSat);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        return bitmapResult;
    }
}
