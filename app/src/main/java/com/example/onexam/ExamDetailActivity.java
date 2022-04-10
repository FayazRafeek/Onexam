package com.example.onexam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onexam.databinding.ActivityExamDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExamDetailActivity extends AppCompatActivity {

    ActivityExamDetailBinding binding;
    Exam exam; ExamResult examResult;

    String type = "EXAM_DETAIL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExamDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        type = getIntent().getStringExtra("TYPE");
        if(type == null) type = "EXAM_DETAIL";

        if(type.equals("EXAM_DETAIL")){
            exam = AppSingleton.getInstance().getSelectedExam();
            if(exam == null) finish();
            updateExamUi();

            binding.attemptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ExamDetailActivity.this, ExamAttempActivity.class));
                }
            });
        } else if (type.equals("EXAM_RESULT")){
            examResult = AppSingleton.getInstance().getSelectedResult();
            if(examResult == null) finish();
            updateResultUi();
        }


        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private static final String TAG = "333";
    void updateExamUi(){

        binding.examName.setText(exam.getExamName());
        binding.examPrgm.setText(exam.getProgram());
        binding.examSem.setText(exam.getSem());
        binding.examQuesCount.setText(exam.getQuestions().size() + "");
        binding.examStaff.setText(exam.getStaffName());

        Log.d(TAG, "updateUi: Questions " + exam.getQuestions());

    }

    void updateResultUi(){

        binding.heroText.setText("Exam Result");
        binding.examName.setText(examResult.getExamName());
        binding.examPrgm.setText(examResult.getProgram());
        binding.examSem.setText(examResult.getSem());
        binding.examQuesCount.setText(examResult.getTotalQuestions() + "");
        binding.staffParent.setVisibility(View.GONE);
        binding.attemptParent.setVisibility(View.VISIBLE);
        binding.scoreParent.setVisibility(View.VISIBLE);
        binding.saScore.setText(examResult.getScore() + " out of " + examResult.getTotalQuestions());

        Date date = new Date();
        date.setTime(Long.parseLong(examResult.getTimestamp()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateSt = dateFormat.format(calendar.getTime());
        binding.examResultAttemtDate.setText(dateSt);

        binding.attemptBtn.setVisibility(View.GONE);

    }
}
