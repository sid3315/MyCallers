package social.media.mycallers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;

import com.android.internal.ITelephony;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class CallReceiver extends PhonecallReceiver {
    Context context;
    final static String CHANNEL_ID = "channel_01";

    ITelephony telephonyService;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onIncomingCallStarted(final Context ctx, String pnumber, Date start) {
        Toast.makeText(ctx, "You Have an Incoming Call from " + pnumber, Toast.LENGTH_LONG).show();
        Log.d("Phone ", "Number: " + pnumber);
        context = ctx;


        String status;
        status = ServiceStatusPreference.getServiceStatus(context);

        ServiceTypePreference serviceTypePreference = new ServiceTypePreference();

        String type;
        type = serviceTypePreference.getServiceType(context);


        if (status.equals("YES")) {
            Log.d("Switch ", "state: " + status);
            if (type.equals("Contacts")) {
                Log.d("Service ", "Type: " + type);
                boolean isNumberpresent = contactExists(ctx, pnumber);
                if (isNumberpresent) {
                    Log.d("Contact ", "Present ");
                } else {
                    rejectCall();
                }
            } else if (type.equals("Specific")) {

                Log.d("Incoming ", "Number: " + pnumber);

                Log.d("Service ", "Type: " + type);
                allowedNumbers(ctx, pnumber);

            } else if (type.equals("National")) {
                Log.d("Service ", "Type: " + type);
                onlyNational(ctx, pnumber);

            }
            //rejectCall();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private boolean contactExists(Context context, String pnumber) {
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(pnumber));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String pnumber, Date start, Date end) {
        Toast.makeText(ctx, "Call dropped " + pnumber, Toast.LENGTH_LONG).show();
        //sendNotification(ctx,pnumber);

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void onlyNational(Context context, String pnumber) {
        if (pnumber.startsWith("+91")) {
            Log.d("Number is ", "National: " + pnumber);
        } else {
            rejectCall();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void allowedNumbers(Context context, String pnumber2) {

        String specificContact = readFile();
        Log.d("Specific ", "Contacts " + specificContact);
        if (specificContact.contains(pnumber2)) {
           /* TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            if (telecomManager != null) {
                ITelephony telephonyService = getTelephonyService(context);
                if (telephonyService != null) {
                    // telephonyService.silenceRinger();
                    telephonyService.answerRingingCall();
                }
            }*/

          //  acceptCall();
            Log.d("Contact ", "Allowed " + pnumber2);
        } else {
            rejectCall();
            Log.d("Contact ", "Rejected " + pnumber2);
        }
    }


    private String readFile() {
        File fileEvents = new File(context.getFilesDir() + "/text/specificContacts");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
        }
        String result = text.toString().replace(" ", "");
        Log.d("File  ", "Read: " + result);
        return result;
    }

   /* public boolean contactExists(Context context, String number) {
/// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        }
        finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }*/

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void rejectCall() {
        try {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                Method m = telecomManager.getClass().getDeclaredMethod("getITelephony");

                m.setAccessible(true);
                telephonyService = (ITelephony) m.invoke(telecomManager);
                if (telecomManager != null) {
                   // telecomManager.endCall();
                    telephonyService.endCall();
                }
            } else {
                rejectCallViaTelephonyManager();
            }*/

           /* TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            Class clazz = Class.forName(telephonyManager.getClass().getName());
            Method method = clazz.getDeclaredMethod("getITelephony");
            method.setAccessible(true);
            ITelephony telephonyService = (ITelephony) method.invoke(telephonyManager);
            telephonyService.endCall();*/

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (tm != null) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    boolean success = tm.endCall();
                }
                // success == true if call was terminated.
            } else {
                if (context != null) {
                    TelephonyManager telephony = (TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    try {
                        Class c = Class.forName(telephony.getClass().getName());
                        Method m = c.getDeclaredMethod("getITelephony");
                        m.setAccessible(true);
                        telephonyService = (ITelephony) m.invoke(telephony);
                        // telephonyService.silenceRinger();
                        telephonyService.endCall();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.d("Incoming ", "call ended successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            TelecomManager tm = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            if (tm != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                boolean success = tm.endCall();
            }
            // success == true if call was terminated.
        } else {
            if (context != null) {
                TelephonyManager telephony = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                try {
                    Class c = Class.forName(telephony.getClass().getName());
                    Method m = c.getDeclaredMethod("getITelephony");
                    m.setAccessible(true);
                    telephonyService = (ITelephony) m.invoke(telephony);
                    // telephonyService.silenceRinger();
                    telephonyService.endCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/
    }

    private void rejectCallViaTelephonyManager() {
        ITelephony telephonyService = getTelephonyService(context);
        if (telephonyService != null) {
            telephonyService.silenceRinger();
            telephonyService.endCall();
        }
    }

    private ITelephony getTelephonyService(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            return (ITelephony) m.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void acceptCall(){
        TelecomManager tm = (TelecomManager) context
                .getSystemService(Context.TELECOM_SERVICE);

        if (tm == null) {
            // whether you want to handle this is up to you really
            throw new NullPointerException("tm == null");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tm.acceptRingingCall();
        }
        else
        {
            acceptCallViaTelephonyManager();
        }
    }

    private void acceptCallViaTelephonyManager() {
        ITelephony telephonyService = getTelephonyService(context);
        if (telephonyService != null) {
            telephonyService.silenceRinger();
            telephonyService.answerRingingCall();
        }
    }


    /*@RequiresApi(api = Build.VERSION_CODES.P)
    public void acceptCall() {


    try {
            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            // Disable access check
            method.setAccessible(true);
            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = method.invoke(telephonyManager);
            // Get the endCall method from ITelephony
            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("answerRingingCall");
            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {
            *//*Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
            buttonUp.putExtra(Intent.EXTRA_KEY_EVENT,
                    new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
            context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");*//*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
                if (telecomManager != null) {
                    ITelephony telephonyService = getTelephonyService(context);
                    if (telephonyService != null) {
                        // telephonyService.silenceRinger();
                        telephonyService.answerRingingCall();
                    }
                }
            } else {
                acceptCallViaTelephonyManager2();
            }
            Log.d("Incoming ", "call received successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acceptCallViaTelephonyManager2() {
        ITelephony telephonyService = getTelephonyService2(context);
        if (telephonyService != null) {
            telephonyService.silenceRinger();
            telephonyService.answerRingingCall();
        }
    }

    private ITelephony getTelephonyService2(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            return (ITelephony) m.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/



    /*static void sendNotification(Context context, String number) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, Dashboard.class);

        notificationIntent.putExtra("from_notification", true);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(Dashboard.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.mipmap.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setColor(Color.BLUE)
                .setContentTitle("Call Rejected")
                .setContentText("Incoming Call "+number)
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_NONE);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);

            // Channel ID
            builder.setChannelId(CHANNEL_ID);
        }

        // Issue the notification
        mNotificationManager.notify(0, builder.build());


    }
*/
}