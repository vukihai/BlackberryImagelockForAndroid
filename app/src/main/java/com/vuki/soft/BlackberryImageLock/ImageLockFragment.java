package com.vuki.soft.BlackberryImageLock;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class ImageLockFragment extends Fragment implements View.OnTouchListener, ContainOnResponseListener{
    IOnResponseListener callback;
    String TAG = "chooseNumber";

    ImageView mMatrixImageView, mBgImageView;
    CoordinatorLayout snackbarLayout;
    Matrix matrix;
    int curentPosx, curetnPosy;
    int sumMovex = 0, sumMovey = 0;
    int choosedNumberPosX = -1, choosedNumberPosY = -1;
    int choosedNumber = -1;
    int matrixImageWidth, matrixImageHeight;
    int choose;
    Snackbar snackbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_image_lock_choose_number, container, false);
        mMatrixImageView = v.findViewById(R.id.imv_matrix_num);
        mBgImageView = v.findViewById(R.id.imv_background);
        snackbarLayout = v.findViewById(R.id.snackbar);

        Bundle args = getArguments();
//        String bgUriString = "android.resource://"+getContext().getString(R.string.package_name)+"/drawable/default";
//        if (args != null) {
//            bgUriString = args.getString(ARG_IMG_BG_URI, bgUriString);
//        }
//        mBgImageView.setImageURI(Uri.parse(bgUriString));
        mMatrixImageView.setOnTouchListener(this);
        matrixImageWidth = getResources().getSystem().getDisplayMetrics().widthPixels;
        matrixImageHeight = getResources().getSystem().getDisplayMetrics().heightPixels;
        matrix = new Matrix(matrixImageWidth, matrixImageHeight);
        mMatrixImageView.setImageBitmap(matrix.drawMatrix(0, 0));
        snackbar = Snackbar.make(snackbarLayout, getString(R.string.touch_a_number), Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        sumMovex = 0;
        sumMovey = 0;
        choosedNumberPosX = -1;
        choosedNumberPosY = -1;
        choosedNumber = -1;
        choose = 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            // cham tay vao man hinh
            sumMovex += (int) motionEvent.getX() - curentPosx;
            sumMovey += (int) motionEvent.getY() - curetnPosy;
            if (sumMovex + choosedNumberPosX < 0) {
                sumMovex = -choosedNumberPosX;
            }
            if (sumMovey + choosedNumberPosY < 0) {
                sumMovey = -choosedNumberPosY;
            }
            if (sumMovex + choosedNumberPosX > matrixImageWidth) {
                sumMovex = matrixImageWidth - choosedNumberPosX;
            }
            if (sumMovey + choosedNumberPosY > matrixImageHeight) {
                sumMovey = matrixImageHeight - choosedNumberPosY;
            }
            if (choose == 0) {
                matrix.chooseNumber((int) motionEvent.getX(), (int) motionEvent.getY(), sumMovex, sumMovey);
                choosedNumberPosX = matrix.getChoosedNumberPosX();
                choosedNumberPosY = matrix.getChoosedNumberPosY();
                mMatrixImageView.setImageBitmap(matrix.drawMatrix(sumMovex + (int) motionEvent.getX() - curentPosx, sumMovey + (int) motionEvent.getY() - curetnPosy));
                snackbar.setText(getString(R.string.drag_number_to_position));
//                snackbar.show();
            } else {
                snackbar.setText(getString(R.string.image_lock_done));
                choosedNumber = matrix.getNumber(choosedNumberPosX + sumMovex, choosedNumberPosY + sumMovey, sumMovex, sumMovey);
                callback.onResponse(3, choosedNumber, choosedNumberPosX + sumMovex, choosedNumberPosY + sumMovey);
            }
            choose++;
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            // nhac tay khoi man hinh
            curentPosx = (int) motionEvent.getX();
            curetnPosy = (int) motionEvent.getY();
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            // dang keo drag
            int newMovex = sumMovex + (int) motionEvent.getX() - curentPosx;
            int newMovey = sumMovey + (int) motionEvent.getY() - curetnPosy;
            if (newMovex + choosedNumberPosX < 0) {
                newMovex = -choosedNumberPosX;
            }
            if (newMovey + choosedNumberPosY < 0) {
                newMovey = -choosedNumberPosY;
            }
            if (newMovex + choosedNumberPosX > matrixImageWidth) {
                newMovex = matrixImageWidth - choosedNumberPosX;
            }
            if (newMovey + choosedNumberPosY > matrixImageHeight) {
                newMovey = matrixImageHeight - choosedNumberPosY;
            }
            mMatrixImageView.setImageBitmap(matrix.drawMatrix(newMovex, newMovey));
        }
        return true;

    }

    public void setOnResponseListener(IOnResponseListener callback) {
        this.callback = callback;
    }
}