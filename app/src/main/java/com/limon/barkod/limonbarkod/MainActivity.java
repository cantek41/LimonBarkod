package com.limon.barkod.limonbarkod;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.limon.barkod.limonbarkod.Model.RequestModel;
import com.limon.barkod.limonbarkod.Model.ResultItem;
import com.limon.barkod.limonbarkod.customJson.jsonHelper;
import com.limon.barkod.limonbarkod.logger.CustomLogger;
import com.limon.barkod.limonbarkod.webApi.IThreadDelegete;
import com.limon.barkod.limonbarkod.webApi.ThreadWebApiPost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

public class MainActivity extends BaseActivity implements IThreadDelegete {

    private static final int REQUEST_READ = 10002;
    private static String TAG = "MainActivity";
    RequestModel request;
    private String webApiAddress = "http://213.74.223.90:1560/home/getChart";
    private List<ResultItem> resultitem;
    private LinearLayout listFragment;
    Button btn;
    ImageButton barkodBtn;
    EditText barkodEdt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_Activity();
    }

    private void init_Activity() {
        listFragment = (LinearLayout) findViewById(R.id.listFragment);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        barkodEdt = (EditText) findViewById(R.id.editText);
        barkodBtn=(ImageButton) findViewById(R.id.imageButton);
        barkodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBarkod();
            }
        });

    }

    private void getBarkod() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            return;
        }
        ZxingOrient integrator = new ZxingOrient(this);
        integrator.setIcon(R.drawable.logoo)   // Sets the custom icon
                .setToolbarColor("#AA3F51B5")       // Sets Tool bar Color
                .setInfoBoxColor("#AA3F51B5")       // Sets Info box color
                .setInfo("Barkod okutunuz")   //// TODO: 27.1.2017 danimik string
                .initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        ZxingOrientResult scanResult =
                ZxingOrient.parseActivityResult(requestCode, resultCode, intent);

        if (scanResult != null && scanResult.getContents() != null) {
            barkodEdt.setText(scanResult.getContents());
            btn.callOnClick();
        }
    }

    private void search() {
        request = new RequestModel();
        request.setVal(barkodEdt.getText().toString());
        if (!request.getVal().isEmpty())
            getData();
    }

    public void getData() {
        CustomLogger.alert(TAG, jsonHelper.objectToJson(request));
        new ThreadWebApiPost<>(REQUEST_READ, this, request, webApiAddress).execute();
        showProgress("Liste Çekliyor");
    }

    @Override
    public void postResult(String data, int requestCode) {

        try {
            resultitem = new ArrayList<>();
            CustomLogger.alert(TAG, data);
            parseJson(data, resultitem);
            showLists();

        } catch (Exception e) {
            showMessage("Hay aksi veri çekerken hata oluştu.");
        } finally {
            dismissProgress();
        }


    }



    private void showLists() {
        listFragment.removeAllViews();
        for (ResultItem item : resultitem) {
            if (item.getData().size() == 0)
                continue;
            CustomLogger.alert(TAG + "LİSTTT", item.getData().toString());

            ListView ls = new ListView(this);
            ls.setId(item.getId());
            ListView.LayoutParams params =
                    new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,
                            600);

            ls.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });


            ls.setLayoutParams(params);

            com.limon.barkod.limonbarkod.List.ListAdapter adp = new com.limon.barkod.limonbarkod.List.ListAdapter(this,
                    R.layout.row_list, item.getData());
            ls.setAdapter(adp);

            TextView title = new TextView(this);
            title.setTextSize(20);
            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            title.setText(spanString);

            listFragment.addView(title);
            listFragment.addView(ls);


        }
    }

    private void parseJson(String data, List<ResultItem> resultitem) {
        JSONArray jData;
        try {
            jData = new JSONArray(data);
            JSONObject jsonObject;

            for (int i = 0; i < jData.length(); i++) {
                jsonObject = jData.getJSONObject(i);
                resultitem.add((ResultItem) jsonHelper.stringToObject
                        (jsonObject.toString(), ResultItem.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
