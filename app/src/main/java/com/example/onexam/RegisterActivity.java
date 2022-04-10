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

import com.example.onexam.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strPassword = binding.passwordInp.getText().toString();
                String strConfirmPassword = binding.confPasswordInp.getText().toString();
                String strUsername = binding.usernameInp.getText().toString();

                if(!strConfirmPassword.equals(strPassword)){
                    binding.confPasswordInp.setError("Dosen't matches"); return;
                }

                binding.regProgress.setVisibility(View.VISIBLE);
                binding.btnCreateUser.setEnabled(false);
                auth.createUserWithEmailAndPassword(strUsername,strPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                binding.regProgress.setVisibility(View.GONE);
                                binding.btnCreateUser.setEnabled(true);
                                if(task.isSuccessful())
                                {
                                    saveLogin(strUsername);
                                    Toast.makeText(RegisterActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

        binding.loginSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
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