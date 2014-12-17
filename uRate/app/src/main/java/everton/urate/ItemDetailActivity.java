package everton.urate;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

// added byc ACM 11/19/14


public class ItemDetailActivity extends FragmentActivity {
    private MyApplication myApp;
    private DbAccess dbAccess;
    private Item item;
    private Item itemCopy;
    private boolean isEditMode;
    private boolean isNewItem; // added ptr 12/15
    private boolean isImgChanged = false;
    private boolean isLocChanged = false;
    private Button btnSave;
    private Button btnEdit;
    private Button btnCancel;
    private Button btnNewCancel; // Added pTR 12/15 Cancel btn for new items
    private EditText etName;
    private Spinner spinCategory;
    private EditText etAddress;
    private RatingBar rbRate;
    private EditText etNotes;
    private ImageView ivItemImg;
    private ImageView ivEditImg;

    // added by ACM on 11/19/14
    private ImageButton ibMapView;
    private ImageButton ibNewCategory;
    private EditText latituteField;
    private EditText longitudeField;
//    private LocationManager locationManager;
//    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayShowTitleEnabled(false);

        setContentView(R.layout.activity_item_detail);

        //acm12/14
        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        //Criteria criteria = new Criteria();
       // provider = locationManager.getBestProvider(criteria, false);
        //Location location = locationManager.getLastKnownLocation(provider);
        //if (location != null) {
          //  System.out.println("Provider " + provider + " has been selected.");
            //onLocationChanged(location);
        ///}

        myApp = (MyApplication) getApplication();
        dbAccess = new DbAccess(this);

        btnSave = (Button) findViewById(R.id.btn_save);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        //Added PTR 12/15 Cancel for new items
        btnNewCancel = (Button) findViewById(R.id.btn_new_cancel);
        etName = (EditText) findViewById(R.id.et_name_item);
        spinCategory =  (Spinner) findViewById(R.id.spin_category);
        etAddress = (EditText) findViewById(R.id.et_address);
        rbRate = (RatingBar) findViewById(R.id.rb_rate);
        etNotes = (EditText) findViewById(R.id.et_notes);
        ivItemImg = (ImageView) findViewById(R.id.iv_item_img);
        // added by PTR on 12/05/14 adds edit icon to ivItemImg
        ivEditImg = (ImageView) findViewById(R.id.iv_edit_img);

        // added by ACM on 11/19/14
        ibMapView = (ImageButton) findViewById(R.id.ib_map);
        ibNewCategory = (ImageButton) findViewById(R.id.ib_new_cat);
        longitudeField = (EditText) findViewById(R.id.long_line);
        latituteField = (EditText) findViewById(R.id.lat_line);
        //removed by PTR 12/05/14 added listener to ivItemImg
//        btnEditPicture = new FloatingActionButton.Builder(this)
//                .withDrawable(getResources().getDrawable(R.drawable.ic_action_edit_w))
//                .withButtonColor(Color.parseColor("#FF0099"))
//                .withGravity(Gravity.TOP | Gravity.RIGHT)
//                .withMargins(0, 80, 15, 0)
//                .create();

        ArrayAdapter<String> adapter =  new ArrayAdapter(this, android.R.layout.simple_spinner_item, myApp.listGroup);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(adapter);

        Intent intent = getIntent();
        isEditMode = intent.getExtras().getBoolean("isEditMode");
        isNewItem = intent.getExtras().getBoolean("isNewItem");
        loadItem(isNewItem, intent);
        executionMode(isEditMode,isNewItem);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = true;
                isNewItem = false;
                executionMode(isEditMode, isNewItem);
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
                    // added ACM 12/15/ to add latitude and longitude to database
                    item.setLat((latituteField.getText()).toString());
                    item.setLng(longitudeField.getText().toString());

