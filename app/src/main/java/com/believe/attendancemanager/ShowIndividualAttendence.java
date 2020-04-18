package com.believe.attendancemanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ShowIndividualAttendence extends AppCompatActivity {

    private static final String TAG = "ShowIndividualAttendenc";

    private ListView lvFinalAttendenceList;
    private ArrayList<StudentAttendencePojo> studentAttendenceArrayList;
    private String className, classKey;
    private int totalClasses;
    private TextView tvActionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_individual_attendence);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        studentAttendenceArrayList = new ArrayList<>();

        lvFinalAttendenceList = findViewById(R.id.lvFinalAttendenceList);
        tvActionbar = findViewById(R.id.tvActionbar);

        Intent intent = getIntent();
        className = intent.getExtras().getString("className");

        tvActionbar.setText("Attendence for " + className);

        DatabaseReference stuRef =
                FirebaseDatabase.getInstance()
                .getReference(GoogleSignIn.getLastSignedInAccount(ShowIndividualAttendence.this).getId());

        stuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    for(DataSnapshot d1: d.getChildren()){
                        if(d1.getKey().equals("ClassName") && d1.getValue().equals(className)){
                            classKey = d.getKey();
                            Log.d(TAG, "onCreate: " + classKey);
                            DatabaseReference stuRef =
                                    FirebaseDatabase.getInstance().getReference(GoogleSignIn.getLastSignedInAccount(ShowIndividualAttendence.this).getId())
                                            .child(classKey)
                                            .child("StudentNameList");
                            stuRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        StudentAttendencePojo pojo = new StudentAttendencePojo(d.getKey(),d.getValue().toString());
                                        studentAttendenceArrayList.add(pojo);
                                    }
                                    DatabaseReference totalClassRef =
                                            FirebaseDatabase.getInstance()
                                                    .getReference(GoogleSignIn.getLastSignedInAccount(ShowIndividualAttendence.this).getId())
                                                    .child(classKey)
                                                    .child("TotalClasses");
                                    totalClassRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            totalClasses = dataSnapshot.getValue(Integer.class);
                                            Log.i(TAG, "onDataChange: into" + totalClasses);

                                            customAttendenceListAdapter adapter =
                                                    new customAttendenceListAdapter(studentAttendenceArrayList,classKey,className,ShowIndividualAttendence.this,totalClasses);
                                            lvFinalAttendenceList.setAdapter(adapter);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
