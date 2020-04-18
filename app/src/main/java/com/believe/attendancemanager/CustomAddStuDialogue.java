package com.believe.attendancemanager;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

public class CustomAddStuDialogue extends Dialog implements View.OnClickListener {

    private static final String TAG = "CustomAddStuDialogue";

    private Activity activity;
    private TextInputLayout etStuNameLayout;
    private TextInputEditText etStuName;
    private Button btFinish, btOneMore;

    private GoogleSignInAccount currentUser;
    private FirebaseDatabase database;

    public int numberOfStudents;
    private String className;
    private String classKeyString;

    public CustomAddStuDialogue(@NonNull Activity activity,String className) {
        super(activity);
        this.activity = activity;
        this.className = className;
        currentUser = GoogleSignIn.getLastSignedInAccount(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_addstu_dialogu);

        database = FirebaseDatabase.getInstance();

        etStuNameLayout = findViewById(R.id.etStuNameLayout);
        etStuName = findViewById(R.id.etStuName);
        btFinish = findViewById(R.id.btFinish);
        btOneMore = findViewById(R.id.btOneMore);

        final DatabaseReference classkey = database.getReference(currentUser.getId());
        classkey.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    for(DataSnapshot d1: d.getChildren()){
                        Log.d(TAG, "onDataChange: reach 1" + d1.getKey());
                        if(d1.getKey().equals("ClassName") && d1.getValue().equals(className)){
                            classKeyString = d.getKey();
                            Log.d(TAG, "onDataChange: " + d.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btFinish.setOnClickListener(this);
        btOneMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        
        if(v.getId() == R.id.btFinish){
            uploadDataToDatabase(1);
            dismiss();
            activity.finish();
        }
        if(v.getId() == R.id.btOneMore){
            Log.d(TAG, "onClick: ");
            uploadDataToDatabase(0);
        }
    }

    private void uploadDataToDatabase(int key) {
        Log.d(TAG, "uploadDataToDatabase: entered");
        String studentName;
        studentName = etStuName.getText().toString();

        final DatabaseReference totalStudentRef = database.getReference(currentUser.getId()).child(classKeyString);
        totalStudentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    if(d.getKey().equals("TotalStudents")){
                        numberOfStudents = Integer.parseInt(d.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "uploadDataToDatabase: number of students" + numberOfStudents);

        if(studentName.isEmpty() && key==0){
            etStuNameLayout.setErrorEnabled(true);
            etStuNameLayout.setError("Please enter your Student name");
        }else if(!studentName.isEmpty() && key==0){
            btFinish.setVisibility(View.VISIBLE);
            etStuNameLayout.setErrorEnabled(false);
            Log.d(TAG, "uploadDataToDatabase: " + className);
            totalStudentRef.child("TotalStudents").setValue(numberOfStudents+1);
            totalStudentRef.child("StudentNameList").child(studentName).setValue(0);
            etStuName.setText("");
        }

        if(key==1 && !studentName.isEmpty()){
            totalStudentRef.child("TotalStudents").setValue(numberOfStudents+1);
            totalStudentRef.child("StudentNameList").child(studentName).setValue(0);
        }
    }
}
