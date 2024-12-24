package com.chaerul.chating;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chaerul.chating.adapter.data.Loading;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private AppCompatButton login_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // SharedPreferences for storing user data locally
    private SharedPreferences sharedPreferences;
    private Loading loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loading = new Loading(this);

        email = findViewById(R.id.email_input);
        email.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String allowedChars = "abcdefghijklmnopqrstuvwxyz.1234567890";

                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);

                    // Cek apakah karakter yang dimasukkan adalah salah satu dari yang diperbolehkan
                    if (allowedChars.indexOf(c) == -1) {
                        return "";  // Tolak karakter jika tidak ada dalam allowedChars
                    }
                }
                return null;  // Terima karakter jika ada dalam allowedChars
            }
        }});
        password = findViewById(R.id.password_input);
        login_btn = findViewById(R.id.login_btn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        TextView text_add = findViewById(R.id.text_add_acc);
        text_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString();
                String passwordInput = password.getText().toString();

                if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                    return;
                }

                // Show loading dialog
                loading.show();

                loginUser(emailInput, passwordInput);
            }
        });
    }

    private void loginUser(String email_, String password) {
        String email = email_.toString()+"@app.com";
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Dismiss loading dialog
                    loading.dismiss();

                    if (task.isSuccessful()) {
                        // Sign-in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserInDatabase(user);
                    } else {
                        // If sign-in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserInDatabase(FirebaseUser user) {
        if (user != null) {
            // Show loading dialog while checking user data
            loading.show();

            // Check if user exists in Realtime Database
            mDatabase.child(user.getEmail().replace(".", "_")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Dismiss loading dialog after checking data
                    loading.dismiss();

                    if (dataSnapshot.exists()) {
                        // User data found, save to SharedPreferences
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_name", name);
                        editor.putString("user_email", email);
                        editor.putBoolean("is_logged_in", true);  // Mark as logged in
                        editor.apply();

                        // Redirect to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // User doesn't exist in the database, prompt to complete profile
                        Toast.makeText(LoginActivity.this, "User not found in database.", Toast.LENGTH_SHORT).show();
                        Log.w("LoginActivity", "User data does not exist in database: " + user.getUid());  // Log warning
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Dismiss loading dialog if there is a database error
                    loading.dismiss();

                    Toast.makeText(LoginActivity.this, "Error checking user data.", Toast.LENGTH_SHORT).show();
                    Log.e("LoginActivity", "Database error: " + databaseError.getMessage());  // Log error message
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, check SharedPreferences for auto login
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
            if (isLoggedIn) {
                // If the user is logged in, go directly to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
