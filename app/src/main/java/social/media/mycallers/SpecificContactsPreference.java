package social.media.mycallers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SpecificContactsPreference
{
    public String SELECTED_CONTACTS = "Empty";


    SharedPreferences getSpecificContactsPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public  void setSelectedContacts(Context context, String serviceType) {
        SharedPreferences.Editor editor = getSpecificContactsPreferences(context).edit();
        editor.putString(SELECTED_CONTACTS, serviceType);
        editor.apply();
    }

    public  String getSelectedContacts(Context context) {
        return getSpecificContactsPreferences(context).getString(SELECTED_CONTACTS, "Empty");
    }
}
