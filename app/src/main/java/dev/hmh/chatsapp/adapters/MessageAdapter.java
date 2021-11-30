package dev.hmh.chatsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dev.hmh.chatsapp.R;
import dev.hmh.chatsapp.databinding.RowReceivedMessageBinding;
import dev.hmh.chatsapp.databinding.RowSentMessageLayoutBinding;
import dev.hmh.chatsapp.models.Message;

public class MessageAdapter extends RecyclerView.Adapter {

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    String strSenderRoom, strReceiverRoom;

    Context context;
    ArrayList<Message> arrayList;

    public MessageAdapter(String strSenderRoom, String strReceiverRoom, Context context, ArrayList<Message> arrayList) {
        this.strSenderRoom = strSenderRoom;
        this.strReceiverRoom = strReceiverRoom;
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            return new MessageSentViewHolder(LayoutInflater.from(context).inflate(R.layout.row_sent_message_layout, parent, false));
        }
        return new MessageReceivedViewHolder(LayoutInflater.from(context).inflate(R.layout.row_received_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = arrayList.get(position);
        int reactions[] = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();


        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == MessageSentViewHolder.class) {
                MessageSentViewHolder messageSentViewHolder = (MessageSentViewHolder) holder;
                messageSentViewHolder.binding.ivFeeling.setImageResource(reactions[pos]);
                messageSentViewHolder.binding.ivFeeling.setVisibility(View.VISIBLE);


            } else {
                MessageReceivedViewHolder messageReceivedViewHolder = (MessageReceivedViewHolder) holder;
                messageReceivedViewHolder.binding.ivFeeling.setImageResource(reactions[pos]);
                messageReceivedViewHolder.binding.ivFeeling.setVisibility(View.VISIBLE);

            }
            message.setFeeling(pos);
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("chats")
                    .child(strSenderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("chats")
                    .child(strReceiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });

        if (holder.getClass() == MessageSentViewHolder.class) {
            MessageSentViewHolder messageSentViewHolder = (MessageSentViewHolder) holder;
            messageSentViewHolder.binding.txtSentMessage.setText(message.getMessage());

            if (message.getFeeling() >= 0) {
               // message.setFeeling(reactions[(int) message.getFeeling()]);
                messageSentViewHolder.binding.ivFeeling.setImageResource(reactions[message.getFeeling()]);
                messageSentViewHolder.binding.ivFeeling.setVisibility(View.VISIBLE);
            } else {
                messageSentViewHolder.binding.ivFeeling.setVisibility(View.GONE);
            }

            messageSentViewHolder.binding.txtSentMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });

        } else {
            MessageReceivedViewHolder messageReceivedViewHolder = (MessageReceivedViewHolder) holder;
            messageReceivedViewHolder.binding.txtReceivedMessage.setText(message.getMessage());

            if (message.getFeeling() >= 0) {
                //message.setFeeling(reactions[(int) message.getFeeling()]);
                messageReceivedViewHolder.binding.ivFeeling.setImageResource(reactions[message.getFeeling()]);
                messageReceivedViewHolder.binding.ivFeeling.setVisibility(View.VISIBLE);
            } else {
                messageReceivedViewHolder.binding.ivFeeling.setVisibility(View.GONE);
            }
            messageReceivedViewHolder.binding.txtReceivedMessage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v, event);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = arrayList.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MessageSentViewHolder extends RecyclerView.ViewHolder {
        RowSentMessageLayoutBinding binding;

        public MessageSentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowSentMessageLayoutBinding.bind(itemView);

        }
    }

    public class MessageReceivedViewHolder extends RecyclerView.ViewHolder {
        RowReceivedMessageBinding binding;

        public MessageReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowReceivedMessageBinding.bind(itemView);
        }
    }
}
