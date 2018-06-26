package com.limon.barkod.limonbarkod;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.limon.barkod.limonbarkod.customJson.jsonHelper;
import com.limon.barkod.limonbarkod.logger.CustomLogger;

/**
 * preferences e erişmek
 * genel ayarları
 * kaydetmek ve okumak için
 * Created by Cantekin on 11.01.2016.
 * singleton pattern
 */

public class MyPreference {
    private static String TAG = "Preference";
    private Context context;
    public static MyPreference preference;
    public static String mainURL;
    public static SharedPreferences data;

    private MyPreference(Context context) {
        this.context = context;
    }

    public static MyPreference getPreference(Context context) {
        if (preference == null)
            preference = new MyPreference(context);
        if (data == null)
            data = PreferenceManager.getDefaultSharedPreferences(context);
        mainURL = data.getString("mainURL", "http://demo.veribiscrm.com/");
        return preference;
    }

    //region MainURL
    public void setMainURL(String mainURL) {
        SharedPreferences.Editor editor = data.edit();
        if (mainURL != null) editor.putString("mainURL", mainURL);
        editor.commit();
    }

    public String getMainURL() {
        return mainURL;
    }

    //endregion
    //region Get
    public <T> T getData(String key, Class<T> clazzType) {
        String dataString = data.getString(key, null);
        if (dataString == null)
            return null;
        CustomLogger.alert(TAG,"Menu  "+dataString);
        return jsonHelper.stringToObject(dataString, clazzType);
    }



    //endregion
    //region Set
    public void setData(@NonNull String name, @NonNull String value) throws NullPointerException {
        CustomLogger.info(TAG, name + "-->" + value);
        SharedPreferences.Editor editor = data.edit();
        if (value != null && name != null) editor.putString(name, value);
        else
            throw new NullPointerException("paramtreler null olamaz");
        editor.commit();
    }

    public void setUserData(String user) {
        SharedPreferences.Editor editor = data.edit();
        CustomLogger.error(TAG, user);
        if (user != null) editor.putString("User", user);
        editor.commit();
    }

    //endregion
    //region delete
    public void clearPreferences() {
        data.edit().clear().commit();
    }

    public void deleteValue(String key) {
        SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = data.edit();
        if (key != null) editor.remove(key);
        editor.commit();
    }

    //endregion
    //region WepApi Adresleri
    public String getLoginAddress() {
        return mainURL.concat("/token");
    }

    public String getUserDataAddress() {
        return mainURL.concat("/api/admin/AccountApi/GetEmployeData");
    }

    public String getUserFormDataWebApiAddress() {
        return mainURL.concat("/api/mobil/User/GetUserData");
    }

    public String getGetAddress() {
        return mainURL.concat("/api/mobile/GetData");
    }

    public String getSetAddress() {
        return mainURL.concat("/api/mobile/UpdateData");
    }

    public String getListAddress() {
        return mainURL.concat("/api/mobile/GetList");
    }

    public String getSaveFileAddress() {
        return mainURL.concat("/api/mobile/SaveFile");
    }

    public String getSqlAddress() {
        return mainURL.concat("/api/mobile/GetReportList");
    }

    public String getMenuApiAddres() {
        return mainURL.concat("/api/mobil/MenuData/GetMenu");
    }

    public String getFormApiAddres() {
        return mainURL.concat("/Api/mobil/formData/GetFormMobil");
    }

    public String getSetDeviceApiAddres() {
        return mainURL.concat("/api/mobil/User/SetDevice");
    }
    //endregion

}
