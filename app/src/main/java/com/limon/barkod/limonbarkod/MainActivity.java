package com.limon.barkod.limonbarkod;

import android.Manifest;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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
import java.util.Map;

//import me.sudar.zxingorient.Barcode;
//import me.sudar.zxingorient.ZxingOrient;
//import me.sudar.zxingorient.ZxingOrientResult;

public class MainActivity extends BaseActivity implements IThreadDelegete {

    private static final int REQUEST_READ = 10002;
    private static String TAG = "MainActivity";
    RequestModel request;
    private String webApiAddress; //= "http://213.74.223.90:1560/home/getChart";
    private String api = "/home/getChart";
    private List<ResultItem> resultitem;
    private LinearLayout listFragment;
    Button btn;
    ImageButton barkodBtn;
    EditText barkodEdt;
    int clickCount = 0;

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
        barkodEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCount++;
                if (clickCount >= 5) {
                    showSettings();
                    clickCount = 0;
                }
            }
        });
        barkodBtn = (ImageButton) findViewById(R.id.imageButton);
        barkodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBarkod();
            }
        });
        getSupportActionBar().setHomeButtonEnabled(true);
        setWepApiAddress();
    }

    private void setWepApiAddress() {
        String URL = MyPreference.getPreference(this).getMainURL();
        webApiAddress = URL + api;
    }

    private void showSettings() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ayarlar");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
        input.setText(MyPreference.getPreference(getApplicationContext()).getMainURL());
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyPreference.getPreference(getApplicationContext()).setMainURL(input.getText().toString());
                setWepApiAddress();
            }
        });
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void getBarkod() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
            return;
        }
        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setPrompt("Barkod okutunuz");
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.initiateScan();

//        ZxingOrient integrator = new ZxingOrient(this);
//        integrator.setIcon(R.drawable.logoo)   // Sets the custom icon
//                .setToolbarColor("#AA3F51B5")       // Sets Tool bar Color
//                .setInfoBoxColor("#AA3F51B5")       // Sets Info box color
//                .setInfo("Barkod okutunuz")   //// TODO: 27.1.2017 danimik string
//                .initiateScan(Barcode.PRODUCT_CODE_TYPES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {


        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, intent);

        if (result.getContents() == null) {
            Log.d("MainActivity", "Cancelled scan");
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            Log.d("MainActivity", "Scanned");
//            Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            barkodEdt.setText(result.getContents());
            btn.callOnClick();
        }
//        ZxingOrientResult scanResult =
//                ZxingOrient.parseActivityResult(requestCode, resultCode, intent);
//
//        if (scanResult != null && scanResult.getContents() != null) {
//            barkodEdt.setText(scanResult.getContents());
//            btn.callOnClick();
//        }
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
//            showLists();

            createTableLayout();
        } catch (Exception e) {
            showMessage("Hay aksi veri çekerken hata oluştu.");
        } finally {
            dismissProgress();
        }


    }

    private void createTableLayout() {
        listFragment.removeAllViews();
        for (ResultItem item : resultitem) {
            if (item.getData().size() == 0)
                continue;
            // 1) Create a tableLayout and its params
            TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
            TableLayout tableLayout = new TableLayout(this);

            //  tableLayout.setBackgroundColor(Color.BLACK);

            // 2) create tableRow params
            TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
            tableRowParams.weight = 1;
            tableLayoutParams.topMargin = 15;

            int index = 0;
            for (Map<String, Object> row : item.getData()) {

                if (index == 0) {
                    TableRow tableRow = new TableRow(this);
                    for (Map.Entry<String, Object> column : row.entrySet()) {
                        TextView textView = new TextView(this);
                        SpannableString spanString = new SpannableString(column.getKey());
                        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                        textView.setText(spanString);
                        textView.setBackgroundColor(Color.parseColor("#CAECFF"));
                        tableRow.addView(textView, tableRowParams);
                    }
                    tableLayout.addView(tableRow, tableLayoutParams);
                }
                TableRow tableRow = new TableRow(this);
                for (Map.Entry<String, Object> column : row.entrySet()) {
                    TextView textView = new TextView(this);
                    textView.setGravity(Gravity.LEFT);
                    textView.setPadding(5, 5, 5, 5);
//                textView.setBackground(getResources().getDrawable(R.drawable.textview_border));
                    textView.setTextSize(10);
                    textView.setMaxLines(1);
//                    if (index == 0) {
//                        SpannableString spanString = new SpannableString(column.getKey());
//                        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
//                        textView.setText(spanString);
//                        textView.setBackgroundColor(Color.parseColor("#CAECFF"));
//                        tableRow.addView(textView, tableRowParams);
//                    } else {
                    if (index % 2 == 0)
                        textView.setBackgroundColor(Color.parseColor("#E8E5E5"));
                    textView.setText(column.getValue().toString());
//                    }
                    tableRow.addView(textView, tableRowParams);
                }
                index++;
                tableLayout.addView(tableRow, tableLayoutParams);
            }
            listFragment.addView(tableLayout);
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

            for (int i = jData.length() - 1; i >= 0; i--) {
                jsonObject = jData.getJSONObject(i);
                resultitem.add((ResultItem) jsonHelper.stringToObject
                        (jsonObject.toString(), ResultItem.class));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
