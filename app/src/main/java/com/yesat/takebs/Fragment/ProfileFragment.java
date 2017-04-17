package com.yesat.takebs.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yesat.takebs.MainActivity;
import com.yesat.takebs.MyRouteActivity;
import com.yesat.takebs.R;
import com.yesat.takebs.SettingActivity;
import com.yesat.takebs.support.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private static final String TAG = "yernar";
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        v.findViewById(R.id.btn_log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                getActivity().finish();
            }
        });
        v.findViewById(R.id.btn_my_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MyRouteActivity.class);
                startActivity(i);
            }
        });

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                ((TextView) v.findViewById(R.id.pro_name)).setText(u.username);
                ((TextView) v.findViewById(R.id.pro_about)).setText(u.aboutYourSelf);
                ((TextView) v.findViewById(R.id.pro_email)).setText(u.email);
                ((TextView) v.findViewById(R.id.pro_dob)).setText(u.date);
                ((TextView) v.findViewById(R.id.pro_coun)).setText(u.country);
                ((TextView) v.findViewById(R.id.pro_city)).setText(u.city);




                ImageView imageView = (ImageView) v.findViewById(R.id.pro_ava);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Log.d(TAG,u.profileImage);
                StorageReference storageReference = storage.getReferenceFromUrl(u.profileImage);
                try {
                    Glide.with(ProfileFragment.this)
                            .using(new FirebaseImageLoader())
                            .load(storageReference)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(imageView);
                }catch (Exception ex){
                    Log.d(TAG,"errpr");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_setting, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            Intent i = new Intent(getActivity(), SettingActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
