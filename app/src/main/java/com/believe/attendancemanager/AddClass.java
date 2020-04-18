package com.believe.attendancemanager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

public class AddClass extends AppCompatActivity implements View.OnClickListener{

    private TextInputLayout etClassNameLayout, etSubjectNameLayout;
    private TextInputEditText etClassName, etSubjectName;
    private Button btConfirm;
    private ProgressBar pbLoading;

    private GoogleSignInAccount currentUser;
    private FirebaseDatabase database;

    private int confirmKey = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        currentUser = GoogleSignIn.getLastSignedInAccount(AddClass.this);

        database = FirebaseDatabase.getInstance();

        etClassNameLayout = findViewById(R.id.etClassNameLayout);
        etSubjectNameLayout = findViewById(R.id.etSubNameLayout);
        etClassName = findViewById(R.id.etClassName);
        etSubjectName = findViewById(R.id.etSubName);
        btConfirm = findViewById(R.id.btConfirm);
        pbLoading = findViewById(R.id.pdLoading);
        btConfirm.setOnClickListener(this);
    }

    private void uploadDataToDatabase() {
        final String className, subjectName;
        className = etClassName.getText().toString();
        subjectName = etSubjectName.getText().toString();

        if(className.isEmpty()){
            etClassNameLayout.setErrorEnabled(true);
            etClassNameLayout.setError("Please enter your class name");
            confirmKey = 0;
        }else {
            etClassNameLayout.setErrorEnabled(false);
        }

        if(subjectName.isEmpty()){
            etSubjectNameLayout.setErrorEnabled(true);
            etSubjectNameLayout.setError("Please enter your subject name");
            confirmKey = 0;
        }else {
            etSubjectNameLayout.setErrorEnabled(false);
        }

        if(!className.isEmpty() && !subjectName.isEmpty()){
            final DatabaseReference myRef = database.getReference(currentUser.getId());
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    //TODO your background code
                    Map<String,Object> node = new HashMap<>();
                    node.put("ClassName",className);
                    node.put("SubjectName",subjectName);
                    node.put("TotalStudents",0);
                    node.put("TotalClasses",0);
                    myRef.push().setValue(node);
                }
            });
            confirmKey = 1;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btConfirm:
                btConfirm.setEnabled(false);
                pbLoading.setVisibility(View.VISIBLE);
                uploadDataToDatabase();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 1000);

                if(confirmKey == 1){
                    CustomAddStuDialogue dialogue = new CustomAddStuDialogue(AddClass.this,etClassName.getText().toString());
                    dialogue.setCancelable(false);
                    dialogue.show();
                    Window window = dialogue.getWindow();
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
