package everton.urate;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;
import java.lang.*;


public class ItemDetailActivity extends Activity {


    // adde by ACM
    private static String logtag = "CameraApp";
    private final static int TAKE_PICTURE = 1;
    private Uri imageUri;

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


        ImageButton cameraButton = (ImageButton) findViewById(R.id.iv_item_detail);
        cameraButton.setOnClickListener(cameraListener);

        Intent intent = getIntent();
        boolean editMode = intent.getExtras().getBoolean("editMode");
        executionMode(editMode);

    }

    private View.OnClickListener cameraListener = new View.OnClickListener() {

        public void onClick(View v) {
            takePhoto(v);
        }// end onclick
    };
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
                imageView.setImageBitmap(bitmap);
                Toast.makeText(ItemDetailActivity.this,selectImage.toString(),Toast.LENGTH_LONG).show();

            }catch (Exception e){
                Log.e(logtag, e.toString());
            }

        }

    }
    public void takePhoto(View v)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Picture1.jpg");
        File photo = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent, TAKE_PICTURE);
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


}
