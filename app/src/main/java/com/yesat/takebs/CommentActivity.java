package com.yesat.takebs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.yesat.takebs.Fragment.AddRouteFragment;
import com.yesat.takebs.Fragment.RoutesFragment;
import com.yesat.takebs.support.CommentClass;
import com.yesat.takebs.support.Route;
import com.yesat.takebs.support.User;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<CommentClass> comments;
    private CommAdapter commAdapter;
    private ListView listview;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uid = getIntent().getStringExtra("uid");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        comments = new ArrayList<>();
        commAdapter = new CommAdapter(this,comments);
        listview = (ListView) findViewById(R.id.comm_list);
        listview.setAdapter(commAdapter);



        mDatabase.child("comments").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    CommentClass comm = dataSnapshot1.getValue(CommentClass.class);
                    comments.add(comm);
                }
                commAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class CommAdapter extends ArrayAdapter<CommentClass> {
        public CommAdapter(Context context, List<CommentClass> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            CommentClass comm = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comm, parent, false);
            }

            ((TextView)convertView.findViewById(R.id.username)).setText(comm.username);
            ((TextView)convertView.findViewById(R.id.comment)).setText(comm.Comment);
            ((RatingBar)convertView.findViewById(R.id.ratingBar)).setRating(Float.parseFloat(comm.Rate));

            return convertView;
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_comment, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_comm) {
            Intent i = new Intent(CommentActivity.this, AddCommActivity.class);
            startActivityForResult(i,37);
            return true;
        }
        if(item.getItemId()==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (37) : {
                if (resultCode == Activity.RESULT_OK) {
                    final String rating = data.getStringExtra("rating");
                    final String comment = data.getStringExtra("comment");
                    mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            CommentClass cc = new CommentClass(comment,rating,user.username);
                            addComment(cc);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                break;
            }
        }
    }

    private void addComment(final CommentClass cc) {
        mDatabase.child("comments").child(uid).push().setValue(cc);
            mDatabase.child("Average").child(uid).child("average").
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Integer n;
                            try {
                                n = Integer.parseInt(dataSnapshot.getValue(String.class));
                            }catch (Exception ex){
                                n = 0;
                            }
                            dataSnapshot.getRef().setValue(n + Integer.parseInt(cc.Rate) + "");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
    }
}
