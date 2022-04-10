package com.example.onexam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onexam.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements ExamListAdapter.ExamListClick {


    ActivityProfileBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchUserProfile();

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLogout();
            }
        });
    }

    User user;
    void fetchUserProfile(){

        startLoading();
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        stopLoading();
                        if(task.isSuccessful()){
                            user = task.getResult().toObject(User.class);
                            updateUi();
                        } else {

                        }
                    }
                });
    }

    void updateUi(){

        binding.userName.setText(user.getFullName());
        binding.program.setText(user.getProgram());
        binding.semester.setText(user.getSem());
        binding.phone.setText(user.getPhone());
        binding.address.setText(user.getAddress());
        binding.dob.setText(user.getDob());

        binding.profileParent.setVisibility(View.VISIBLE);

        fetchAttemptedExams();
    }


    void fetchAttemptedExams(){

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("ExamAttempts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            List<ExamResult> examResults = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()){
                                ExamResult item = doc.toObject(ExamResult.class);
                                examResults.add(item);
                            }
                            updateAttemptRecycler(examResults);
                        }
                    }
                });
    }


    ExamListAdapter examListAdapter;
    void updateAttemptRecycler(List<ExamResult> list){

        if(examListAdapter == null){
            examListAdapter = new ExamListAdapter(this,this,"RESULTS");
            binding.examAttmptRecycler.setAdapter(examListAdapter);
            binding.examAttmptRecycler.setLayoutManager(new LinearLayoutManager(this));
        }

        examListAdapter.updateResultList(list);

    }
    void stopLoading(){
        binding.profileProgress.setVisibility(View.GONE);
    }

    void startLoading(){
        binding.profileProgress.setVisibility(View.VISIBLE);
    }


    void startLogout(){
        SharedPreferences sharedPreferences = getSharedPreferences("ONEXAM",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("IS_LOGIN",false);
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onExamItemClick(Exam exam) {

    }

    @Override
    public void onResultItemClick(ExamResult examResult) {
        AppSingleton.getInstance().setSelectedResult(examResult);
        Intent intent = new Intent(this, ExamDetailActivity.class);
        intent.putExtra("TYPE","EXAM_RESULT");
        startActivity(intent);
    }
}
