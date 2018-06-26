package com.limon.barkod.limonbarkod.webApi;

import android.os.AsyncTask;

import com.limon.barkod.limonbarkod.logger.CustomLogger;

import java.io.File;

/**
 * post metodunu arka planda çalıştırmak için
 * bunu kullanacak actitivyler ThreadInterface interfacesi ile
 * implemente olmalı, çünkü
 * dönen değer postResult() metodune gönderiliyor.
 * Created by Cantekin on 28.7.2016.
 */

public class ThreadWebApiPostFile extends AsyncTask<String, String, String> {

    private static final String TAG = "ThreadWebApiPostFile";
    private IThreadDelegete delegate = null;
    private final String webApiAddres;
    private final int requestCode;
    private File file;


    public ThreadWebApiPostFile(int requestCode, IThreadDelegete delegate, File file, String webApiAddres) {
        this.delegate = delegate;
        this.webApiAddres = webApiAddres;
        this.requestCode = requestCode;
        this.file=file;
    }

    @Override
    protected String doInBackground(String... params) {
        return new RestApi(webApiAddres).PostFile(file);
    }

    @Override
    protected void onPostExecute(String result) {
        if (delegate != null) {
            delegate.postResult(result,requestCode);
        } else {
            CustomLogger.error(TAG, "delegate is null");
        }
    }
}
