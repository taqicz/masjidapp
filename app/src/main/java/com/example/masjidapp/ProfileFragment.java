package com.example.masjidapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private EditText editName, editEmail, editPhone;
    private Button btnSave, btnLogout;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inisialisasi Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Inisialisasi UI
        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editPhone = view.findViewById(R.id.editPhone);
        btnSave = view.findViewById(R.id.btnSave);
        btnLogout = view.findViewById(R.id.btnLogout);

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Ambil data user dari Realtime Database
            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        editName.setText(user.getName());
                        editEmail.setText(user.getEmail()); // Non-editable nanti
                        editPhone.setText(user.getPhone());
                        editEmail.setEnabled(false); // Email tidak boleh diubah di sini
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Simpan perubahan profil
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString().trim();
                String newPhone = editPhone.getText().toString().trim();

                if (TextUtils.isEmpty(newName)) {
                    editName.setError("Nama tidak boleh kosong");
                    return;
                }

                if (TextUtils.isEmpty(newPhone) || newPhone.length() < 10) {
                    editPhone.setError("Nomor HP tidak valid");
                    return;
                }

                if (currentUser != null) {
                    String uid = currentUser.getUid();
                    databaseReference.child(uid).child("name").setValue(newName);
                    databaseReference.child(uid).child("phone").setValue(newPhone);
                    Toast.makeText(getContext(), "Profil diperbarui", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}
