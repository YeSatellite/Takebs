package com.yesat.takebs.Fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.yesat.takebs.R;
import com.yesat.takebs.RouteActivity;
import com.yesat.takebs.support.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouritesFragment extends Fragment {


    private static final String TAG = "yernar";
    private DatabaseReference mDatabase;
    private ListView listview;
    private RouteAdapter routeAdapter;
    private ArrayList<Route> routes;
    private ArrayList<String> keys;
    private FirebaseStorage storage;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_routes, container, false);

        storage = FirebaseStorage.getInstance();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        routes = new ArrayList<>();
        keys = new ArrayList<>();

        routeAdapter = new RouteAdapter(getActivity(),routes);
        listview = (ListView) v.findViewById(R.id.rout_listviewa);
        Log.d(TAG, "is null:"+ (listview == null));
        listview.setAdapter(routeAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("favourites").
                child(mAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
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

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), RouteActivity.class);
                i.putExtra("route",routes.get(position));
                i.putExtra("fragment","favourites");
                startActivity(i);
            }
        });
        mySwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_layout);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDatabase.child("favourites").
                        child(mAuth.getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                routes.clear();
                                keys.clear();
                                for (DataSnapshot psRoute: dataSnapshot.getChildren()) {
                                    routes.add(psRoute.getValue(Route.class));
                                    keys.add(psRoute.getKey());
                                }
                                routeAdapter.notifyDataSetChanged();
                                mySwipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
        mySwipeRefreshLayout.setColorSchemeResources(
                android.R.color.black,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Delete this Route")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String key = keys.get(position);
                                Log.d(TAG,key);
                                mDatabase.child("favourites").
                                        child(mAuth.getCurrentUser().getUid()).child(key).removeValue();
                            }
                        })
                        .setNegativeButton("Cancel",null);
                builder.show();
                return true;
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
            ((TextView)convertView.findViewById(R.id.ro_from)).setText(route.fromCity);
            ((TextView)convertView.findViewById(R.id.ro_to)).setText(route.toCity);
            ((TextView)convertView.findViewById(R.id.ro_date)).setText(route.date);
            ((TextView)convertView.findViewById(R.id.ro_time)).setText(route.time);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.ro_ava);
            imageView.setVisibility(View.GONE);
            return convertView;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
