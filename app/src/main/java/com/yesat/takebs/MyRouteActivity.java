package com.yesat.takebs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yesat.takebs.Fragment.RoutesFragment;
import com.yesat.takebs.support.Route;

import java.util.ArrayList;
import java.util.List;

public class MyRouteActivity extends AppCompatActivity {

    private static final String TAG = "yernar";
    private FirebaseStorage storage;
    private ArrayList<Route> routes;
    private RouteAdapter routeAdapter;
    private ListView listview;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private FirebaseUser mUser;
    private ArrayList<String> keys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();

        routes = new ArrayList<>();
        keys = new ArrayList<>();
        routeAdapter = new RouteAdapter(this,routes);
        listview = (ListView) findViewById(R.id.rout_listviewa);
        listview.setAdapter(routeAdapter);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mDatabase.child("routes").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routes.clear();
                keys.clear();
                for (DataSnapshot psRoute: dataSnapshot.getChildren()) {
                    routes.add(psRoute.getValue(Route.class));
                    keys.add(psRoute.getKey());
                }
                routeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyRouteActivity.this);
                builder.setMessage("Delete this route")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String key = keys.get(position);
                                Log.d(TAG,key);
                                mDatabase.child("routes").child(mUser.getUid()).child(key).removeValue();
                            }
                        })
                        .setNegativeButton("Cancel",null);
                builder.show();
                return true;
            }
        });

    }
    public class RouteAdapter extends ArrayAdapter<Route> {
        public RouteAdapter(Context context, List<Route> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Route route = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_route, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.ro_username)).setText(route.username);
            ((TextView) convertView.findViewById(R.id.ro_from)).setText(route.fromCity);
            ((TextView) convertView.findViewById(R.id.ro_to)).setText(route.toCity);
            ((TextView) convertView.findViewById(R.id.ro_date)).setText(route.date);
            ((TextView) convertView.findViewById(R.id.ro_time)).setText(route.time);
            ((ImageView) convertView.findViewById(R.id.im_select))
                    .setImageResource((route.selectMethod.equals("2")) ? R.drawable.man_black : R.drawable.package_black);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ro_ava);
            try {
                StorageReference storageReference = storage.getReferenceFromUrl(route.url);
                Glide.with(MyRouteActivity.this)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
            } catch (Exception ex) {
            }
            return convertView;
        }
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
