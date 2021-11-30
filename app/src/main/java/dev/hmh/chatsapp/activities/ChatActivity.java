package dev.hmh.chatsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import dev.hmh.chatsapp.adapters.MessageAdapter;
import dev.hmh.chatsapp.databinding.ActivityChatBinding;
import dev.hmh.chatsapp.models.Message;

public class ChatActivity extends AppCompatActivity {

    String strReceiverName;
    String strReceiveUId;
    String strSenderUId;
    String strSenderRoom, strReceiverRoom;

    MessageAdapter messageAdapter;
    ArrayList<Message> arrMessage;

    FirebaseDatabase firebaseDatabase;

    ActivityChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseDatabase = FirebaseDatabase.getInstance();



        strReceiverName = getIntent().getStringExtra("name");
        strReceiveUId = getIntent().getStringExtra("id");
        strSenderUId = FirebaseAuth.getInstance().getUid();

        strSenderRoom = strSenderUId + strReceiveUId;
        strReceiverRoom = strReceiveUId + strSenderUId;

        arrMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter(strSenderRoom, strReceiverRoom, ChatActivity.this, arrMessage);
        binding.rvChatList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvChatList.setAdapter(messageAdapter);


        getSupportActionBar().setTitle(strReceiverName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseDatabase.getReference()
                .child("chats")
                .child(strSenderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrMessage.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            arrMessage.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTypeMessage = binding.etMessageBox.getText().toString();
                Date date = new Date();
                Message message = new Message(strTypeMessage, strSenderUId, date.getTime());
                binding.etMessageBox.setText("");

                String strRandomKey = firebaseDatabase.getReference().push().getKey();

                firebaseDatabase.getReference()
                        .child("chats")
                        .child(strSenderRoom)
                        .child("messages")
                        .child(strRandomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

                firebaseDatabase.getReference()
                        .child("chats")
                        .child(strReceiverRoom)
                        .child("messages")
                        .child(strRandomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}