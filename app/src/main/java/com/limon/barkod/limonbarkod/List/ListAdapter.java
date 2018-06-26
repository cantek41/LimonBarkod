package com.limon.barkod.limonbarkod.List;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limon.barkod.limonbarkod.R;
import com.limon.barkod.limonbarkod.logger.CustomLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Cantekin on 8.1.2017.
 */
public class ListAdapter extends ArrayAdapter<Map<String, Object>> {
    private final String TAG = "ListAdapter";
    private List<Map<String, Object>> data;
    Context mContext;

    public ListAdapter(Context context, int resource, ArrayList<Map<String, Object>> objects) {
        super(context, resource, objects);
        data = objects;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(mContext);
            v = vi.inflate(R.layout.row_list, null);
        } else
            ((LinearLayout) v).removeAllViews();
        CustomLogger.error(TAG + "position", position + " ");


        LinearLayout rowLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        rowLayout.setLayoutParams(params);
        rowLayout.setOrientation(LinearLayout.VERTICAL); //dikey
//      rowLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.rowTransparan));


        Map<String, Object> o = data.get(position);
        if (o != null) {
            for (Map.Entry<String, Object> item : o.entrySet()) {

                TextView label = new TextView(mContext);
                label.setTextSize(12);
                SpannableString spanString = new SpannableString(item.getKey() + ": ");
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                label.setText(spanString);

                TextView value = new TextView(mContext);
                value.setTextSize(12);
                value.setText(item.getValue() + "");

                LinearLayout r = new LinearLayout(mContext);
                r.setLayoutParams(params);
                r.setOrientation(LinearLayout.HORIZONTAL); //dikey

                r.addView(label);
                r.addView(value);
                rowLayout.addView(r);
                CustomLogger.alert(TAG, item.getKey() + ": " + item.getValue());

            }
        }
        ((LinearLayout) v).addView(rowLayout);
        return v;
    }
}
