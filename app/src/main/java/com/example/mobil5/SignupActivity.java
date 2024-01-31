package com.example.mobil5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText signup_input1, signup_input2, signup_input3, signup_input4;
    private Button signup_buton1, signup_buton2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        signup_input1 = findViewById(R.id.signup_input1);
        signup_input2 = findViewById(R.id.signup_input2);
        signup_input3 = findViewById(R.id.signup_input3);
        signup_input4 = findViewById(R.id.signup_input4);
        signup_buton1 = findViewById(R.id.signup_buton1);
        signup_buton2 = findViewById(R.id.signup_buton2);

        signup_buton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        signup_buton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signUp() {
        final String email = signup_input3.getText().toString();
        String password = signup_input4.getText().toString();
        final String name = signup_input1.getText().toString();
        final String surname = signup_input2.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Kullanıcı oluşturuldu", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                                String userId = user.getUid();

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("name", name);
                                userData.put("surname", surname);

                                databaseReference.child(userId).setValue(userData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignupActivity.this, "Kullanıcı adı ve soyadı kaydedildi", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    Toast.makeText(SignupActivity.this, "Kullanıcı adı ve soyadı kaydedilemedi", Toast.LENGTH_SHORT).show();

                                                    Log.e("FirebaseError", "Kayıt işlemi sırasında hata: " + task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(SignupActivity.this, "Kullanıcı oluşturulamadı: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}