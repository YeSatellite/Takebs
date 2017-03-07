package com.yesat.takebs.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yesat.yesat.takebs.R;
import com.yesat.yesat.takebs.RouteActivity;
import com.yesat.yesat.takebs.support.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoutesFragment extends Fragment {
    private static final String TAG = "yernar";
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private ListView listview;
    private RouteAdapter routeAdapter;
    private ArrayList<Route> routes;
    private FirebaseStorage storage;

    public RoutesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_routes, container, false);

        storage = FirebaseStorage.getInstance();

        routes = new ArrayList<>();
        routeAdapter = new RouteAdapter(getActivity(),routes);
        listview = (ListView) v.findViewById(R.id.rout_listviewa);
        Log.d(TAG, "is null:"+ (listview == null));
        listview.setAdapter(routeAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mDatabase.child("routes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routes.clear();
                for (DataSnapshot psUser: dataSnapshot.getChildren()) {
                    for (DataSnapshot psRoute: psUser.getChildren()) {
                        routes.add(psRoute.getValue(Route.class));
                    }
                }
                routeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), RouteActivity.class);
                i.putExtra("route",routes.get(position));
                startActivity(i);
            }
        });

        return v;
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
            ((TextView)convertView.findViewById(R.id.ro_username)).setText(route.username);
            ((TextView)convertView.findViewById(R.id.ro_from)).setText(route.fromCity+", "+route.fromCountry);
            ((TextView)convertView.findViewById(R.id.ro_to)).setText(route.toCity+", "+route.toCountry);
            ((TextView)convertView.findViewById(R.id.ro_date)).setText(route.date);
            ((TextView)convertView.findViewById(R.id.ro_time)).setText(route.time);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.ro_ava);
            
            StorageReference storageReference = storage.getReferenceFromUrl(route.url);
            Glide.with(RoutesFragment.this)
                    .using(new FirebaseImageLoader())
                    .load(storageReference)
                    .into(imageView);


            return convertView;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
