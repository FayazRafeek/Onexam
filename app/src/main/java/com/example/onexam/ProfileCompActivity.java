package com.example.onexam;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onexam.databinding.ActivityCompleteProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileCompActivity extends AppCompatActivity implements SelectDialog.OnDialogSelect {

    ActivityCompleteProfileBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCompleteProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gatherData();
            }
        });

        binding.dobInp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ProfileCompActivity.this,listner, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        binding.programInp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgramDialog();
            }
        });

        binding.semInp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSemDialog();
            }
        });
    }


    void showProgramDialog(){
        List<String> programs = new ArrayList<>();
        programs.add("BA English");programs.add("BA Hindi");programs.add("Bsc Chemistry");programs.add("BCA");
        new SelectDialog(programs,"Select Program : ",this,"PROGRAM").show(getSupportFragmentManager(),"TAG");
    }
    void showSemDialog(){
        List<String> sems = new ArrayList<>();
        sems.add("First Sem");sems.add("Second Sem");sems.add("Third Sem");sems.add("Fourth Sem");
        new SelectDialog(sems,"Select Semester : ",this,"SEMESTER").show(getSupportFragmentManager(),"TAG");
    }

    Calendar calendar = Calendar.getInstance();
    String dob = "";
    DatePickerDialog.OnDateSetListener listner =new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,day);
            String date = "";
            month++;
            if(day < 10) date = "0" + day; else date = "" + day;
            if(month < 10) date += "-0" + month; else date += "-" + month;
            date += "-" + year;
            dob = date;
            binding.dobInp.setText(date);

        }
    };

    void gatherData(){

        String userName = getSharedPreferences("ONEXAM",MODE_PRIVATE).getString("USERNAME","");
        String fullName = binding.fullnameInp.getText().toString();
        String dob = binding.dobInp.getText().toString();
        String phone = binding.phoneInp.getText().toString();
        String address = binding.addressInp.getText().toString();
        String program = binding.programInp.getText().toString();
        String sem = binding.semInp.getText().toString();

        User user = new User(FirebaseAuth.getInstance().getUid(),
                userName,fullName,phone,address,dob,sem,program,String.valueOf(System.currentTimeMillis()));


        binding.progress.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.progress.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Account created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ProfileCompActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onSelectItem(String item, String tag) {

        if(tag.equals("PROGRAM"))
            binding.programInp.setText(item);
        else binding.semInp.setText(item);


    }
}
