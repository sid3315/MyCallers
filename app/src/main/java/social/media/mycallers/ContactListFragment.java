package social.media.mycallers;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ContactListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private final static String SAVE_STATE_KEY = "mcListFrag";

    private final String[] projection = new String[] { ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI };
    private final String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";

    private ListView mContactListView;
    private CursorAdapter mCursorAdapter;
    Context context2;
    //ArrayList<Service> actorList;

    //public static View view ;




    private class ContactsCursorAdapter extends SimpleCursorAdapter {
        public ContactsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);

            context2 = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ret = super.getView(position, convertView, parent);

            CheckBox checkbox = (CheckBox) ret.findViewById(R.id.contactCheck);

            getCursor().moveToPosition(position);
            String id = getCursor().getString(0);
            checkbox.setChecked(results.containsKey(id));
            ImageView imageView = (ImageView) ret.findViewById(R.id.contactImage);
            String imgUri = getCursor().getString(2);
            if(imgUri == null || imgUri.equals("")) {
                imageView.setImageResource(R.drawable.ic_contact_picture);
            }

           /* String CheckBoxState = CheckBoxPreference.getCheckboxStatus(context2);

            if(CheckBoxState.equals("Yes"))
            {
                checkbox.setChecked(true);
            }
            else
            {
                checkbox.setChecked(false);
            }*/

            return ret;


        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(SAVE_STATE_KEY, results);
    }

    private Hashtable<String, ContactResult> results = new Hashtable<String, ContactResult>();

    public Hashtable<String, ContactResult> getResults() {
        return results;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCursorAdapter = new ContactsCursorAdapter(getActivity(), R.layout.contact_list_item, null,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.PHOTO_THUMBNAIL_URI },
                new int[] { R.id.contactLabel, R.id.contactImage }, 0);

        getLoaderManager().initLoader(0, null, this);
        Log.d("fragmentOnCreate ", "True");

    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            results = (Hashtable<String, ContactResult>) savedInstanceState.getSerializable(SAVE_STATE_KEY);
        }

        View rootView = inflater.inflate(R.layout.contact_list_fragment, container);

        mContactListView = (ListView) rootView.findViewById(R.id.contactListView);

        mContactListView.setAdapter(mCursorAdapter);

        mContactListView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Log.d("onCreateLoader ", "onCreateLoader ");

        return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI,
                projection, selection, null, ContactsContract.Contacts.DISPLAY_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d("onLoadFinished ", "onLoadFinished ");


        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long rowId) {
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.contactCheck);

        Cursor cursor = mCursorAdapter.getCursor();
        cursor.moveToPosition(pos);
        String id = cursor.getString(0);

        if (checkbox.isChecked())
        {
            checkbox.setChecked(false);
            CheckBoxPreference.setCheckboxStatus(getContext(),"No");
            results.remove(id);
        }
        else
        {
            checkbox.setChecked(true);
            CheckBoxPreference.setCheckboxStatus(getContext(),"Yes");

            Cursor itemCursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[] { id }, null);
            List<ContactResult.ResultItem> resultItems = new LinkedList<ContactResult.ResultItem>();

            itemCursorLoop:
            while (itemCursor.moveToNext()) {
                String contactNumber = itemCursor.getString(itemCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                int contactKind = itemCursor.getInt(itemCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                Log.d("onItemClick While Loop ", "contactNumber "+contactNumber);
                Log.d("onItemClick While Loop ", "contactKind "+contactKind);

                for (ContactResult.ResultItem previousItem : resultItems) {
                    if (contactNumber.equals(previousItem.getResult())) {
                        continue itemCursorLoop;
                    }
                }

                resultItems.add(new ContactResult.ResultItem(contactNumber, contactKind));
            }
            itemCursor.close();

            if (resultItems.size() > 1)
            {
                // contact has multiple items - user needs to choose from them
                chooseFromMultipleItems(resultItems, checkbox, id);
            }
            else
            {
                // only one result or all items are similar for this contact
                results.put(id, new ContactResult(id, resultItems));
            }
        }
    }

///

    ///
    protected void chooseFromMultipleItems(List<ContactResult.ResultItem> items, CheckBox checkbox, String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ArrayList<String> itemLabels = new ArrayList<String>(items.size());

        for (ContactResult.ResultItem resultItem : items) {
            itemLabels.add(resultItem.getResult());

        }

        class ClickListener implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener, DialogInterface.OnMultiChoiceClickListener {
            private List<ContactResult.ResultItem> items;
            private CheckBox checkbox;
            private String id;
            private boolean[] checked;

            public ClickListener(List<ContactResult.ResultItem> items, CheckBox checkbox, String id) {
                this.items = items;
                this.checkbox = checkbox;
                this.id = id;
                checked = new boolean[items.size()];

                Log.d("ClickListener ", "True");
            }

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

            @Override
            public void onClick(DialogInterface arg0, int which, boolean isChecked) {
                checked[which] = isChecked;
            }

            private void finish() {
                ArrayList<ContactResult.ResultItem> result = new ArrayList<ContactResult.ResultItem>(items.size());
                for (int i = 0; i < items.size(); ++i) {
                    if (checked[i]) {
                        result.add(items.get(i));
                    }
                }
                if (result.size() == 0) {
                    checkbox.setChecked(false);
                } else {
                    results.put(id, new ContactResult(id, result));
                }
            }

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }

        }

        ClickListener clickListener = new ClickListener(items, checkbox, id);

        builder
                .setMultiChoiceItems(itemLabels.toArray(new String[0]), null, clickListener)
                .setOnCancelListener(clickListener)
                .setPositiveButton(android.R.string.ok, clickListener)
                .show();
    }

    private String readFile()
    {
        File fileEvents = new File(getContext().getFilesDir()+"/text/specificContacts");
        StringBuilder text = new StringBuilder();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e)
        { }
        String result = text.toString();
        Log.d("File  ","Read: "+result);
        return result;
    }

}
