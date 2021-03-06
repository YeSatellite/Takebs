package com.yesat.takebs.Fragment;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yesat.takebs.R;
import com.yesat.takebs.RouteActivity;
import com.yesat.takebs.support.Route;

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
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private String filter = "";
    private SearchView searchView;

    public RoutesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_routes, container, false);

        storage = FirebaseStorage.getInstance();

        routes = new ArrayList<>();
        routeAdapter = new RouteAdapter(getActivity(),routes);
        listview = (ListView) v.findViewById(R.id.rout_listviewa);
        listview.setAdapter(routeAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mDatabase.child("routes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routes.clear();
                for (DataSnapshot psUser: dataSnapshot.getChildren()) {
                    for (DataSnapshot psRoute: psUser.getChildren()) {
                        Route r = psRoute.getValue(Route.class);
                        if(r.have(filter))routes.add(r);
                        Log.d(TAG,"asdf "+filter);
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
                i.putExtra("fragment","routes");
                startActivity(i);
            }
        });

        mySwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_layout);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDatabase.child("routes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        routes.clear();
                        for (DataSnapshot psUser: dataSnapshot.getChildren()) {
                            for (DataSnapshot psRoute: psUser.getChildren()) {
                                Route r = psRoute.getValue(Route.class);
                                if(r.have(filter))routes.add(r);
                                Log.d(TAG,"asdf "+filter);
                            }
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


        return v;
    }

    public void filter(String query) {
        filter = query.toLowerCase();
        mDatabase.child("routes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                routes.clear();
                for (DataSnapshot psUser: dataSnapshot.getChildren()) {
                    for (DataSnapshot psRoute: psUser.getChildren()) {
                        Route r = psRoute.getValue(Route.class);
                        if(r.have(filter))routes.add(r);
                        Log.d(TAG,"asdf "+filter);
                    }
                }
                routeAdapter.notifyDataSetChanged();
                mySwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            ((TextView)convertView.findViewById(R.id.ro_username)).setText(route.username);
            ((TextView)convertView.findViewById(R.id.ro_from)).setText(route.fromCity);
            ((TextView)convertView.findViewById(R.id.ro_to)).setText(route.toCity);
            ((TextView)convertView.findViewById(R.id.ro_date)).setText(route.date);
            ((TextView)convertView.findViewById(R.id.ro_time)).setText(route.time);
            ((ImageView) convertView.findViewById(R.id.im_select))
                    .setImageResource((route.selectMethod.equals("2"))?R.drawable.man_black:R.drawable.package_black);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.ro_ava);
            try {
                StorageReference storageReference = storage.getReferenceFromUrl(route.url);
                Glide.with(RoutesFragment.this)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageView);
            }catch (Exception ex){}
            return convertView;
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG,newText);
                filter(newText);
                return true;
            }
        });

    }
}
