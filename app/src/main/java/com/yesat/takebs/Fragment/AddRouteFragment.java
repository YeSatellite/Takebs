package com.yesat.takebs.Fragment;


import android.content.DialogInterface;
import android.graphics.PorterDuff;
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
import com.onesignal.OneSignal;
import com.yesat.takebs.R;
import com.yesat.takebs.SignUpActivity;
import com.yesat.takebs.support.Route;
import com.yesat.takebs.support.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddRouteFragment extends Fragment {


    private static final String TAG = "yernar";
    private EditText vFromCity;
    private EditText vToCity;
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
    private String selectMethod = "2";
    private ImageView man;
    private ImageView pack;

    public AddRouteFragment() {

    }

    String transport = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_add_route, container, false);

        vFromCity = (EditText)v.findViewById(R.id.from_city);
        vToCity = (EditText)v.findViewById(R.id.to_city);
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
        man = (ImageView)v.findViewById(R.id.image_man);
        pack = (ImageView)v.findViewById(R.id.image_package);

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
        MyIconListener2 lis2 = new MyIconListener2();
        vType_0.setOnClickListener(lis);
        vType_1.setOnClickListener(lis);
        vType_2.setOnClickListener(lis);
        vType_3.setOnClickListener(lis);
        vType_4.setOnClickListener(lis);
        man.setOnClickListener(lis2);
        pack.setOnClickListener(lis2);
        vAddRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView[] views = new TextView[]{vFromCity,vToCity,vData};
                boolean a = false;
                for(TextView vv: views){
                    Log.d(TAG,vv.length()+"");
                    if(vv.length()==0){
                        vv.getBackground().mutate().setColorFilter(getResources().getColor(R.color.your_color), PorterDuff.Mode.SRC_ATOP);
                        a = a || true;
                    }
                    else{
                        vv.getBackground().mutate().setColorFilter(getResources().getColor(R.color.your_color2), PorterDuff.Mode.SRC_ATOP);
                    }
                }
                if(a)return;


                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase fdb = FirebaseDatabase.getInstance();
                final DatabaseReference dbr = fdb.getReference();
                dbr.child("users").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User u = dataSnapshot.getValue(User.class);
                        Route route = new Route(
                                vFromCity.getText().toString(),
                                vToCity.getText().toString(),
                                vData.getText().toString(),
                                vTime.getText().toString().length()==0?"00:00":vTime.getText().toString(),
                                vCost.getText().toString().length()==0?"-":vCost.getText().toString(),
                                vContact.getText().toString().length()==0?"-":vContact.getText().toString(),
                                transport,
                                vAbout.getText().toString(),
                                selectMethod,
                                u.username,
                                user.getUid(),
                                u.profileImage
                                );
                        Log.d(TAG,route.toString());

                        dbr.child("routes").child(user.getUid()).push().setValue(route);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Route added")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                        builder.show();

                        vFromCity.setText("");
                        vToCity.setText("");
                        vData.setText("");
                        vTime.setText("");
                        vCost.setText("");
                        vContact.setText("");
                        vAbout.setText("");

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
                    transport = "Airplane";
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
    private class MyIconListener2 implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            man.setImageResource(R.drawable.man_white);
            pack.setImageResource(R.drawable.package_white);
            switch (v.getId()){
                case R.id.image_man:
                    man.setImageResource(R.drawable.man_black);
                    selectMethod = "2";
                    break;
                case R.id.image_package:
                    pack.setImageResource(R.drawable.package_black);
                    selectMethod = "1";
                    break;
            }
        }
    }
}
