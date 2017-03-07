package com.yesat.takebs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yesat.takebs.support.Chat;
import com.yesat.takebs.support.ChatPerson;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<Chat> chats;
    private ChatAdapter adapter;
    private ChatPerson chatPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatPerson = (ChatPerson) getIntent().getSerializableExtra("chat");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        chats = new ArrayList<>();
        adapter = new ChatAdapter(this, chats);

        mDatabase.child("user-messages").child(mAuth.getCurrentUser().getUid()).
                child(chatPerson.uid).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chats.clear();
                for (DataSnapshot psUser: dataSnapshot.getChildren()) {
                    String key = psUser.getKey();
                    mDatabase.child("message").child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Chat lastChat = dataSnapshot.getValue(Chat.class);
                            chats.add(lastChat);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ListView listview = (ListView) findViewById(R.id.chat_list);
        listview.setAdapter(adapter);
    }

    public class ChatAdapter extends ArrayAdapter<Chat> {
        public ChatAdapter(Context context, List<Chat> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Chat chat = getItem(position);
            boolean right = chat.fromId.equals(mAuth.getCurrentUser().getUid());
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).
                        inflate(right?R.layout.item_chat_right:R.layout.item_chat_left
                                , parent, false);
            }
            ((TextView)convertView.findViewById(R.id.chat_text)).setText(chat.text);
            return convertView;

        }
    }
}
