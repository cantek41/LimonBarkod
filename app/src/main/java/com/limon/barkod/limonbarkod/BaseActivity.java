package com.limon.barkod.limonbarkod;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

/**
 * Created by Cantekin on 21.1.2017.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected ProgressDialog mProgressDialog;

    /**
     * user reme tıklayınca
     *
     * @param v
     */
    public void ShowDetail(View v) {
    }



    /**
     * aktivitenin tamamında toas mesajlarının kontolu buradan yapılır
     *
     * @param msg
     */
    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * All Activity
     * Message Progressbar Open
     *
     * @param msg
     */
    public void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();
        mProgressDialog = ProgressDialog.show(this, getResources().getString(
                R.string.app_name), msg);
    }

    /**
     * All Activity
     * ProgressBar Close
     */
    public void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            showMessage("İzinleri vermezseniz uygulama doğru çalışmayacaktır.");
        }
    }
}
