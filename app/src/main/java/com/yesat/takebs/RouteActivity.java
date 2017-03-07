package com.yesat.takebs;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yesat.takebs.support.Route;

public class RouteActivity extends AppCompatActivity {

    private Route route;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        route = (Route) getIntent().getSerializableExtra("route");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        ((TextView)findViewById(R.id.pro_name)).setText(route.username);
        ((TextView)findViewById(R.id.big_to_from)).setText(route.fromCity+", "+route.fromCountry+" - "+
                                                            route.toCity+", "+route.toCountry);
        ((TextView)findViewById(R.id.big_date)).setText(route.date);
        ((TextView)findViewById(R.id.big_time)).setText(route.time);
        ((TextView)findViewById(R.id.big_delivery_method)).setText(route.deliveryMethod);
        ((TextView)findViewById(R.id.big_tel)).setText(route.contactNumber);
        ((TextView)findViewById(R.id.big_cost)).setText(route.shippingCost);
        ((TextView)findViewById(R.id.big_extra)).setText(route.note);

        ImageView imageView = (ImageView) findViewById(R.id.big_ava);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(route.url);
        Glide.with(RouteActivity.this)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(imageView);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());


                        builder.setTitle("Respond");
                        //builder.setView(editText);
                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Sent", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
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
