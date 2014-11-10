package everton.urate;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ItemDetailActivity extends Activity {
   /* private Button btnSave;
    private Button btnEdit;
    private Button btnCancel;

    private EditText etName;
    private Spinner spinCategory;
    private EditText etAddress;
    private RatingBar rbRate;
    private EditText etNotes;*/




    // Added by ACM
    private static String logtag = "CameraApp";
    private final static int TAKE_PICTURE = 1;
    private Uri imageUri;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
/*

        btnSave = (Button) findViewById(R.id.btn_save);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        etName = (EditText) findViewById(R.id.et_name_item);
        spinCategory =  (Spinner) findViewById(R.id.spin_category);
        etAddress = (EditText) findViewById(R.id.et_address);
        rbRate = (RatingBar) findViewById(R.id.rb_rate);
        etNotes = (EditText) findViewById(R.id.et_notes);
*/
        //Added by ACM
        ImageView cameraButton;
        //added by ACM
        cameraButton = (ImageView) findViewById(R.id.iv_item_detail);
        cameraButton.setOnClickListener( new View.OnClickListener(){
            @Override
          public void  onClick(View v) {
            takePhoto(v);
        }// end onclick
    });

/*
        //Intent intent = getIntent();
        // boolean editMode = intent.getExtras().getBoolean("editMode");
        //executionMode(editMode);
        private View.OnClickListener cameraListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto(v);
            }// end onclick
        };*/
    }





    public void takePhoto(View v)
    {
        String timeStamp = new SimpleDateFormat("dd.HH.mm.ss").format(new Date());
        String picname = timeStamp + "uRate_Image";
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),picname+".jpeg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent, TAKE_PICTURE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);


        // does the user think it's OK to use the camera
        if(resultCode == Activity.RESULT_OK){
            Uri selectImage =  imageUri;
            getContentResolver().notifyChange(selectImage,null);
            // get image for the viewer
            ImageView imageView = (ImageView)findViewById(R.id.iv_item_detail);
            // get bitmap to hold image data
            ContentResolver cr = getContentResolver();

            Bitmap bitmap;
            try{

                // Create the bitMap
                bitmap = MediaStore.Images.Media.getBitmap(cr, selectImage);
                //BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                imageView.setImageBitmap(bitmap);


                Toast.makeText(ItemDetailActivity.this, selectImage.toString(), Toast.LENGTH_LONG).show();



            }catch (Exception e){
                Log.e(logtag, e.toString());
            }
        }
    }

/*

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
    }*/

}
