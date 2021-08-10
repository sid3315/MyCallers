package social.media.mycallers;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ALL_PERMISSIONS = 101;

    private static final int WRITE_PERMISSION = 102;

    public static Switch ServiceButton;

    public static RadioGroup radioGroupType;

    public static Boolean switchState;

    public static RadioButton contactsButton, specificButton, nationalButton;

    public static TextView mTvContacts;

    ServiceTypePreference serviceTypePreference = new ServiceTypePreference();
    SpecificContactsPreference specificContactsPreference= new SpecificContactsPreference();

    //public static ArrayList<SelectedContacts> contacts = new ArrayList<>();

  //  private static final int CONTACT_PICKER_REQUEST = 991;
    // private ArrayList results = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        final String[] permissions = new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS);

        ServiceButton = (Switch)findViewById(R.id.switch1);
        radioGroupType = (RadioGroup) findViewById(R.id.RGroup);
        contactsButton = (RadioButton)findViewById(R.id.contactOnly);
        specificButton = (RadioButton)findViewById(R.id.specific);
        nationalButton = (RadioButton)findViewById(R.id.national);
        mTvContacts = (TextView)findViewById(R.id.tvContacts);


        File file = new File(MainActivity.this.getFilesDir(), "text");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, "specificContacts");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append("Empty");
            writer.flush();
            writer.close();
            Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
        } catch (Exception e) { }

        Log.d("File ", "Specific Contacts: Empty");
        /*switchState = ServiceButton.isChecked();
        if (switchState == true)
        {
            ServiceButton.setChecked(true);

        }
        else
        {
            ServiceButton.setChecked(false);
        }*/

        String status;
        status = ServiceStatusPreference.getServiceStatus(getApplicationContext());
        String serviceType;
        serviceType = serviceTypePreference.getServiceType(getApplicationContext());
        String AllowedContacts;

        if (status.equals("YES"))
        {
            ServiceButton.setChecked(true);
            radioGroupType.setVisibility(View.VISIBLE);
            if(serviceType.equals("Contacts"))
            {
                contactsButton.setChecked(true);
            }
            else if(serviceType.equals("Specific"))
            {
                specificButton.setChecked(true);
                AllowedContacts = specificContactsPreference.getSelectedContacts(getApplicationContext());


                mTvContacts.setText(AllowedContacts);
                Log.d("Allowed ","Contacts: "+AllowedContacts);

            }
            else if(serviceType.equals("National"))
            {
                nationalButton.setChecked(true);
            }
        }
        else
        {
            ServiceButton.setChecked(false);
        }



       /* AllowedContacts = specificContactsPreference.getSelectedContacts(getApplicationContext());


        mTvContacts.setText(AllowedContacts);
        Log.d("Allowed ","Contacts: "+AllowedContacts);*/


        Log.d("Switch ","state: "+status);




        ServiceButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
               // switchState = ServiceButton.isChecked();
                if (isChecked) {
                    ServiceStatusPreference.setServiceStatus(getApplicationContext(), "YES");
                    Log.d("Switch ", "True state: " + switchState);
                    radioGroupType.setVisibility(View.VISIBLE);


                } else {
                    ServiceStatusPreference.setServiceStatus(getApplicationContext(), "NO");
                    Log.d("Switch ", "False state: " + switchState);
                   // radioGroupType.setVisibility(View.INVISIBLE);


                }

            }
        });

        radioGroupType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(checkedId == R.id.contactOnly)
                {
                    serviceTypePreference.setServiceType(getApplicationContext(),"Contacts");
                    Toast.makeText(getApplicationContext(), "Only Contacts Allowed" , Toast.LENGTH_LONG).show();

                }
                else if(checkedId == R.id.specific)
                {
                    serviceTypePreference.setServiceType(getApplicationContext(),"Specific");
                    Toast.makeText(getApplicationContext(), "Only Specific Allowed" , Toast.LENGTH_LONG).show();

                }
                else if(checkedId == R.id.national)
                {
                    serviceTypePreference.setServiceType(getApplicationContext(),"National");
                    Toast.makeText(getApplicationContext(), "Only Nationals Allowed" , Toast.LENGTH_LONG).show();
                }

            }
        });
   }


    private void getContacts()
    {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if("1".equals(hasPhone) || Boolean.parseBoolean(hasPhone)) {
                // You know it has a number so now query it like this
                Cursor phones = getApplicationContext().getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int itype = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                    final boolean isMobile =
                            itype == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ||
                                    itype == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE;

                    // Do something here with 'phoneNumber' such as saving into
                    // the List or Array that will be used in your 'ListView'.

                }
                phones.close();
            }
        }
    }


    public void showContacts(View view) {
        // Take care of using a random request code.
        startActivityForResult(new Intent(this, ContactPickerActivity.class), 1302);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1302 && RESULT_OK == resultCode) {
            processContacts((ArrayList<ContactResult>)
                    data.getSerializableExtra(ContactPickerActivity.CONTACT_PICKER_RESULT));
        } else if(RESULT_CANCELED == resultCode) {
            if (data != null && data.hasExtra("error")) {
                mTvContacts.setText(data.getStringExtra("error"));
                Toast.makeText(getApplicationContext(),"No contacts available",Toast.LENGTH_SHORT);
            } else {
                mTvContacts.setText("Contact selection cancelled");
              //  Toast.makeText(getApplicationContext(),"Contact selection cancelled",Toast.LENGTH_SHORT);

            }
        }
    }

    private void processContacts(ArrayList<ContactResult> contacts) {
        StringBuilder sb = new StringBuilder();
        for(ContactResult contactResult : contacts) {
            //sb.append(contactResult.getContactId());
          //  sb.append(" <");
            for(ContactResult.ResultItem item : contactResult.getResults()) {
                sb.append(item.getResult().trim());
                if(contactResult.getResults().size() > 1) {
                    sb.append(", ");
                }
            }
            sb.append(", ");
        }

        String spContacts = sb.toString().replace(" ","");
        //spContacts = spContacts.replace(","," , ");
       // Toast.makeText(getApplicationContext(),"Selected Contacts: "+sb,Toast.LENGTH_LONG);
        mTvContacts.setText(spContacts);

            File file = new File(MainActivity.this.getFilesDir(), "text");
            if (!file.exists()) {
                file.mkdir();
            }
            try {
                File gpxfile = new File(file, "specificContacts");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(sb);
                writer.flush();
                writer.close();
                Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
            } catch (Exception e) { }

        Log.d("File ", "Specific Contacts: "+spContacts);

    }
   /* private String readFile() {
        File fileEvents = new File(MainActivity.this.getFilesDir()+"/text/specificContacts");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) { }
        String result = text.toString();
        return result;
    }*/
}