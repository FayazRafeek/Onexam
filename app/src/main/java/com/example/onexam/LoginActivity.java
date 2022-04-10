package com.example.onexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.onexam.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.regSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username_from_ed = binding.usernameInp.getText().toString();
                String password_from_ed = binding.passwordInp.getText().toString();

                binding.loginProgress.setVisibility(View.VISIBLE);
                binding.btnLogin.setEnabled(false);
                auth.signInWithEmailAndPassword(username_from_ed,password_from_ed)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                binding.btnLogin.setEnabled(true);
                                binding.loginProgress.setVisibility(View.GONE);
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    saveLogin(username_from_ed);
                                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    void saveLogin(String username){
        SharedPreferences sharedPreferences = getSharedPreferences("ONEXAM",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IS_LOGIN",true);
        editor.putString("USERNAME",username);
        editor.putString("USER_ID",FirebaseAuth.getInstance().getUid());
        editor.commit();
    }
}




