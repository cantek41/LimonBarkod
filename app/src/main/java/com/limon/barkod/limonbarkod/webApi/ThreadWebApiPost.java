package com.limon.barkod.limonbarkod.webApi;

import android.os.AsyncTask;
import com.limon.barkod.limonbarkod.logger.CustomLogger;

/**
 * post metodunu arka planda çalıştırmak için
 * bunu kullanacak actitivyler ThreadInterface interfacesi ile
 * implemente olmalı, çünkü
 * dönen değer postResult() metodune gönderiliyor.
 * Created by Cantekin on 28.7.2016.
 */

public class ThreadWebApiPost<T> extends AsyncTask<T, String, String> {

    private static final String TAG = "ThreadWebApiPost";
    private IThreadDelegete delegate = null;
    private final T jSonRequest;
    private final String webApiAddres;
    private int requestCode;

    public ThreadWebApiPost(int requestCode, IThreadDelegete delegate, T jSonRequest, String webApiAddres) {
        this.delegate = delegate;
        this.jSonRequest = jSonRequest;
        this.webApiAddres = webApiAddres;
        this.requestCode = requestCode;
    }

    @Override
    protected String doInBackground(T... params) {
        return new RestApi<T>(webApiAddres).Post(jSonRequest);
    }

    @Override
    protected void onPostExecute(String result) {
        if (delegate != null) {
            delegate.postResult(result,requestCode);
        } else {
            CustomLogger.error(TAG, "delegate is null" + jSonRequest.getClass().getName());
        }
    }
}
