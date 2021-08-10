package social.media.mycallers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ServiceTypePreference
{
    public String SERVICE_TYPE = "Empty";


    SharedPreferences getServiceTypePreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public  void setServiceType(Context context, String serviceType) {
        SharedPreferences.Editor editor = getServiceTypePreferences(context).edit();
        editor.putString(SERVICE_TYPE, serviceType);
        editor.apply();
    }

    public  String getServiceType(Context context) {
        return getServiceTypePreferences(context).getString(SERVICE_TYPE, "Empty");
    }
}
