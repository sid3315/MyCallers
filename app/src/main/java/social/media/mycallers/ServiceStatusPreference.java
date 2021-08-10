package social.media.mycallers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServiceStatusPreference
{
    public static String SERVICE_STATUS = "NO";


    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setServiceStatus(Context context, String status) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(SERVICE_STATUS, status);
        editor.apply();
    }

    public static String getServiceStatus(Context context) {
        return getPreferences(context).getString(SERVICE_STATUS, "NO");
    }
}
