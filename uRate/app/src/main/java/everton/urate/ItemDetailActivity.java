package everton.urate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


public class ItemDetailActivity extends Activity {
    private MyApplication myApp;
    private DbAccess dbAccess;
    private Item item;
    private boolean isEditMode;

    private Button btnSave;
    private Button btnEdit;
    private Button btnCancel;
    private FloatingActionButton btnEditPicture;
    private EditText etName;
    private Spinner spinCategory;
    private EditText etAddress;
    private RatingBar rbRate;
    private EditText etNotes;
    private ImageView ivItemImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        myApp = (MyApplication) getApplication();
        dbAccess = new DbAccess(this);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        etName = (EditText) findViewById(R.id.et_name_item);
        spinCategory =  (Spinner) findViewById(R.id.spin_category);
        etAddress = (EditText) findViewById(R.id.et_address);
        rbRate = (RatingBar) findViewById(R.id.rb_rate);
        etNotes = (EditText) findViewById(R.id.et_notes);
        ivItemImg = (ImageView) findViewById(R.id.iv_item_img);

        btnEditPicture = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.ic_action_edit))
                .withButtonColor(Color.parseColor("#BDBDBD"))
                .withGravity(Gravity.TOP | Gravity.RIGHT)
                .withMargins(0, 90, 25, 0)
                .create();

        ArrayAdapter<String> adapter =  new ArrayAdapter(this, android.R.layout.simple_spinner_item, myApp.listGroup);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(adapter);

        Intent intent = getIntent();
        isEditMode = intent.getExtras().getBoolean("isEditMode");
        loadItem(isEditMode, intent);
        executionMode(isEditMode);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = true;
                executionMode(isEditMode);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item == null){
                    item = new Item();
                    item.setName(etName.getText().toString());
                    item.setCategory(spinCategory.getSelectedItem().toString());
                    item.setAddress(etAddress.getText().toString());
                    item.setRate(rbRate.getRating());
                    item.setNotes(etNotes.getText().toString());
                    dbAccess.insert(item);
                }else {
                    item.setName(etName.getText().toString());
                    item.setCategory(spinCategory.getSelectedItem().toString());
                    item.setAddress(etAddress.getText().toString());
                    item.setRate(rbRate.getRating());
                    item.setNotes(etNotes.getText().toString());
                    dbAccess.update(item);
                }
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void loadItem(boolean isNewItem, Intent intent){
        //if an item is selected from the list it's information should be loaded here
        if (!isNewItem){
            int groupPosition = intent.getExtras().getInt("groupPosition");
            int childPosition = intent.getExtras().getInt("childPosition");
            item = myApp.listItem.get(myApp.listGroup.get(groupPosition)).get(childPosition);

            etName.setText(item.getName());
            spinCategory.setSelection(groupPosition);
            etAddress.setText(item.getAddress());
            rbRate.setRating(item.getRate());
            etNotes.setText(item.getNotes());
        }else {
            //
        }
    }

    private void executionMode(boolean isEditMode){
        if (isEditMode){
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            btnEditPicture.setVisibility(View.VISIBLE);
            etName.setEnabled(true);
            spinCategory.setEnabled(true);
            etAddress.setEnabled(true);
            rbRate.setEnabled(true);
            etNotes.setEnabled(true);
        }else {
            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnEditPicture.setVisibility(View.GONE);
            etName.setEnabled(false);
            spinCategory.setEnabled(false);
            etAddress.setEnabled(false);
            rbRate.setEnabled(false);
            etNotes.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        if (bp != null){
            ivItemImg.setImageBitmap(bp);
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
