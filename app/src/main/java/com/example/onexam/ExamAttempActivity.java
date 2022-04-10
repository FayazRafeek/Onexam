package com.example.onexam;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.onexam.databinding.ActivityExamAttemptBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ExamAttempActivity extends AppCompatActivity {

    ActivityExamAttemptBinding binding;
    Exam exam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExamAttemptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        exam = AppSingleton.INSTANCE.getSelectedExam();
        if(exam == null) finish();

        questions = exam.getQuestions();
        showNextQuestion();

        binding.option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAnswer("OP_1");
            }
        });
        binding.option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAnswer("OP_2");
            }
        });
        binding.option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAnswer("OP_3");
            }
        });
        binding.option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAnswer("OP_4");
            }
        });
    }

    int currQues = -1;
    Question currQuest;
    void showNextQuestion(){

        blink();
        currQues++;
        if(currQues >= questions.size()){
            submitExam();
            return;
        }
        currQuest = questions.get(currQues);

        int questNo = currQues+1;
        binding.questNum.setText(questNo +"/" + questions.size());

        binding.question.setText(currQuest.getQuestionTitle());
        binding.option1.setText(currQuest.getOp1());
        binding.option2.setText(currQuest.getOp2());
        binding.option3.setText(currQuest.getOp3());
        binding.option4.setText(currQuest.getOp4());

        binding.option1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.orange1)));
        binding.option2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.orange1)));
        binding.option3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.orange1)));
        binding.option4.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this,R.color.orange1)));

        binding.countdown.setText(currQuest.getTime() + "");
        startTimer(currQuest.getTime());

    }

    private static final String TAG = "333";
    CountDownTimer timer;

    void startTimer(int time){

        Log.d(TAG, "startTimer: Time " + time);
        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.countdown.setText(String.valueOf(millisUntilFinished / 1000));
                Log.d(TAG, "startTimer: Time  tick" + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                showNextQuestion();
            }
        };

        timer.start();
    }

    List<Question> questions = new ArrayList<>();
    void submitAnswer(String option){

        timer.cancel();
        Log.d(TAG, "submitAnswer: Answer " + currQuest.getAnswer()) ;
        switch (option){
            case "OP_1" :
                if(currQuest.getAnswer().equals(currQuest.getOp1())){
                    questions.get(currQues).setCorrect(true);
                    binding.option1.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                } else binding.option1.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
            case "OP_2" :
                if(currQuest.getAnswer().equals(currQuest.getOp2())){
                    questions.get(currQues).setCorrect(true);
                    binding.option2.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                } else binding.option2.setBackgroundTintList(ColorStateList.valueOf(Color.RED));;
                break;
            case "OP_3" :
                if(currQuest.getAnswer().equals(currQuest.getOp3())){
                    questions.get(currQues).setCorrect(true);
                    binding.option3.setBackgroundColor(ContextCompat.getColor(this,R.color.green));
                } else binding.option3.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
            case "OP_4" :
                if(currQuest.getAnswer().equals(currQuest.getOp4())){
                    questions.get(currQues).setCorrect(true);
                    binding.option4.setBackgroundColor(ContextCompat.getColor(this,R.color.green));
                } else binding.option4.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextQuestion();
            }
        },1000);

    }


    void submitExam(){
        Toast.makeText(this, "Exam successfully submitted", Toast.LENGTH_SHORT).show();

        int score = 0;
        for (Question q : questions)
            if(q.getCorrect())
                score++;


        binding.scoreParent.setVisibility(View.VISIBLE);
        binding.scoreLayout.saScore.setText(score + " out of " + questions.size());

        binding.scoreLayout.saDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postResults();
            }
        });
    }

    void postResults(){

        int score = 0;
        for (Question q : questions)
            if(q.getCorrect())
                score++;

        String resultId = String.valueOf(System.currentTimeMillis());

        ExamResult examResult = new ExamResult(resultId,exam.getExamId(), FirebaseAuth.getInstance().getUid(),
                exam.getStaffId(),exam.getStaffName(),exam.getExamName(),exam.getProgram(),exam.getSem(),questions.size(),score,resultId);


        showUploadLoading();

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        examResult.setUserName(user.getFullName());

                        FirebaseFirestore.getInstance()
                                .collection("Exams")
                                .document(exam.getExamId())
                                .collection("Attempts")
                                .document(examResult.getResultId())
                                .set(examResult)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){
                                            FirebaseFirestore.getInstance().collection("Users")
                                                    .document(FirebaseAuth.getInstance().getUid())
                                                    .collection("ExamAttempts")
                                                    .document(examResult.getResultId())
                                                    .set(examResult)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(ExamAttempActivity.this, "Exam results uploaded", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(ExamAttempActivity.this,MainActivity.class));
                                                                finish();
                                                            } else {
                                                                Toast.makeText(ExamAttempActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(ExamAttempActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                });



    }

    void showUploadLoading(){
        binding.examUploadProgress.setVisibility(View.VISIBLE);
    }

    void blink(){
        binding.changeBlink.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.changeBlink.setVisibility(View.GONE);
            }
        },400);
    }
}
