package com.example.onexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.onexam.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ExamListAdapter.ExamListClick {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkUserProfileStatus();

        binding.compProBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileCompActivity.class));
            }
        });

        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });

        binding.mainSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAvailExams();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkUserProfileStatus();
    }

    void checkUserProfileStatus(){

        binding.mainSwipe.setRefreshing(true);
        String userId = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        binding.mainSwipe.setRefreshing(false);
                        if(task.isSuccessful() && task.getResult().exists()){
                            user = task.getResult().toObject(User.class);
                            saveUser(user);
                            binding.profIncParent.setVisibility(View.GONE);

                            fetchAvailExams();
                        } else {
                            showProfImpUi();
                        }
                    }
                });
    }

    User user;
    void fetchAvailExams(){


        binding.mainSwipe.setRefreshing(true);
        FirebaseFirestore.getInstance()
                .collection("Exams")
                .whereEqualTo("program",user.getProgram())
                .whereEqualTo("sem", user.getSem())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        binding.mainSwipe.setRefreshing(false);
                        if(task.isSuccessful()){

                            List<Exam> exams = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()){
                                Exam item = doc.toObject(Exam.class);
                                exams.add(item);
                            }
                            updateRecycler(exams);

                        } else {

                        }
                    }
                });
    }

    ExamListAdapter examListAdapter;
    void updateRecycler(List<Exam> exams){

        if(examListAdapter == null){
            examListAdapter = new ExamListAdapter(this,this);
            binding.mainRecycler.setAdapter(examListAdapter);
            binding.mainRecycler.setLayoutManager(new LinearLayoutManager(this));
        }

        examListAdapter.updateList(exams);
    }

    void showProfImpUi(){
        binding.profIncParent.setVisibility(View.VISIBLE);
    }

    void saveUser(User user){

        SharedPreferences sharedPreferences = getSharedPreferences("PREFS",MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("USER_DATA",json);
        editor.commit();
    }

    @Override
    public void onExamItemClick(Exam exam) {

        AppSingleton.getInstance().setSelectedExam(exam);
        startActivity(new Intent(this, ExamDetailActivity.class));
    }

    @Override
    public void onResultItemClick(ExamResult examResult) {

    }
}