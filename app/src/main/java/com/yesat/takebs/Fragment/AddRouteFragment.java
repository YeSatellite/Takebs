package com.yesat.takebs.Fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yesat.yesat.takebs.R;
import com.yesat.yesat.takebs.support.Route;
import com.yesat.yesat.takebs.support.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddRouteFragment extends Fragment {


    private static final String TAG = "yernar";
    private EditText vFromCity;
    private EditText vFromCount;
    private EditText vToCity;
    private EditText vToCount;
    private TextView vData;
    private TextView vTime;
    private EditText vCost;
    private EditText vContact;
    private ImageView vType_0;
    private ImageView vType_1;
    private ImageView vType_2;
    private ImageView vType_3;
    private ImageView vType_4;
    private EditText vAbout;
    private Button vAddRoute;

    public AddRouteFragment() {

    }

    String transport = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_route, container, false);

        vFromCity = (EditText)v.findViewById(R.id.from_city);
        vFromCount = (EditText)v.findViewById(R.id.from_coun);
        vToCity = (EditText)v.findViewById(R.id.to_city);
        vToCount = (EditText)v.findViewById(R.id.to_coun);
        vData = (TextView)v.findViewById(R.id.date);
        vTime = (TextView)v.findViewById(R.id.time);
        vCost = (EditText)v.findViewById(R.id.cost);
        vContact = (EditText)v.findViewById(R.id.contact_num);
        vType_0 = (ImageView)v.findViewById(R.id.type_0);
        vType_1 = (ImageView)v.findViewById(R.id.type_1);
        vType_2 = (ImageView)v.findViewById(R.id.type_2);
        vType_3 = (ImageView)v.findViewById(R.id.type_3);
        vType_4 = (ImageView)v.findViewById(R.id.type_4);
        vType_4 = (ImageView)v.findViewById(R.id.type_4);
        vAbout = (EditText)v.findViewById(R.id.about);
        vAddRoute = (Button)v.findViewById(R.id.add_route_btn);

        vData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // data
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final DatePicker picker = new DatePicker(getContext());

                builder.setTitle("Pick a data");
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
                        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
                        vData.setText(format.format(cal.getTime()));
                    }
                });
                builder.show();
            }
        });

        vTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // data
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final TimePicker picker = new TimePicker(getContext());
                picker.setIs24HourView(true);

                builder.setTitle("Pick a time");
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(2000,1,1,picker.getCurrentHour(),picker.getCurrentMinute());
                        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                        vTime.setText(format.format(cal.getTime()));
                    }
                });
                builder.show();
            }
        });
        MyIconListener lis = new MyIconListener();
        vType_0.setOnClickListener(lis);
        vType_1.setOnClickListener(lis);
        vType_2.setOnClickListener(lis);
        vType_3.setOnClickListener(lis);
        vType_4.setOnClickListener(lis);
        vAddRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase fdb = FirebaseDatabase.getInstance();
                final DatabaseReference dbr = fdb.getReference();
                dbr.child("users").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        Route route = new Route(
                                vFromCity.getText().toString(),
                                vFromCount.getText().toString(),
                                vToCity.getText().toString(),
                                vToCount.getText().toString(),
                                vData.getText().toString(),
                                vTime.getText().toString(),
                                vCost.getText().toString(),
                                vContact.getText().toString(),
                                transport,
                                vAbout.getText().toString(),
                                u.username,
                                user.getUid(),
                                u.profileImage
                                );
                        Log.d(TAG,route.toString());

                        dbr.child("routes").child(user.getUid()).push().setValue(route);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        return v;
    }

    private class MyIconListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            vType_0.setImageResource(R.drawable.airplane);
            vType_1.setImageResource(R.drawable.ship);
            vType_2.setImageResource(R.drawable.railway);
            vType_3.setImageResource(R.drawable.bus);
            vType_4.setImageResource(R.drawable.sedan);
            switch (v.getId()){
                case R.id.type_0:
                    vType_0.setImageResource(R.drawable.airplane_filled);
                    transport = "Airport";
                    break;
                case R.id.type_1:
                    vType_1.setImageResource(R.drawable.ship_filled);
                    transport = "Ship";
                    break;
                case R.id.type_2:
                    vType_2.setImageResource(R.drawable.railway_filled);
                    transport = "Train";
                    break;
                case R.id.type_3:
                    vType_3.setImageResource(R.drawable.bus_filled);
                    transport = "Bus";
                    break;
                case R.id.type_4:
                    vType_4.setImageResource(R.drawable.sedan_filled);
                    transport = "Car";
                    break;
            }
        }
    }
}
