package com.chaerul.chating.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.chaerul.chating.LoginActivity;
import com.chaerul.chating.SearchPage;
import com.chaerul.chating.SplashscreenActivity;
import com.chaerul.chating.adapter.ListAdapter;
import com.chaerul.chating.adapter.data.ChatMessage;
import com.chaerul.chating.adapter.data.ListChatUsers;
import com.chaerul.chating.adapter.data.Loading;
import com.chaerul.chating.adapter.data.NotificationHelper;
import com.chaerul.chating.adapter.data.Svdata;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.chaerul.chating.R;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class HomeFg extends Fragment {

    private DatabaseReference mDatabase;
    private ListView listview_chat;
    private ArrayList<ListChatUsers> array;
    private ListAdapter adapter;
    private FloatingActionButton float_add_users;
    private Svdata shared;
    private Loading loading;

    private FirebaseUser currentUser;
    private Handler refreshHandler = new Handler();
    private Runnable refreshRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_fg, container, false);

        loading = new Loading(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        shared = new Svdata(getContext());
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // Mendapatkan pengguna yang sedang login

        listview_chat = view.findViewById(R.id.list_chat);
        array = new ArrayList<>();
        adapter = new ListAdapter(getContext(), array, true);
        listview_chat.setAdapter(adapter);
        // Memuat data teman
        loadFriends();

        return view;
    }

    private void loadFriends() {
        if (currentUser != null) {
            // Mendapatkan data teman pengguna yang sedang login
            mDatabase.child("users").child(currentUser.getUid()).child("friends")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            array.clear(); // Bersihkan data lama
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ListChatUsers user = snapshot.getValue(ListChatUsers.class);
                                if (user != null) {
                                    detail(user.getEmail());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getContext(), "Failed to load friends", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void detail(String email) {
        mDatabase.child("users").child(email.replace(".", "_"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String name = dataSnapshot.child("name").getValue(String.class);
                            String email = dataSnapshot.child("email").getValue(String.class);
                            boolean isOnline = dataSnapshot.child("isOnline").getValue(Boolean.class);
                            long lastOnline = dataSnapshot.child("lastOnline").getValue(Long.class);
                            String profile = dataSnapshot.child("profile").getValue(String.class);
                            String userId = dataSnapshot.child("userId").getValue(String.class);

                            ListChatUsers user = new ListChatUsers();
                            user.setUserId(userId);
                            user.setEmail(email);
                            user.setName(name);
                            user.setIsOnline(isOnline ? "online" : "offline");
                            user.setLastOnline(lastOnline);
                            user.setProfile(profile);

                            array.add(user);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to read user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (refreshHandler != null) {
            refreshHandler.removeCallbacks(refreshRunnable); // Hentikan refresh otomatis
        }
    }
}
