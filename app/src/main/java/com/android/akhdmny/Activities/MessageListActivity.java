package com.android.akhdmny.Activities;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.android.akhdmny.Adapter.MessageListAdapter;
import com.android.akhdmny.ApiResponse.AcceptModel.User;
import com.android.akhdmny.Models.Message;
import com.android.akhdmny.R;
import com.android.akhdmny.Singletons.CurrentOrder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessageListActivity extends AppCompatActivity {

    private EditText editText;
    private MessageListAdapter mMessageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private DatabaseReference ref;
    private SimpleDateFormat formatter;
    private User user = CurrentOrder.getInstance().user;
    private ValueEventListener valueEventListner;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        RecyclerView mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, messageList, "31");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);

        editText = findViewById(R.id.edittext_chatbox);
        Button sendButton = findViewById(R.id.button_chatbox_send);
        sendButton.setOnClickListener(view -> {
            mMessageAdapter.notifyDataSetChanged();
            Message message = new Message();
            message.setBody(editText.getText().toString());
            message.setType(1);
            sendMessage(message);
            editText.setText("");
        });


        ref = FirebaseDatabase.getInstance().getReference().child("Chat").child("270").child("messages");
        getFirebaseMessage();

    }

    private void getFirebaseMessage(){
        valueEventListner = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Boolean isRead = Boolean.parseBoolean(Objects.requireNonNull(snapshot.child("isRead").getValue()).toString());
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        message.setRead(isRead);
                    }
                    messageList.add(message);
                    if (user != null){
                        if (message != null) {
                            if (!message.getSenderId().equals(user.getId() + "")){
                                if (message.getRead() == null){
                                    message.setRead(false);
                                }
                                ref.child(message.getId()).child("isRead").setValue(true);
                            }
                        }
                    }
                }
                mMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addValueEventListener(valueEventListner);
    }

    private void sendMessage(int type){

        switch (type){
            case 1:

                break;

            case 2:
                break;

            case 3:
                break;

            case 4:
                break;
        }
    }

    private void sendMessage(Message message){
        String key = ref.push().getKey();
        if (user != null) {
            ArrayMap<String, Object> map = new ArrayMap<>();
            map.put("body", message.getBody());
            map.put("isDelivered", true);
            map.put("isRead", false);
            map.put("senderId", user.getId() + "");
            map.put("senderImage", user.getAvatar());
            map.put("senderName", user.getName());
            map.put("time", formatter.format(new Date()));
            map.put("type", message.getType());
            map.put("id", key);
            if (key != null) {
                ref.child(key).setValue(map);
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (valueEventListner != null) {
            ref.removeEventListener(valueEventListner);
        }
        finish();
        return;
    }

}
