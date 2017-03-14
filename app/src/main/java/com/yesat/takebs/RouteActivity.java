package com.yesat.takebs;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yesat.takebs.support.Chat;
import com.yesat.takebs.support.Route;

public class RouteActivity extends AppCompatActivity {

    private static final String TAG = "yernar";
    private Route route;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String parrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        route = (Route) getIntent().getSerializableExtra("route");
        parrent = getIntent().getStringExtra("fragment");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        ((TextView)findViewById(R.id.pro_name)).setText(route.username);
        ((TextView)findViewById(R.id.big_to_from)).setText(route.fromCity+" - "+route.toCity);
        ((TextView)findViewById(R.id.big_date)).setText(route.date);
        ((TextView)findViewById(R.id.big_time)).setText(route.time);
        ((TextView)findViewById(R.id.big_delivery_method)).setText(route.deliveryMethod);
        ((TextView)findViewById(R.id.big_tel)).setText(route.contactNumber);
        ((TextView)findViewById(R.id.big_cost)).setText(route.shippingCost);
        ((TextView)findViewById(R.id.big_extra)).setText(route.note);

        MyListener listener = new MyListener();

        ImageView imageView = (ImageView) findViewById(R.id.big_ava);
        imageView.setOnClickListener(listener);
        findViewById(R.id.pro_name).setOnClickListener(listener);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        imageView.setImageResource(R.drawable.avatar);
        try {
            StorageReference storageReference = storage.getReferenceFromUrl(route.url);
            Glide.with(RouteActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .skipMemoryCache(true)
                    .into(imageView);
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        findViewById(R.id.act_route_add_to_favo).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.child("favourites").child(mAuth.getCurrentUser().getUid()).push().setValue(route);
                    }
                });

        findViewById(R.id.act_route_respond).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RouteActivity.this, R.style.myDialog));
                        final EditText input = new EditText(RouteActivity.this);
                        input.setHint("Text something...");
                        builder.setView(input);
                        builder.setTitle("Sent Message");
                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Sent", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String myUid = mAuth.getCurrentUser().getUid();
                                String target_uid = route.uid;
                                String text = input.getText().toString();
                                double time = System.currentTimeMillis()/1000.0;
                                DatabaseReference ref = mDatabase.child("user-messages")
                                .child(myUid).child(target_uid).push();
                                ref.setValue(1);
                                mDatabase.child("user-messages")
                                .child(target_uid).child(myUid).child(ref.getKey())
                                .setValue(1);
                                Chat chat = new Chat(myUid,text,time,target_uid);
                                mDatabase.child("message").child(ref.getKey()).setValue(chat);
                                }
                        });
                        builder.show();
                    }
                });

        if(parrent.equals("favourites"))
            findViewById(R.id.act_route_add_to_favo).setVisibility(View.GONE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return false;
    }
    public class MyListener implements  View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent i = new Intent(RouteActivity.this, UserInfoActivity.class);
            i.putExtra("uid",route.uid);
            startActivityForResult(i,37);
        }
    }
}
