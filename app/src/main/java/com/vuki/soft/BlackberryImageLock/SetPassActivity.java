package com.vuki.soft.BlackberryImageLock;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;


public class SetPassActivity extends AppCompatActivity implements IOnResponseListener {

    Button btnNext, btnPrevious;
    Uri bgDrawableUri;
    int index = 2, length = 4;
    int imagePosX, imagePosY, imageNumber;
    ImageLockFragment imageLockFragment;
    String TAG = "ImageLock";
    private Intent bbServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pass);
        bbServiceIntent = new Intent(this, BbService.class);
        startService(bbServiceIntent);
        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_back);


        renderFragment(true);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderFragment(true);
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renderFragment(false);
            }
        });
    }

    public void onResponse(int screenId, Object... results) {
        for (Object result : results) {
            Log.d(TAG, "screenId = " + screenId + ", res = " + result.toString());
        }
        switch (screenId) {
//            case 2:
//                bgDrawableUri = (Uri) results[0];
//                break;
            case 3:
                imageNumber = (int) results[0];
                imagePosX = (int) results[1];
                imagePosY = (int) results[2];
                break;
            case 4:
                if ((boolean) results[0]) {
                    SharedPreferences sharedPreferences = getSharedPreferences("vukihai", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("BB_PASS_NUMBER", imageNumber);
                    editor.putInt("BB_PASS_X", imagePosX);
                    editor.putInt("BB_PASS_Y", imagePosY);
//                    editor.putString(IMAGE_PASS_BG, bgDrawableUri.toString());
                    editor.commit();

                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra(ARG_SUCCESS, true);
                    setResult(Activity.RESULT_OK, returnIntent);
                    PackageManager p = getPackageManager();
                    ComponentName componentName = new ComponentName(this, com.vuki.soft.BlackberryImageLock.SetPassActivity.class);
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    finish();
                }
        }
        enableNext(true);
    }

    public void enableNext(boolean isEnable) {
        btnNext.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }

    private void renderFragment(boolean isNext) {
        if (isNext && index + 1 <= length) {
            index++;
        } else if (!isNext && index - 1 >= 0) {
            index--;
        }
//        Log.d(TAG, "index = "+index);
        Fragment newFragment = null;
        Bundle args = new Bundle();
        switch (index) {
            case 0:
                finish();
            case 1:
//                enableNext(true);
//                if (imageLockWelcomeFragment == null) {
//                    imageLockWelcomeFragment = new ImageLockWelcomeFragment();
//                }
//                newFragment = imageLockWelcomeFragment;
//                setTitle(getString(R.string.title_image_lock));
//                break;
            case 2:
//                enableNext(false);
//                if (imageLockChooseImageFragment == null) {
//                    imageLockChooseImageFragment = new ImageLockChooseImageFragment();
//                }
//                newFragment = imageLockChooseImageFragment;
//                setTitle(getString(R.string.title_image_chooser));
//                break;
                index = 3;
            case 3:
                enableNext(false);
                imageLockFragment = new ImageLockFragment();
                newFragment = imageLockFragment;
//                args.putString(ARG_IMG_BG_URI, bgDrawableUri.toString());
//                newFragment.setArguments(args);
                setTitle(getString(R.string.title_image_lock_chooser));
                break;
            case 4:
                enableNext(false);
                newFragment = new ImageUnlockFragment();
                args.putInt("BB_PASS_X", imagePosX);
                args.putInt("BB_PASS_Y", imagePosY);
                args.putInt("BB_PASS_NUMBER", imageNumber);
//                args.putString(ARG_IMG_BG_URI, bgDrawableUri.toString());
                newFragment.setArguments(args);
                setTitle(getString(R.string.title_confirm_image_lock));
                break;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_welcome_image_lock, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        btnPrevious.callOnClick();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ContainOnResponseListener) {
            ((ContainOnResponseListener) fragment).setOnResponseListener(this);
        }
    }
}