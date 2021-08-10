package social.media.mycallers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

public class ContactPickerActivity extends AppCompatActivity {

    public static final String PICKER_TYPE = "type";
    public static final String PICKER_TYPE_PHONE = "phone";
    public static final String CONTACT_PICKER_RESULT = "contacts";

    public static final int RESULT_ERROR = RESULT_FIRST_USER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_contact_picker);

        if (getIntent().getAction() != null && !Intent.ACTION_PICK.equals(getIntent().getAction())) {
            Intent ret = new Intent();
            ret.putExtra("error", "Unsupported action type");
            setResult(RESULT_ERROR, ret);
            return;
        }

        if (getIntent().getExtras() == null || PICKER_TYPE_PHONE.equals(getIntent().getExtras().getString(PICKER_TYPE))) {
            setContentView(R.layout.activity_contact_picker);
        } else {
            Intent ret = new Intent();
            ret.putExtra("error", "Unsupported picker type");
            setResult(RESULT_ERROR, ret);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_picker_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contacts_done) {
            returnResults();
            return true;
        } else if (item.getItemId() == R.id.contacts_cancel) {
            cancel();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void returnResults() {
        ContactListFragment fragment = (ContactListFragment) getSupportFragmentManager().getFragments().get(0);
        ArrayList<ContactResult> resultList = new ArrayList<ContactResult>( fragment.getResults().values() );
        Intent retIntent = new Intent();
        retIntent.putExtra(CONTACT_PICKER_RESULT, resultList);

        setResult(RESULT_OK, retIntent);

        finish();
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancel();
        super.onBackPressed();
    }
}
