package everton.urate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;


// added byc ACM 11/19/14
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ItemDetailActivity extends FragmentActivity {
    private MyApplication myApp;
    private DbAccess dbAccess;
    private Item item;
    private Item itemCopy;
    private boolean isEditMode;
    private boolean isImgChanged = false;

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

    // added by ACM on 11/19/14
    private ImageButton ibMapView;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


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

        // added by ACM on 11/19/14
        ibMapView = (ImageButton) findViewById(R.id.ib_map);

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
                itemCopy = item;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item == null){
                    //If it's a new item, insert it to the db
                    String fileName = UUID.randomUUID().toString().replace("-", "_");
                    item = new Item();
                    item.setName(etName.getText().toString());
                    item.setCategory(spinCategory.getSelectedItem().toString());
                    item.setAddress(etAddress.getText().toString());
                    item.setRate(rbRate.getRating());
                    item.setNotes(etNotes.getText().toString());
                    item.setFileName(fileName);
                    dbAccess.insert(item);
                    if (isImgChanged == true){
                        saveImageToInternalStorage(((BitmapDrawable)ivItemImg.getDrawable()).getBitmap(), fileName);
                    }
                }else {
                    //If it's an old item, update it in the db
                    item.setName(etName.getText().toString());
                    item.setCategory(spinCategory.getSelectedItem().toString());
                    item.setAddress(etAddress.getText().toString());
                    item.setRate(rbRate.getRating());
                    item.setNotes(etNotes.getText().toString());
                    dbAccess.update(item);
                    if (isImgChanged == true){
                        saveImageToInternalStorage(((BitmapDrawable)ivItemImg.getDrawable()).getBitmap(), item.getFileName());
                    }
                }
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = false;
                executionMode(isEditMode);
                item = itemCopy;
                etName.setText(item.getName());
                spinCategory.setSelection(myApp.listGroup.indexOf(item.getCategory()));
                etAddress.setText(item.getAddress());
                rbRate.setRating(item.getRate());
                etNotes.setText(item.getNotes());

            }
        });

        btnEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        ibMapView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailActivity.this, Map.class);
                startActivity(intent);
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
            Bitmap image = getThumbnail(item.getFileName());
            if (image != null){
                ivItemImg.setImageBitmap(image);
            }
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

    public boolean saveImageToInternalStorage(Bitmap image, String fileName) {
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("saveToInternalStorage()", e.getMessage());
            return false;
        }
    }

    public Bitmap getThumbnail(String filename) {
        Bitmap thumbnail = null;
        try {
            File filePath = getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            Log.e("getThumbnail() on internal storage", ex.getMessage());
        }
        return thumbnail;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            Bundle bundle = data.getExtras();
            if (bundle != null){
                Bitmap img = (Bitmap) bundle.get("data");
                ivItemImg.setImageBitmap(img);
                isImgChanged = true;
            }
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
