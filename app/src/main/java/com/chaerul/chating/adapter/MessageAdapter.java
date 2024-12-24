package com.chaerul.chating.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chaerul.chating.R;
import com.chaerul.chating.adapter.data.ChatMessage;
import com.chaerul.chating.adapter.data.Svdata;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ChatViewHolder> {

    private List<ChatMessage> messageList;
    private Context context;
    private Svdata shared;
    private DatabaseReference chatDatabase;  // Firebase reference for messages

    public MessageAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
        this.shared = new Svdata(context);
        chatDatabase = FirebaseDatabase.getInstance().getReference("messages"); // Firebase reference
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_custom, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        holder.messageText.setText(message.getMessage());
        holder.timestamp.setText(message.getTimestamp());

        // Set background bubble
        if (!message.getEmail().contains(shared.getUserEmail())) {
            holder.avatar.setVisibility(View.GONE);
            holder.avatar_other.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(message.getProfile())
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(holder.avatar_other);

            holder.chatBubble.setBackgroundResource(R.drawable.chat_bubble_background_2);
            holder.ll_message.setGravity(Gravity.END);

            holder.messageText.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.messageText);
                popupMenu.inflate(R.menu.message_options_menu);  // Create a menu resource for the options

                // Set the actions for each menu item
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.copy_menu) {
                        copyMessageToClipboard(message);
                        return true;
                    } else if (item.getItemId() == R.id.delete_menu) {
                        deleteMessage(message, position);  // Pass the message to delete from Firebase
                        return true;
                    } else if (item.getItemId() == R.id.edit_menu) {
                        editMessage(message);
                        return true;
                    } else {
                        return false;
                    }
                });

                popupMenu.show();
            });
        } else {
            holder.avatar.setVisibility(View.VISIBLE);
            holder.avatar_other.setVisibility(View.GONE);
            Glide.with(context)
                    .load(message.getProfile())
                    .placeholder(R.drawable.user)
                    .error(R.drawable.user)
                    .into(holder.avatar);

            holder.chatBubble.setBackgroundResource(R.drawable.chat_bubble_background);
            holder.ll_message.setGravity(Gravity.START);

            holder.messageText.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.messageText);
                popupMenu.inflate(R.menu.message_options_menu_2);  // Create a menu resource for the options

                // Set the actions for each menu item
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.copy_menu) {
                        copyMessageToClipboard(message);
                        return true;
                    } else {
                        return false;
                    }
                });

                popupMenu.show();
            });
        }

        // Set click listener to show the options menu (Copy, Delete, Edit)

    }

    private void copyMessageToClipboard(ChatMessage message) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            android.content.ClipData clip = android.content.ClipData.newPlainText("message", message.getMessage());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Message copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    // Modify deleteMessage to delete from Firebase
    private void deleteMessage(ChatMessage message, int position) {
        // Get the message ID
        String messageId = String.valueOf(message.getMessageId());

        // Remove from Firebase
        chatDatabase.child(messageId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove from the local list and notify the adapter
                messageList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editMessage(ChatMessage message) {
        // Implement your edit logic here, like opening an edit dialog or activity
        Toast.makeText(context, "Edit message: " + message.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestamp;
        ImageView avatar, avatar_other;
        LinearLayout chatBubble, ll_message;

        public ChatViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            timestamp = itemView.findViewById(R.id.timestamp);
            avatar = itemView.findViewById(R.id.avatar);
            avatar_other = itemView.findViewById(R.id.avatar_other);
            chatBubble = itemView.findViewById(R.id.chat_bubble);
            ll_message = itemView.findViewById(R.id.ll_message);
        }
    }
}
