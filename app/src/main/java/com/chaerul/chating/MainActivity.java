package com.chaerul.chating;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.chaerul.chating.adapter.CameraPage;
import com.chaerul.chating.adapter.data.Svdata;
import com.chaerul.chating.fragment.HomeFg;
import com.chaerul.chating.fragment.StatusFg;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private FirebaseDatabase mDatabase;  // Firebase Database reference
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Svdata shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance();
        shared = new Svdata(this);

        bottomNavigationView = findViewById(R.id.bottomnav);


        viewPager = findViewById(R.id.viewPager);
        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                startActivity(intent);
            }
        });

        ImageView camera = findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CameraPage.class);
                startActivity(intent);
            }
        });

        // Set up ViewPager2 with an adapter
        pagerAdapter = new FragmentStateAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Set BottomNavigationView item click listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int selectedItemId = item.getItemId();
            if (selectedItemId == R.id.home_nav) {
                viewPager.setCurrentItem(0);  // Switch to HomeFg
            } else if (selectedItemId == R.id.status_nav) {
                viewPager.setCurrentItem(1);  // Switch to StatusFg
            }
            return true;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View headerView = navigationView.getHeaderView(0);
        TextView user = headerView.findViewById(R.id.username_users);
        user.setText(shared.getUserName());

        // Set NavigationView item click listener
        navigationView.setNavigationItemSelectedListener(this);

        // Set ViewPager2's page change listener to sync with BottomNavigationView
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.home_nav);
                } else if (position == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.status_nav);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void logoutUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;  // No user is logged in
        }

        String currentUserEmail = currentUser.getEmail();
        if (currentUserEmail == null) {
            return;
        }

        // Ganti titik (.) dalam email agar sesuai dengan format kunci Firebase
        String userId = currentUserEmail.replace(".", "_");

        // Create reference to the user's node in Firebase Realtime Database
        DatabaseReference userRef = mDatabase.getReference("users").child(userId);

        // Update the 'isOnline' status and 'online' status to false
        userRef.child("isOnline").setValue(false);
        userRef.child("online").setValue(false);  // Optionally set online to false

        // Update lastOnline timestamp (optional)
        userRef.child("lastOnline").setValue(System.currentTimeMillis());

        // Logout from Firebase Auth
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show();

        // Navigate to login screen
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();  // Close the current activity
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.penyimpanan_nav) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.komunitas_nav) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.tentang_nav) {

        }else if(id == R.id.logout_nav){
            logoutUser();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    // Custom FragmentStateAdapter for ViewPager2
    private class FragmentStateAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

        public FragmentStateAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new HomeFg();
            } else {
                return new StatusFg();
            }
        }

        @Override
        public int getItemCount() {
            return 2; // Two fragments: HomeFg and StatusFg
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Svdata shred = new Svdata(this);
        DatabaseReference userRef = mDatabase.getReference("users").child(shred.getUserEmail().replace(".", "_"));
        userRef.child("isOnline").setValue(true);
        userRef.child("online").setValue(true);

        // Gunakan onDisconnect() untuk mengubah status ketika koneksi Firebase terputus
        userRef.child("isOnline").onDisconnect().setValue(false);
        userRef.child("online").onDisconnect().setValue(false);
        userRef.child("lastOnline").onDisconnect().setValue(System.currentTimeMillis());
    }
}
