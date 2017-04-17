package com.yesat.takebs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yesat.takebs.Fragment.ProfileFragment;
import com.yesat.takebs.R;
import com.yesat.takebs.support.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "yernar";
    private static final int LOAD_IMAGE_RESULTS = 54;
    private FirebaseAuth mAuth;
    private FirebaseStorage mStorage;
    private ImageView ava;
    private TextView coun;
    private TextView city;
    private TextView data;
    private EditText user;
    private EditText about;
    private DatabaseReference mDatabase;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // -----------------------------------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // ----------------------------------------------------------
        ava = (ImageView) findViewById(R.id.iv_ava);
        user = (EditText)findViewById(R.id.st_user);
        about = (EditText)findViewById(R.id.st_about);
        coun = (TextView)findViewById(R.id.st_coun);
        city = (TextView)findViewById(R.id.st_city);
        data = (TextView)findViewById(R.id.st_data);

        // -----------------------------------------------------------------------------------------
        ava.setImageResource(R.drawable.avatar);
//        try {
//            StorageReference storageReference = mStorage.getReferenceFromUrl();
//            Glide.with(SettingActivity.this)
//                    .using(new FirebaseImageLoader())
//                    .load(storageReference)
//                    .into(ava);
//        }catch (Exception ex){
//            Log.e(TAG,ex.getMessage());
//        }

        ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // upload ava
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                startActivityForResult(pickIntent, LOAD_IMAGE_RESULTS);
            }
        });
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // data
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                final DatePicker picker = new DatePicker(SettingActivity.this);

                builder.setTitle("Data of birth");
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(picker.getYear(),picker.getMonth(),picker.getDayOfMonth());
                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
                        data.setText(format.format(cal.getTime()));
                    }
                });
                builder.show();
            }
        });

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUser = dataSnapshot.getValue(User.class);
                        SettingActivity.this.user.setText(mUser.username);
                        coun.setText(mUser.country);
                        city.setText(mUser.city);
                        data.setText(mUser.date);
                        about.setText(mUser.aboutYourSelf);
                        StorageReference storageReference = mStorage.getReferenceFromUrl(mUser.profileImage);
                        try {
                            Glide.with(SettingActivity.this)
                                    .using(new FirebaseImageLoader())
                                    .load(storageReference)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(ava);
                        }catch (Exception ex){

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        findViewById(R.id.bt_res_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail());

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setMessage("Check your email")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_save) {
            if(mUser != null){
                saveUser();
            }
            return true;
        }
        if(item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveUser() {
        if(user.length()==0){
            user.getBackground().mutate().setColorFilter(getResources().getColor(R.color.your_color), PorterDuff.Mode.SRC_ATOP);
            return;
        }
        else{
            user.getBackground().mutate().setColorFilter(getResources().getColor(R.color.your_color2), PorterDuff.Mode.SRC_ATOP);
        }




        StorageReference mountainsRef = mStorage.getReference().child(mAuth.getCurrentUser().getUid());

        ava.setDrawingCacheEnabled(true);
        ava.buildDrawingCache();
        Bitmap bitmap = ava.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] d = baos.toByteArray();
        UploadTask uploadTask = mountainsRef.putBytes(d);
        final ProgressDialog pd = ProgressDialog.show(SettingActivity.this,"","",true);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri uri = taskSnapshot.getDownloadUrl();
                Log.d(TAG, uri.toString());

                mUser.username = user.getText().toString();
                mUser.aboutYourSelf = about.getText().toString();
                mUser.country = coun.getText().toString();
                mUser.date = data.getText().toString();
                mUser.city = city.getText().toString();
                mUser.profileImage = uri.toString();

                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(mUser);
                pd.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOAD_IMAGE_RESULTS && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            CropImage.activity(selectedImage)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setFixAspectRatio(true)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri uri = result.getUri();
            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ava.setImageBitmap(mBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
