package com.vuki.soft.BlackberryImageLock;

import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

public class UnlockActivity extends AppCompatActivity implements View.OnTouchListener, LockScreenUtils.OnLockStatusChangedListener {
    SharedPreferences sharedPreferences;
    String packageName;
    LockScreenUtils mLockScreenUtils;
    int imageNumber, imagePosX, imagePosY;
    private ImageView mMatrixImageView;
    private Matrix matrix;
    private int curentPosx, curetnPosy;
    private int sumMovex = 0, sumMovey = 0;
    private int tryCount = 0;
    private int lookPointx = 345, lookPointy = 345;
    private int lookNumber = 1;
    int matrixImageWidth, matrixImageHeight;
    Intent thisIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        getSupportActionBar().hide();
        mLockScreenUtils = new LockScreenUtils();
        sharedPreferences = getSharedPreferences("vukihai", MODE_PRIVATE);
        matrixImageWidth = getResources().getSystem().getDisplayMetrics().widthPixels;
        matrixImageHeight = getResources().getSystem().getDisplayMetrics().heightPixels;
        mMatrixImageView = findViewById(R.id.imv_matrix_num);
        mMatrixImageView.setOnTouchListener(this);
        matrix = new Matrix(matrixImageWidth,matrixImageHeight);
        mMatrixImageView.setImageBitmap(matrix.drawMatrix(0,0));
        if (getIntent() != null && getIntent().hasExtra("kill")
                && getIntent().getExtras().getInt("kill") == 1) {
            enableKeyguard();
            unlockHomeButton();
        } else {

            try {
                disableKeyguard();
                lockHomeButton();
                StateListener phoneStateListener = new StateListener();
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                telephonyManager.listen(phoneStateListener,
                        PhoneStateListener.LISTEN_CALL_STATE);

            } catch (Exception e) {
            }

        }

    }
    @SuppressWarnings("deprecation")
    private void disableKeyguard(){
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.disableKeyguard();
    }
    @SuppressWarnings("deprecation")
    private void enableKeyguard() {
        KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
        mKL.reenableKeyguard();
    }
    // Handle events of calls and unlock screen if necessary
    private class StateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    unlockHomeButton();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
            }
        }
    };
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
                || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
            return false;
        }
        if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

            return true;
        }
        return false;
    }
    public void lockHomeButton() {
        mLockScreenUtils.lock(UnlockActivity.this);
    }
    @Override
    public void onLockStatusChanged(boolean isLocked) {
        if (!isLocked) {
            unlockDevice();
        }
    }
    private void unlockDevice()
    {
        finish();
    }

    @Override
    public void onBackPressed() {
        return;
    }
    @Override
    protected void onResume() {
        super.onResume();
        lookNumber = sharedPreferences.getInt("BB_PASS_NUMBER", -1);
        lookPointx = sharedPreferences.getInt("BB_PASS_X", -1);
        lookPointy = sharedPreferences.getInt("BB_PASS_Y", -1);
    }
    @Override
    public void onAttachedToWindow() {
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
        this.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        );
        super.onAttachedToWindow();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_HOME)
        {
            Log.i("Home Button","Clicked");
        }
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return false;
    }
    @Override
    protected void onStop(){
        super.onStop();
        unlockHomeButton();
    }
    public void unlockHomeButton() {
        mLockScreenUtils.unlock();
    }
    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            sumMovex += (int) motionEvent.getX() - curentPosx;
            sumMovey += (int) motionEvent.getY() - curetnPosy;
            if (matrix.getNumber(lookPointx, lookPointy, sumMovex, sumMovey) == lookNumber) {
                Toast.makeText(this, getString(R.string.unlock_success), Toast.LENGTH_SHORT).show();
                finish();
//                moveTaskToBack(true);
//                android.os.Process.killProcess(android.os.Process.myPid());
//                System.exit(1);
                tryCount = 1;
            } else {
                Toast.makeText(this, getString(R.string.unlock_failed), Toast.LENGTH_SHORT).show();
            }
            if (tryCount == 1) {
                matrix = new Matrix(matrixImageWidth, matrixImageHeight);
                mMatrixImageView.setImageBitmap(matrix.drawMatrix(0, 0));
                sumMovey = 0;
                sumMovex = 0;
                tryCount = -1;
            }
            tryCount++;
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

}