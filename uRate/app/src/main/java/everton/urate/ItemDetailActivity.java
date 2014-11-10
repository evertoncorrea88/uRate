package everton.urate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;


public class ItemDetailActivity extends Activity {
    private Button btnSave;
    private Button btnEdit;
    private Button btnCancel;

    private EditText etName;
    private Spinner spinCategory;
    private EditText etAddress;
    private RatingBar rbRate;
    private EditText etNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        etName = (EditText) findViewById(R.id.et_name_item);
        spinCategory =  (Spinner) findViewById(R.id.spin_category);
        etAddress = (EditText) findViewById(R.id.et_address);
        rbRate = (RatingBar) findViewById(R.id.rb_rate);
        etNotes = (EditText) findViewById(R.id.et_notes);

        Intent intent = getIntent();
        boolean editMode = intent.getExtras().getBoolean("editMode");
        executionMode(editMode);

    }

    private void executionMode(boolean editMode){


        if (editMode){
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);

            etName.setEnabled(true);
            spinCategory.setEnabled(true);
            etAddress.setEnabled(true);
            rbRate.setEnabled(true);
            etNotes.setEnabled(true);

        }else {
            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);

            etName.setEnabled(false);
            spinCategory.setEnabled(false);
            etAddress.setEnabled(false);
            rbRate.setEnabled(false);
            etNotes.setEnabled(false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
