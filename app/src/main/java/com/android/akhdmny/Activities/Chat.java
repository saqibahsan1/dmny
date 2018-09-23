package com.android.akhdmny.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.akhdmny.R;
import com.android.akhdmny.Utils.UserDetails;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Chat extends AppCompatActivity {
    private static final String TAG = "saqib test" ;
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    SimpleDateFormat sdf;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        tvTitle = (TextView) toolbar.findViewById(R.id.tvTitle);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText("Chat");
        sdf = new SimpleDateFormat("EEE, MMM d 'AT' HH:mm a");



        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.fullScroll(View.FOCUS_DOWN);



        Firebase.setAndroidContext(this);
         reference1 = new Firebase("https://akhdmny-4d778.firebaseio.com/Chat/" + UserDetails.username /*+ "_" + UserDetails.chatWith*/);
        //reference2 = new Firebase("https://akhdmny-4d778.firebaseio.com/Chat/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    String currentDateandTime = sdf.format(new Date());
                    map.put("body", messageText);
                    map.put("id", String.valueOf(UserDetails.username));
                    map.put("isDelivered", "true");
                    map.put("isRead", "true");
                    map.put("senderId", String.valueOf(UserDetails.username));
                    map.put("senderImage", "true");
                    map.put("senderName", String.valueOf(UserDetails.username));
                    map.put("time", currentDateandTime);
                    map.put("type", "1");
                    reference1.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                    String message = map.get("body").toString();
                String userName = map.get("id").toString();
                String time = map.get("time").toString();

                if(userName.equals(String.valueOf(UserDetails.username))){
                    addMessageBox("You " , message,time, 1);
                }
                else{
                    addMessageBox(String.valueOf(UserDetails.chatWith), message,time, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String name,String message,String time, int type){

        TextView textmsg = new TextView(Chat.this);
        TextView textname = new TextView(Chat.this);
        TextView texttime = new TextView(Chat.this);

        textname.setText(name);
        textname.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        textmsg.setText(message);
        texttime.setText(time);
        texttime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);

        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp1.gravity = Gravity.RIGHT;
            lp2.gravity = Gravity.RIGHT;
            lp3.gravity = Gravity.RIGHT;
            textmsg.setBackgroundResource(R.drawable.text_in);

        }
        else{
            lp1.gravity = Gravity.LEFT;
            lp2.gravity = Gravity.LEFT;
            lp3.gravity = Gravity.LEFT;

            textmsg.setBackgroundResource(R.drawable.text_out);
        }


        textname.setLayoutParams(lp1);
        textmsg.setLayoutParams(lp2);
        texttime.setLayoutParams(lp3);

        layout.addView(textname);
        layout.addView(textmsg);
        layout.addView(texttime);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
