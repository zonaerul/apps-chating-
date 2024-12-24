package com.chaerul.chating;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;  // Tambahkan Log untuk mencatat informasi
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.chaerul.chating.adapter.ListAdapter;
import com.chaerul.chating.adapter.data.ListChatUsers;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class SearchPage extends AppCompatActivity {
    private ArrayList<ListChatUsers> array;
    private ListAdapter adapter;
    private AppCompatEditText edittext_search;

    private DatabaseReference databaseReference;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        listView = findViewById(R.id.listView);
        
        array = new ArrayList<ListChatUsers>();
        adapter = new ListAdapter(SearchPage.this, array, false);
        listView.setAdapter(adapter);
        edittext_search = findViewById(R.id.search_edittext);

        ImageView back = findViewById(R.id.back_page);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Event listener untuk perubahan pada EditText (search)
        edittext_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                if (!query.isEmpty()) {
                    Log.d("SearchPage", "Searching for: " + query);  // Log pencarian
                    searchUsers(query+"@app.com");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    // Fungsi untuk melakukan pencarian di Firebase
    private void searchUsers(String query) {
        // Use a query that searches for the email field
        Query searchQuery = databaseReference.orderByChild("email").equalTo(query);

        Log.d("SearchPage", "Executing search query: " + searchQuery);  // Log query yang digunakan

        searchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                array.clear();
                if (!dataSnapshot.exists()) {
                    Log.d("SearchPage", "No results found for query: " + query);  // Log jika tidak ada hasil
                }
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Ambil data dari setiap user yang ditemukan
                    String email = snapshot.child("email").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    boolean isOnline = Boolean.TRUE.equals(snapshot.child("isOnline").getValue(Boolean.class));
                    long lastOnline = snapshot.child("lastOnline").getValue(Long.class);
                    String userId = snapshot.child("userId").getValue(String.class);
                    String profile = snapshot.child("profile").getValue(String.class);

                    // Masukkan data ke dalam array
                    ListChatUsers user = new ListChatUsers();
                    user.setUserId(userId);
                    user.setEmail(email);
                    user.setName(name);
                    if(isOnline){
                        user.setIsOnline("online");
                    }else{
                        user.setIsOnline("offline");
                    }
                    user.setLastOnline(lastOnline);
                    user.setProfile(profile);

                    array.add(user);
                }
                Log.d("SearchPage", "Search results updated. Number of results: " + array.size());  // Log jumlah hasil
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Tangani error jika ada
                Log.e("SearchPage", "Search failed: " + databaseError.getMessage());  // Log kesalahan
                Toast.makeText(SearchPage.this, "Search failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
