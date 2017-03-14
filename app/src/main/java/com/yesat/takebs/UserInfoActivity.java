package com.yesat.takebs;

import android.content.Intent;
import android.renderscript.Double2;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yesat.takebs.Fragment.ProfileFragment;
import com.yesat.takebs.support.CommentClass;
import com.yesat.takebs.support.Route;
import com.yesat.takebs.support.User;

import org.w3c.dom.Comment;

import java.text.DecimalFormat;

public class UserInfoActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String uid = getIntent().getStringExtra("uid");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                UserInfoActivity.this.setTitle(user.username);
                ((TextView) findViewById(R.id.pro_email)).setText(user.email);
                ((TextView) findViewById(R.id.pro_dob)).setText(user.date);
                ((TextView) findViewById(R.id.pro_coun)).setText(user.country);
                ((TextView) findViewById(R.id.pro_city)).setText(user.city);

                ImageView imageView = (ImageView) findViewById(R.id.pro_ava);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReferenceFromUrl(user.profileImage);
                try {
                    Glide.with(UserInfoActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .into(imageView);
                }catch (Exception ex){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("comments").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int sum = 0;
                int n = 0;
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    String rate = dataSnapshot1.getValue(CommentClass.class).Rate;
                    sum += Integer.parseInt(rate);
                    n++;

                }
                if(n == 0)((TextView) findViewById(R.id.tv_rate)).setText("");
                else ((TextView) findViewById(R.id.tv_rate)).setText(sum/n+"/5");
                //double rate = 1.0*sum/n;
                //((TextView) findViewById(R.id.tv_rate)).setText(new DecimalFormat("#.#").format(rate)+"/5");
                ((TextView) findViewById(R.id.comm)).setText("("+n+") Show Reviews");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        findViewById(R.id.comm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserInfoActivity.this, CommentActivity.class);
                i.putExtra("uid",uid);
                startActivityForResult(i,37);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return false;
    }
}
