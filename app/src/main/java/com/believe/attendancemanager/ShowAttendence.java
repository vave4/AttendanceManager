package com.believe.attendancemanager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
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

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class ShowAttendence extends AppCompatActivity {

    private static final String TAG = "ShowAttendence";

    private ListView lvShowClassList;
    private ArrayList<String> classList;
    private String classname, subjectname;
    private ProgressBar pbLoading;

    private LinearLayout llNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendence);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lvShowClassList = findViewById(R.id.lvShowClassList);
        pbLoading = findViewById(R.id.pbloading);
        llNotification = findViewById(R.id.llNotification);

        DatabaseReference classRef =
                FirebaseDatabase.getInstance()
                        .getReference(GoogleSignIn.getLastSignedInAccount(ShowAttendence.this).getId());
        classRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null){
                    llNotification.setVisibility(View.VISIBLE);
                }
                classList = new ArrayList<>();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    for(DataSnapshot d1: d.getChildren()){
                        if(d1.getKey().equals("ClassName")){
                            classname = d1.getValue(String.class);
                        }
                        if(d1.getKey().equals("SubjectName")){
                            subjectname = d1.getValue(String.class);
                        }
                    }
                    classList.add(classname+"-"+subjectname);
                }
                Log.d(TAG, "onDataChange: " + classList);
                ArrayAdapter adapter =
                        new ArrayAdapter<String>(ShowAttendence.this,R.layout.custom_textview,R.id.tvCustomClassName,classList);
                lvShowClassList.setAdapter(adapter);
                pbLoading.setVisibility(View.GONE);
                lvShowClassList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();
            }
        });

        lvShowClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShowAttendence.this,ShowIndividualAttendence.class);
                String selectedItem = lvShowClassList.getItemAtPosition(position).toString();
                String finalClassName = selectedItem.substring(0,selectedItem.indexOf("-"));
                intent.putExtra("className",finalClassName);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
