package social.media.mycallers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CheckBoxPreference
{

    public static String CHECKBOX_STATUS = "No";


    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setCheckboxStatus(Context context, String status) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(CHECKBOX_STATUS, status);
        editor.apply();
    }

    public static String getCheckboxStatus(Context context) {
        return getPreferences(context).getString(CHECKBOX_STATUS, "No");
    }
}