                    dbAccess.insert(item);
                    if (isImgChanged == true){
                        saveImageToInternalStorage(((BitmapDrawable)ivItemImg.getDrawable()).getBitmap(), fileName);
                    }
                    if (isLocChanged == true)
                        item.setAddress(etAddress.getText().toString());

                }else {
                    //If it's an old item, update it in the db
                    item.setName(etName.getText().toString());
                    item.setCategory(spinCategory.getSelectedItem().toString());
                    //   item.setAddress(etAddress.getText().toString());
                    item.setRate(rbRate.getRating());
                    item.setNotes(etNotes.getText().toString());
                    // added ACM 12/15/ to add latitude and longitude to database
                    item.setLat(latituteField.getText().toString());
                    item.setLng(longitudeField.getText().toString());

                    dbAccess.update(item);
                    if (isImgChanged == true){
                        saveImageToInternalStorage(((BitmapDrawable)ivItemImg.getDrawable()).getBitmap(), item.getFileName());
                    }
                    if (isLocChanged == true)
                        item.setAddress(etAddress.getText().toString());
                    if (latituteField.getText() != null && longitudeField.getText() != null)
                    {
                        item.setLat((latituteField.getText().toString()));
                        item.setLng((longitudeField.getText().toString()));
                    }
                }
                finish();
            }
        });
        // Cancels out of edit mode. Only viewable in edit mode.
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = false;
                isNewItem = false;
                executionMode(isEditMode,isNewItem);
                item = itemCopy;
                etName.setText(item.getName());
                spinCategory.setSelection(myApp.listGroup.indexOf(item.getCategory()));
                etAddress.setText(item.getAddress());
                rbRate.setRating(item.getRate());
                etNotes.setText(item.getNotes());
                // added ACM 12/15/ to add latitude and longitude to database
                latituteField.setText(String.valueOf(item.getLat()));
                longitudeField.setText(String.valueOf(item.getLng()));
            }
        });
        // Added PTR 12/15 Cancel button for new items
        btnNewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditMode = false;
                isNewItem = false;
                finish();
            }
        });
        // In EditMode user can click image to add or edit the image
        ivItemImg.setOnClickListener(new View.OnClickListener(){
             @Override
             public void onClick(View v){
                 // if only allows allows Image Capture on click when in edit mode
                 if(isEditMode){
                     Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                     startActivityForResult(intent, 0);
                 }
             }
        });
        //removed by PTR 12/05/14 added listener to ivItemImg
//        btnEditPicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 0);
//            }
//        });

        ibMapView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    Intent intent = new Intent(ItemDetailActivity.this, MapGetLocationActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        ibNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEditMode){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemDetailActivity.this);
                    builder.setTitle("Crete new category");
                    final EditText input = new EditText(ItemDetailActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newCategory = input.getText().toString();
                            myApp.listGroup.add(newCategory);
                            ArrayAdapter<String> adapter =  new ArrayAdapter(ItemDetailActivity.this, android.R.layout.simple_spinner_item, myApp.listGroup);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinCategory.setAdapter(adapter);
                            spinCategory.setSelection(myApp.listGroup.indexOf(newCategory));
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
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
    // method determines what is displayed based on isEditMode
    private void executionMode(boolean isEditMode, boolean isNewItem){
        if (isEditMode){
            btnEdit.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
            if(isNewItem){
                btnNewCancel.setVisibility(View.VISIBLE);}
            else{
                btnCancel.setVisibility(View.VISIBLE);}
            ivEditImg.setVisibility(View.VISIBLE);
            ibNewCategory.setVisibility(View.VISIBLE);
            ibMapView.setVisibility(View.VISIBLE);
            etName.setEnabled(true);
            spinCategory.setEnabled(true);
            etAddress.setEnabled(true);
            rbRate.setEnabled(true);
            etNotes.setEnabled(true);
        }else {
            btnEdit.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            btnNewCancel.setVisibility(View.GONE);
            ivEditImg.setVisibility(View.GONE);
            ibNewCategory.setVisibility(View.GONE);
            ibMapView.setVisibility(View.GONE);
            etName.setEnabled(false);
            spinCategory.setEnabled(false);
            etAddress.setEnabled(false);
            rbRate.setEnabled(false);
            etNotes.setEnabled(false);
            // prevents soft keyboard from appearing when not editing
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
            switch (requestCode){

                case(1):{
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        etAddress.setText((CharSequence) bundle.get("address"));//
                        latituteField.setText(bundle.get("lat").toString());
                        longitudeField.setText(bundle.get("lng").toString());
                        isLocChanged = true;

                    }
                    break;
                }
                default:
                    Bundle bundle = data.getExtras();
                    if (bundle != null){

                        Bitmap img = (Bitmap) bundle.get("data");
                        ivItemImg.setImageBitmap(img);
                        isImgChanged = true;
                        break;
                    }
            }
        }


    }
}

