package com.yesat.takebs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.onesignal.OneSignal;
import com.yesat.takebs.support.Chat;
import com.yesat.takebs.support.ChatPerson;
import com.yesat.takebs.support.Route;
import com.yesat.takebs.support.Route2;
import com.yesat.takebs.support.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RouteActivity extends AppCompatActivity {

    private static final String TAG = "yernar";
    private Route route;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String parrent;
    private TextView fromTo;
    private ImageView imageView;

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
        fromTo = (TextView)findViewById(R.id.big_to_from);
        fromTo.setText(route.fromCity+" - "+route.toCity);
        ((TextView)findViewById(R.id.big_date)).setText(route.date);
        ((TextView)findViewById(R.id.big_time)).setText(route.time);
        ((TextView)findViewById(R.id.big_delivery_method)).setText(route.deliveryMethod);
        ((TextView)findViewById(R.id.big_tel)).setText(route.contactNumber);
        ((TextView)findViewById(R.id.big_cost)).setText(route.shippingCost);
        ((TextView)findViewById(R.id.big_extra)).setText(route.note);

        MyListener listener = new MyListener();

        imageView = (ImageView) findViewById(R.id.big_ava);
        imageView.setOnClickListener(listener);
        findViewById(R.id.pro_name).setOnClickListener(listener);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        imageView.setImageResource(R.drawable.avatar);
        try {
            StorageReference storageReference = storage.getReferenceFromUrl(route.url);
            Glide.with(RouteActivity.this)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
        }catch (Exception ex){
            Log.e(TAG,ex.getMessage());
        }
        findViewById(R.id.act_route_add_to_favo).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Route2 r = route.toRoute2();
                        mDatabase.child("favourites").child(mAuth.getCurrentUser().getUid()).push().setValue(r);
                        AlertDialog.Builder builder = new AlertDialog.Builder(RouteActivity.this);
                        builder.setMessage("Route added to favourites")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        builder.show();
                    }
                });

        findViewById(R.id.act_route_respond).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RouteActivity.this, R.style.myDialog));
                        final EditText input = new EditText(RouteActivity.this);
                        input.setHint("Text something...");
                        TableLayout.LayoutParams params = new TableLayout.LayoutParams();
                        input.layout(20,0,20,0);
                        builder.setView(input,50,0,50,0);
                        builder.setTitle("Sent Message");
                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Sent", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String myUid = mAuth.getCurrentUser().getUid();
                                String target_uid = route.uid;
                                final String text = input.getText().toString();
                                double time = System.currentTimeMillis()/1000.0;
                                DatabaseReference ref = mDatabase.child("user-messages")
                                .child(myUid).child(target_uid).push();
                                ref.setValue(1);
                                mDatabase.child("user-messages")
                                .child(target_uid).child(myUid).child(ref.getKey())
                                .setValue(1);
                                Chat chat = new Chat(myUid,text,time,target_uid);
                                mDatabase.child("message").child(ref.getKey()).setValue(chat);
                                Intent i = new Intent(RouteActivity.this, ChatActivity.class);
                                i.putExtra("chat",new ChatPerson(target_uid,""));
                                startActivity(i);
                                mDatabase.child("users").child(target_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        try {
                                            OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + text + "'}, 'include_player_ids': ['" + user.oneSignalId + "']}"), null);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
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
        if(item.getItemId()==R.id.item_share){
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, fromTo.getText().toString()+"\nwww.takebs.com");
            startActivity(Intent.createChooser(share, "Takebs"));
            return true;
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        return true;
    }
}
