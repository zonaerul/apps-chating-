package com.chaerul.chating.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaerul.chating.ChatActivity;
import com.chaerul.chating.R;
import com.chaerul.chating.adapter.data.ListChatUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ListAdapter extends BaseAdapter {

    Context context;
    ArrayList<ListChatUsers> data;
    LayoutInflater inflater;
    boolean icon;

    public ListAdapter(Context context, ArrayList<ListChatUsers> data, boolean icon) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.icon = icon;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_chat_custom, parent, false);
            holder = new ViewHolder();
            holder.textViewName = convertView.findViewById(R.id.name_users);
            holder.textViewMessage = convertView.findViewById(R.id.status_user);
            holder.profile = convertView.findViewById(R.id.profile_users);
            holder.dateusers = convertView.findViewById(R.id.date_users);
            holder.add_user = convertView.findViewById(R.id.add_user);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the current item
        ListChatUsers user = data.get(position);

        // Bind data to the views
        holder.textViewName.setText(user.getName());
        holder.textViewMessage.setText(user.getIsOnline());
        Glide.with(context)
                .load(user.getProfile())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(holder.profile);

        // Convert the timestamp to a formatted time string
        String formattedTime = convertLongToTime(user.getLastOnline());
        if(icon == true){
            holder.add_user.setVisibility(View.GONE);
        }
        holder.dateusers.setText(formattedTime);



        holder.add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dapatkan informasi user yang ingin ditambahkan
                ListChatUsers selectedUser = data.get(position);
                String userIdToAdd = selectedUser.getUserId(); // ID teman yang ingin ditambahkan
                String friendName = selectedUser.getName();
                String friendEmail = selectedUser.getEmail();
                long friendLastOnline = selectedUser.getLastOnline();

                // Dapatkan user yang sedang login (anggap ada cara untuk mendapatkan userId yang sedang login)
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = currentUser != null ? currentUser.getUid() : null;

                if (currentUserId != null) {
                    // Siapkan data teman yang akan ditambahkan
                    HashMap<String, Object> friendData = new HashMap<>();
                    friendData.put("email", friendEmail);
                    friendData.put("name", friendName);
                    friendData.put("lastOnline", friendLastOnline);


                    // Menambahkan teman di Firebase ke dalam node "friends" pengguna yang sedang login
                    DatabaseReference userFriendsRef = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(currentUserId)
                            .child("friends");

                    // Menambahkan teman dengan ID teman yang ingin ditambahkan
                    userFriendsRef.child(userIdToAdd).setValue(friendData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, selectedUser.getName() + " has been added as a friend", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to add friend", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("email", user.getEmail());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    // Method to convert long timestamp to formatted time
    private String convertLongToTime(long timestamp) {
        // Create a SimpleDateFormat to format the time as AM/PM
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        // Convert the long timestamp to a Date object
        Date date = new Date(timestamp);

        // Return the formatted time
        return sdf.format(date);
    }

    static class ViewHolder {
        TextView textViewName;
        TextView textViewMessage;
        TextView dateusers;
        ImageView profile, add_user;

    }

}
