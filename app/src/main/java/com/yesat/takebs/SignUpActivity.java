package com.yesat.takebs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yesat.yesat.takebs.support.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {
    private static final int LOAD_IMAGE_RESULTS = 10;
    private static final int SAVE_ID = 1037;
    private static final String TAG = "yernar";
    private ImageView ava;
    private TextView email;
    private TextView pass;
    private TextView pass2;
    private TextView user;
    private TextView coun;
    private TextView city;
    private TextView data;
    private TextView about;
    private TextView team;
    private Button singUp;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // -----------------------------------------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        // ----------------------------------------------------------
        ava = (ImageView) findViewById(R.id.iv_ava);
        email = (TextView)findViewById(R.id.to_city);
        pass = (TextView)findViewById(R.id.su_pass);
        pass2 = (TextView)findViewById(R.id.su_pass2);
        user = (TextView)findViewById(R.id.su_user);
        coun = (TextView)findViewById(R.id.su_coun);
        city = (TextView)findViewById(R.id.su_city);
        data = (TextView)findViewById(R.id.su_data);
        about = (TextView)findViewById(R.id.about);
        team = (TextView)findViewById(R.id.team);
        singUp = (Button) findViewById(R.id.bt_sign_up);
        coun.setError(null);

        // -----------------------------------------------------------------------------------------
        ava.setImageResource(R.drawable.avatar);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                final DatePicker picker = new DatePicker(SignUpActivity.this);

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
        team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // team
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                TextView text = new TextView(SignUpActivity.this);
                text.setTextSize(20);
                text.setPadding(10,10,10,10);
                text.setGravity(Gravity.CENTER);
                text.setText("Bla bla bla bla bla d fdfds sddfasd df dd fa gdf d adadaf dagadfgad gadgfdgad fgdaagfg dgfghfg ffadffafg");
                builder.setView(text);
                builder.setPositiveButton("OK",null);
                builder.show();
            }
        });
        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        // -----------------------------------------------------------------------------------------
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(email.getText().length()==0 && !hasFocus){
                    email.setError("bla bla");
                }
            }
        });
        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(pass.getText().length()==0 && !hasFocus){
                    pass.setError("bla bla");
                }
            }
        });
        pass2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!pass2.getText().toString().
                        equals(pass.getText().toString())&&
                        !hasFocus){
                    pass2.setError("bla bla");
                }
            }
        });
        user.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(user.getText().length()==0 && !hasFocus){
                    user.setError("bla bla");
                }
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
                Bitmap  mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                ava.setImageBitmap(mBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    private void signUp(){
        if(pass2.getError() != null){
            Toast.makeText(this, pass2.getError(),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(user.getError() != null){
            Toast.makeText(this, user.getError(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String email_s  = email.getText().toString();
        String pass_s = pass.getText().toString();
        mAuth.createUserWithEmailAndPassword(email_s, pass_s)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()){
                            Log.d(TAG, mAuth.getCurrentUser().getUid());
                            saveUser(mAuth.getCurrentUser().getUid());
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUser(final String uid){

        final String oneSignalId = "null";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://takebs-399c1.appspot.com");
        StorageReference mountainsRef = storageRef.child(uid);

        ava.setDrawingCacheEnabled(true);
        ava.buildDrawingCache();
        Bitmap bitmap = ava.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] d = baos.toByteArray();
        UploadTask uploadTask = mountainsRef.putBytes(d);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri uri = taskSnapshot.getDownloadUrl();

                Log.d(TAG,uri.toString());

                User u = new User(
                        about.getText().toString(),
                        city.getText().toString(),
                        coun.getText().toString(),
                        data.getText().toString(),
                        email.getText().toString(),
                        oneSignalId,
                        uri.toString(),
                        user.getText().toString()
                );
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(uid).setValue(u);
                finish();
            }
        });



    }
}
