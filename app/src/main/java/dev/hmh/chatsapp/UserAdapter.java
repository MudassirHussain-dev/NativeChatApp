package dev.hmh.chatsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.hmh.chatsapp.databinding.RowConversationsBinding;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    RowConversationsBinding binding;
    Context context;
    ArrayList<User> arrayList;

    public UserAdapter(Context context, ArrayList<User> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(context).inflate(R.layout.row_conversations, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = arrayList.get(position);
        holder.binding.txtName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        RowConversationsBinding binding;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationsBinding.bind(itemView);
        }
    }
}
