package com.chaerul.chating;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaerul.chating.adapter.MessageAdapter;
import com.chaerul.chating.adapter.data.ChatMessage;
import com.chaerul.chating.adapter.data.ListChatUsers;
import com.chaerul.chating.adapter.data.Loading;
import com.chaerul.chating.adapter.data.Svdata;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText message_input;
    private ImageView send_message;
    private String name, email, currentUserEmail;
    private TextView name_users, textViewStatus;
    private DatabaseReference chatDatabase;
    private Loading loading;
    private Svdata shared;
    private ImageView back_page;
    private String chatPath;
    private String reverseChatPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);

        loading = new Loading(this);
        shared = new Svdata(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            // --- nama teman
            name = bundle.getString("name", "unknown");

            // --- email teman yang akan di chat -----
            email = bundle.getString("email", "null");
        }
        ImageView back = findViewById(R.id.back_page);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        detail(email);

        // Menampilkan nama dan email pengguna lain
        name_users = findViewById(R.id.name_users);
        textViewStatus = findViewById(R.id.status_user);
        name_users.setText(name);
        currentUserEmail = shared.getUserEmail();
        recyclerChat = findViewById(R.id.recycler_message);
        message_input = findViewById(R.id.message_input);
        message_input.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Jika tombol Enter ditekan
                String messageText = message_input.getText().toString().trim();

                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                    message_input.setText(""); // Hapus teks setelah dikirim
                } else {
                    Toast.makeText(ChatActivity.this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                return true; // Konsumsi event
            }
            return false; // Tidak memproses tindakan lain
        });

        send_message = findViewById(R.id.send_message);
        back_page = findViewById(R.id.back_page);

        chatDatabase = FirebaseDatabase.getInstance().getReference("chat");

        ArrayList<ChatMessage> arrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(linearLayoutManager);
        MessageAdapter adapter = new MessageAdapter(this, arrayList);
        recyclerChat.setAdapter(adapter);

        // Ubah email menjadi format path yang valid di Firebase
        String validEmailPath = email.replace(".", "_").replace("@", "_");
        String validCurrentUserEmail = currentUserEmail.replace(".", "_").replace("@", "_");

        // Mengatur path Firebase untuk chat
        chatPath = "chat/" + validCurrentUserEmail + "/" + validEmailPath;
        reverseChatPath = "chat/" + validEmailPath + "/" + validCurrentUserEmail;

        // Setup Listener untuk tombol kirim pesan
        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = message_input.getText().toString().trim();

                if (!messageText.isEmpty()) {
                    sendMessage(messageText);
                    message_input.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Mendengarkan perubahan pesan dari Firebase
        chatDatabase.child(validCurrentUserEmail).child(validEmailPath).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // Mendapatkan data pesan
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                if (chatMessage != null) {
                    arrayList.add(chatMessage);
                    adapter.notifyItemInserted(arrayList.size() - 1); // Update RecyclerView
                    recyclerChat.scrollToPosition(arrayList.size() - 1); // Scroll ke bawah
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle perubahan pesan jika diperlukan
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle penghapusan pesan jika diperlukan
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle jika pesan dipindahkan
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ChatActivity", "Error membaca data Firebase: " + databaseError.getMessage());
            }
        });
    }

    // Fungsi untuk mengirim pesan ke Firebase
    private void sendMessage(String messageText) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String messageId = chatDatabase.push().getKey(); // Mengambil ID pesan unik dari Firebase

        // Membuat objek ChatMessage
        ChatMessage message = new ChatMessage(
                messageId,
                email,
                messageText,
                "null", // Anda bisa ganti dengan URL profile pengguna
                timestamp,
                currentUserEmail, // Pengirim pesan
                true, // Menandakan bahwa ini pesan dari pengguna saat ini
                false, // Pesan belum dibaca pada saat pengiriman
                true, // Status online
                timestamp, // Waktu terakhir kali pengguna online
                new ChatMessage.Receiver(currentUserEmail, email) // Menambahkan receiver
        );

        String validEmailPath = email.replace(".", "_").replace("@", "_");
        String validCurrentUserEmail = currentUserEmail.replace(".", "_").replace("@", "_");

        chatPath = "chat/" + validCurrentUserEmail + "/" + validEmailPath;
        reverseChatPath = "chat/" + validEmailPath + "/" + validCurrentUserEmail;

        // Kirim pesan ke Firebase untuk pengirim dan penerima
        chatDatabase.child(validCurrentUserEmail).child(validEmailPath).child(messageId).setValue(message);
        chatDatabase.child(validEmailPath).child(validCurrentUserEmail).child(messageId).setValue(message);
    }

    private void detail(String email) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
// Mendapatkan data pengguna berdasarkan email
        mDatabase.child("users").child(email.replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Mengecek apakah data ada
                if (dataSnapshot.exists()) {
                    // Mengambil data pengguna dari snapshot
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    boolean isOnline = dataSnapshot.child("isOnline").getValue(Boolean.class);
                    long lastOnline = dataSnapshot.child("lastOnline").getValue(Long.class);
                    String profile = dataSnapshot.child("profile").getValue(String.class);
                    String userId = dataSnapshot.child("userId").getValue(String.class);

                    // Menampilkan data pengguna
                    Log.d("UserData", "Name: " + name);
                    Log.d("UserData", "Email: " + email);
                    Log.d("UserData", "Is Online: " + isOnline);
                    Log.d("UserData", "Last Online: " + lastOnline);
                    Log.d("UserData", "Profile: " + profile);
                    Log.d("UserData", "User ID: " + userId);

                    if(isOnline){
                        textViewStatus.setText("sedang online");
                    }else{
                        textViewStatus.setText("terakhir online "+convertLongToTime(lastOnline));
                    }

                } else {
                    // Jika data tidak ditemukan
                    Toast.makeText(ChatActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Menangani kesalahan
                Toast.makeText(ChatActivity.this, "Failed to read user data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String convertLongToTime(long timestamp) {
        // Create a SimpleDateFormat to format the time as AM/PM
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        // Convert the long timestamp to a Date object
        Date date = new Date(timestamp);

        // Return the formatted time
        return sdf.format(date);
    }
}
