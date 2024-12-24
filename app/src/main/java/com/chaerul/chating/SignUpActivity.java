package com.chaerul.chating;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.chaerul.chating.adapter.data.Loading;
import com.chaerul.chating.adapter.data.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    private EditText email, password, name;
    private AppCompatButton signup_btn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final String TAG = "SignUpActivity"; // Log tag for easier identification in Logcat
    private Loading loading;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        loading = new Loading(this);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        // Initialize views
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
        name = findViewById(R.id.name_input);
        signup_btn = findViewById(R.id.signup_button);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailInput = email.getText().toString();
                String passwordInput = password.getText().toString();
                String nameInput = name.getText().toString();

                if (emailInput.isEmpty() || passwordInput.isEmpty() || nameInput.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Sign-up attempt with empty fields"); // Log warning for empty fields
                    return;
                }

                signUpUser(emailInput, passwordInput, nameInput);
            }
        });
    }

    private void signUpUser(String email_, String password, String name) {
        String email = email_.toString()+"@app.com";
        loading.show();
        Log.d(TAG, "Attempting to sign up user with email: " + email); // Log the email for debugging

        // Mengecek apakah email sudah terdaftar
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            // Jika email belum terdaftar, lanjutkan pendaftaran
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(this, signUpTask -> {
                                        if (signUpTask.isSuccessful()) {
                                            loading.dismiss();
                                            // Sign-up success
                                            FirebaseUser user = mAuth.getCurrentUser();

                                            if (user != null) {
                                                String profile = ""; // Bisa diganti dengan URL atau link gambar profil yang diinginkan
                                                String createdAt = String.valueOf(System.currentTimeMillis()); // Tanggal saat pembuatan akun
                                                String lastOnline = String.valueOf(System.currentTimeMillis()); // Timestamp waktu terakhir online

                                                // Buat objek User baru dengan data yang diperlukan
                                                Users newUser = new Users(user.getUid(), true, name, profile, email, password, createdAt, lastOnline);

                                                // Simpan ke Realtime Database
                                                mDatabase.child("users").child(email.replace(".", "_")).setValue(newUser)
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                Log.d(TAG, "User profile saved successfully for " + user.getUid()); // Log success
                                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                editor.putString("user_name", name);
                                                                editor.putString("user_email", email);
                                                                editor.putBoolean("is_logged_in", true);  // Mark as logged in
                                                                editor.apply();
                                                                // Anda dapat melanjutkan ke aktivitas selanjutnya
                                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                finish(); // Pastikan activity saat ini (SignUpActivity) selesai dan tidak ada lagi di stack
                                                            } else {
                                                                Log.e(TAG, "Error saving user data: " + task1.getException().getMessage()); // Log error message
                                                            }
                                                        });
                                            }
                                        } else {
                                            loading.dismiss();
                                            Toast.makeText(this, "pengguna sudah ada", Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Sign-up failed: " + signUpTask.getException().getMessage()); // Log error message
                                        }
                                    });
                        } else {
                            loading.dismiss();
                            Toast.makeText(this, "pengguna sudah ada", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        loading.dismiss();
                        // Jika terjadi error saat pengecekan email
                        Toast.makeText(SignUpActivity.this, "Error checking email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error checking email: " + task.getException().getMessage());
                    }
                });
    }


    // User class to represent user data

}

