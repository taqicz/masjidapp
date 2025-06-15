package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest; // <-- IMPORT BARU
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText nameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView loginText;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        // Path "Users" Anda sudah benar, kita ikuti
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Inisialisasi elemen UI
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.loginText);

        registerButton.setOnClickListener(v -> registerUser());

        loginText.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validasi input (kode Anda di sini sudah bagus dan tidak diubah)
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Nama tidak boleh kosong!");
            return;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Masukkan email yang valid!");
            return;
        }
        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            phoneEditText.setError("Masukkan nomor telepon yang valid!");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEditText.setError("Password harus minimal 6 karakter!");
            return;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password tidak cocok!");
            return;
        }

        // --- MULAI PERUBAHAN LOGIKA DI SINI ---

        // Registrasi user ke Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {

                            // 1. Buat permintaan untuk update profil dengan nama
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            // 2. Jalankan update profil pada user di Firebase Auth
                            firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                                if (profileTask.isSuccessful()) {
                                    // 3. Setelah nama di Auth berhasil di-set, baru simpan data lengkap ke Realtime Database
                                    String uid = firebaseUser.getUid();
                                    User user = new User(uid, name, email, phone);

                                    databaseReference.child(uid).setValue(user).addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            // 4. SEMUA BERHASIL, arahkan ke halaman Login
                                            Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Gagal menyimpan data pengguna.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Gagal mengatur nama profil.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}