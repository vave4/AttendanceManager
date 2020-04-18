package com.believe.attendancemanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TakeAttendance extends AppCompatActivity{
    private static final String TAG = "TakeAttendance";

    private String className;
    private RecyclerView rvStudentList;
    private String classKey;
    private ArrayList<ClassPojo> studentList;
    private TextView tvStudentName, tvAttendenceDate;
    private List<Integer> absentStu = new ArrayList<>();
    private Button btConfirmAtt;
    private LinearLayout llNotification;
    private ProgressBar pbStudentLoad;

    private FirebaseDatabase database;
    private GoogleSignInAccount signInAccount;

    private StuRecyclerViewAdapter stuRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        database = FirebaseDatabase.getInstance();
        rvStudentList = findViewById(R.id.lvStudentList);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvAttendenceDate = findViewById(R.id.tvAttendenceDate);
        btConfirmAtt = findViewById(R.id.btConfirmAtt);
        llNotification = findViewById(R.id.llNotification);
        pbStudentLoad = findViewById(R.id.pbStudentLoad);
        signInAccount = GoogleSignIn.getLastSignedInAccount(TakeAttendance.this);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        tvAttendenceDate.setText(formattedDate);

        studentList = new ArrayList<>();

        Intent intent = getIntent();
        className = intent.getExtras().getString("className");
        Log.d(TAG, "onCreate: " + className);

        DatabaseReference classref = database.getReference(signInAccount.getId());

        classref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pbStudentLoad.setVisibility(View.VISIBLE);
                rvStudentList.setVisibility(View.GONE);
                btConfirmAtt.setVisibility(View.GONE);
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    for(DataSnapshot d1: d.getChildren()){
                        if(d1.getKey().equals("ClassName") && d1.getValue().equals(className)){
                            classKey = d.getKey();
                            Log.d(TAG, "onCreate: " + classKey);
                            DatabaseReference stuRef = database.getReference(signInAccount.getId()).child(classKey).child("StudentNameList");
                            stuRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot d : dataSnapshot.getChildren()){
                                        ClassPojo pojo = new ClassPojo(d.getKey(), "P", Integer.parseInt(d.getValue().toString()));
                                        studentList.add(pojo);
                                    }
                                    Log.d(TAG, "onDataChange: data " + studentList.size());
                                    Log.d(TAG, "onDataChange: " + studentList);
                                    if(!studentList.isEmpty()){
                                        pbStudentLoad.setVisibility(View.GONE);
                                        llNotification.setVisibility(View.GONE);
                                        rvStudentList.setVisibility(View.VISIBLE);
                                        btConfirmAtt.setVisibility(View.VISIBLE);
                                        stuRecyclerViewAdapter =
                                                new StuRecyclerViewAdapter(classKey,signInAccount.getId().toString(),TakeAttendance.this,studentList,new ArrayList<Integer>());
                                        LinearLayoutManager layoutManager = new LinearLayoutManager(TakeAttendance.this);
                                        rvStudentList.setLayoutManager(layoutManager);
                                        rvStudentList.setAdapter(stuRecyclerViewAdapter);
                                    }else {
                                        pbStudentLoad.setVisibility(View.GONE);
                                        llNotification.setVisibility(View.VISIBLE);
                                        rvStudentList.setVisibility(View.GONE);
                                        btConfirmAtt.setVisibility(View.GONE);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void confirmAttendance(View view) {
        Log.d(TAG, "confirmAttendance: alicked");
        stuRecyclerViewAdapter.uploadDataToDatabase();
        Log.d(TAG, "confirmAttendance: finished");
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
