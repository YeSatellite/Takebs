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

import com.bumptech.glide.Glide;
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
import com.yesat.takebs.ChatActivity;
import com.yesat.takebs.MyRouteActivity;
import com.yesat.takebs.R;
import com.yesat.takebs.support.Chat;
import com.yesat.takebs.support.ChatPerson;
import com.yesat.takebs.support.Route;
import com.yesat.takebs.support.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private static final String TAG = "yernar";
    private ArrayList<ChatPerson> chatPersons;
    private ArrayList<String> keys;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;
    private ChatPersonAdapter adapter;
    private FirebaseStorage mStorage;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        chatPersons = new ArrayList<>();
        keys = new ArrayList<>();
        adapter = new ChatPersonAdapter(getActivity(), chatPersons);

        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        ListView listview = (ListView) v.findViewById(R.id.chat_list);
        listview.setAdapter(adapter);


        mDatabase.child("user-messages").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatPersons.clear();
                keys.clear();
                for (DataSnapshot psUser: dataSnapshot.getChildren()) {
                    String url = psUser.getKey();
                    Iterator<DataSnapshot> iter = psUser.getChildren().iterator();
                    DataSnapshot lastData = null;
                    while (iter.hasNext()){
                        lastData = iter.next();
                    }
                    chatPersons.add(new ChatPerson(url,lastData.getKey()));
                    keys.add(psUser.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), ChatActivity.class);
                i.putExtra("chat",chatPersons.get(position));
                startActivity(i);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Delete this Chat")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String key = keys.get(position);
                                Log.d(TAG,key);
                                mDatabase.child("user-messages").child(mUser.getUid()).child(key).removeValue();
                            }
                        })
                        .setNegativeButton("Cancel",null);
                builder.show();
                return true;
            }
        });

        mySwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.swipe_layout);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDatabase.child("user-messages").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        chatPersons.clear();
                        keys.clear();
                        for (DataSnapshot psUser: dataSnapshot.getChildren()) {
                            String url = psUser.getKey();
                            Iterator<DataSnapshot> iter = psUser.getChildren().iterator();
                            DataSnapshot lastData = null;
                            while (iter.hasNext()){
                                lastData = iter.next();
                            }
                            chatPersons.add(new ChatPerson(url,lastData.getKey()));
                            keys.add(psUser.getKey());
                        }
                        adapter.notifyDataSetChanged();
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

    public class ChatPersonAdapter extends ArrayAdapter<ChatPerson> {
        public ChatPersonAdapter(Context context, List<ChatPerson> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ChatPerson chatPerson = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_chat_person, parent, false);
            }

            final TextView tLatMes = (TextView) convertView.findViewById(R.id.lastMes);
            final TextView tLatMesTime = (TextView) convertView.findViewById(R.id.time);
            mDatabase.child("message").child(chatPerson.lastMes).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Chat lastChat = dataSnapshot.getValue(Chat.class);
                    tLatMes.setText(lastChat.text);
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis((long)(double)(lastChat.timestamp*1000));
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    tLatMesTime.setText(format.format(cal.getTime()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            final TextView name = (TextView) convertView.findViewById(R.id.username);
            final ImageView ava = (ImageView) convertView.findViewById(R.id.ava);
            mDatabase.child("users").child(chatPerson.uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User u = dataSnapshot.getValue(User.class);

                            name.setText(u.username);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            try {
                                StorageReference storageReference = storage.getReferenceFromUrl(u.profileImage);
                                Glide.with(ChatFragment.this)
                                        .using(new FirebaseImageLoader())
                                        .load(storageReference)
                                        .into(ava);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            return convertView;

        }
    }

}
