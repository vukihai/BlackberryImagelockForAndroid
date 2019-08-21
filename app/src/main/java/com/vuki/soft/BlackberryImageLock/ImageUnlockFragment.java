package com.vuki.soft.BlackberryImageLock;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


public class ImageUnlockFragment extends Fragment implements View.OnTouchListener, ContainOnResponseListener {
    IOnResponseListener callback;
    Context mContext;
    String TAG = "unlockImage";

    private ImageView mMatrixImageView, mBgImageView;
    private Matrix matrix;
    private int curentPosx, curetnPosy;
    private int sumMovex = 0, sumMovey = 0;
    private int tryCount = 0;
    private int lookPointx = 345, lookPointy = 345;
    private int lookNumber = 1;
    int matrixImageWidth, matrixImageHeight;
    int choose = 0;
    Snackbar snackbar;
    CoordinatorLayout snackbarLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_image_lock_choose_number, container, false);
        mContext = getContext();
//        String bgUriString = "android.resource://"+getContext().getString(R.string.package_name)+"/drawable/default";

        Bundle bundle = getArguments();
        if (bundle != null) {
            lookNumber = bundle.getInt("BB_PASS_NUMBER", -1);
            lookPointx = bundle.getInt("BB_PASS_X", -1);
            lookPointy = bundle.getInt("BB_PASS_Y", -1);
            Log.d("vukihai", ""+ lookNumber + " " + lookPointx + " " + lookPointy);
//            bgUriString = bundle.getString(ARG_IMG_BG_URI, bgUriString);
//            Log.d(TAG, "bundle: "+lookNumber + " "+lookPointx + " "+lookPointy);
        }

        mMatrixImageView = v.findViewById(R.id.imv_matrix_num);
        mBgImageView = v.findViewById(R.id.imv_background);
        snackbarLayout = v.findViewById(R.id.snackbar);

//        mBgImageView.setImageURI(Uri.parse(bgUriString));
        mMatrixImageView.setOnTouchListener(this);
        matrixImageWidth = getResources().getSystem().getDisplayMetrics().widthPixels;
        matrixImageHeight = getResources().getSystem().getDisplayMetrics().heightPixels;
        matrix = new Matrix(matrixImageWidth, matrixImageHeight);
        mMatrixImageView.setImageBitmap(matrix.drawMatrix(0, 0));
        snackbar = Snackbar.make(snackbarLayout, getString(R.string.drag_number_to_selected_pos), Snackbar.LENGTH_LONG);
        snackbar.show();
        return v;
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            //matrix.chooseNumber((int)motionEvent.getX(), (int)motionEvent.getY(),sumMovex, sumMovey);

            //choose++;
            sumMovex += (int) motionEvent.getX() - curentPosx;
            sumMovey += (int) motionEvent.getY() - curetnPosy;
            if (matrix.getNumber(lookPointx, lookPointy, sumMovex, sumMovey) == lookNumber) {
                Toast.makeText(mContext, getString(R.string.unlock_success), Toast.LENGTH_SHORT).show();
                callback.onResponse(4, true);
                tryCount = 1;
            } else {
                Toast.makeText(mContext, getString(R.string.unlock_failed), Toast.LENGTH_SHORT).show();
            }
            if (tryCount == 1) {
                matrix = new Matrix(matrixImageWidth, matrixImageHeight);
                mMatrixImageView.setImageBitmap(matrix.drawMatrix(0, 0));
                sumMovey = 0;
                sumMovex = 0;
                tryCount = -1;
            }
            tryCount++;
//            if(choose%2 ==0){
//                matrix.chooseNumber((int)motionEvent.getX(),(int)motionEvent.getY(), sumMovex, sumMovey);
//                mMatrixImageView.setImageBitmap(matrix.drawMatrix(sumMovex, sumMovey));
//            }
//            choose++;
//
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

//
            curentPosx = (int) motionEvent.getX();
            curetnPosy = (int) motionEvent.getY();

        }
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

            int tmpx = (int) motionEvent.getX();
            int tmpy = (int) motionEvent.getY();
            mMatrixImageView.setImageBitmap(matrix.drawMatrix(sumMovex + (int) motionEvent.getX() - curentPosx, sumMovey + (int) motionEvent.getY() - curetnPosy));
        }
        return true;

    }

    public void setOnResponseListener(IOnResponseListener callback) {
        this.callback = callback;
    }
}