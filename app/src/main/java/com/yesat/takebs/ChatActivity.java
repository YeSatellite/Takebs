package com.yesat.takebs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.StorageReference;
import com.onesignal.OneSignal;
import com.yesat.takebs.support.Chat;
import com.yesat.takebs.support.ChatPerson;
import com.yesat.takebs.support.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "yernar";
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
        final ListView listview = (ListView) findViewById(R.id.chat_list);
        listview.setAdapter(adapter);

        mDatabase.child("user-messages").child(mAuth.getCurrentUser().getUid()).
                child(chatPerson.uid).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chats.clear();
                final int n = (int) dataSnapshot.getChildrenCount();
                final int[] i = { 0 };
                for (DataSnapshot psUser: dataSnapshot.getChildren()) {
                    String key = psUser.getKey();
                    mDatabase.child("message").child(key).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Chat lastChat = dataSnapshot.getValue(Chat.class);
                            chats.add(lastChat);
                            i[0]++;
                            if(i[0] == n) {
                                adapter.notifyDataSetChanged();
                                listview.setSelection(adapter.getCount() - 1);
                                Log.d(TAG,"-----------"+n+"---------"+i[0]);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final EditText etTest = (EditText)findViewById(R.id.text_chat);
        TextView sent = (TextView) findViewById(R.id.btn_send);

        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUid = mAuth.getCurrentUser().getUid();
                String target_uid = chatPerson.uid;
                final String text = etTest.getText().toString();
                if(text.length()==0)return;
                double time = System.currentTimeMillis()/1000.0;
                DatabaseReference ref = mDatabase.child("user-messages")
                        .child(myUid).child(target_uid).push();
                ref.setValue(1);
                mDatabase.child("user-messages")
                        .child(target_uid).child(myUid).child(ref.getKey())
                        .setValue(1);
                Chat chat = new Chat(myUid,text,time,target_uid);
                mDatabase.child("message").child(ref.getKey()).setValue(chat);
                etTest.setText("");

                mDatabase.child("users").child(target_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        try {
                            OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + text + "'}, 'include_player_ids': ['" + user.oneSignalId + "']}"), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

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
            convertView = LayoutInflater.from(getContext()).
                    inflate(right?R.layout.item_chat_right:R.layout.item_chat_left
                            , parent, false);

            ((TextView)convertView.findViewById(R.id.chat_text)).setText(chat.text);
            return convertView;

        }
    }
}
