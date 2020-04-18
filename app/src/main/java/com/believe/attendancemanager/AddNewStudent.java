package com.believe.attendancemanager;

import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class AddNewStudent extends AppCompatActivity {

    private static final String TAG = "AddNewStudent";

    private ListView lvClassList;
    private List<String> classList;
    private ProgressBar pbClassLoad;
    private LinearLayout llNotification;

    private String classname, subjectname, classkey;

    private GoogleSignInAccount currentUser;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        currentUser = GoogleSignIn.getLastSignedInAccount(AddNewStudent.this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        lvClassList = findViewById(R.id.lvClassList);
        pbClassLoad = findViewById(R.id.pbClassLoad);
        llNotification = findViewById(R.id.llNotification);

        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference(GoogleSignIn.getLastSignedInAccount(AddNewStudent.this).getId());
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
                ArrayAdapter adapter =
                        new ArrayAdapter<String>(AddNewStudent.this,R.layout.custom_textview,R.id.tvCustomClassName,classList);
                lvClassList.setAdapter(adapter);
                pbClassLoad.setVisibility(View.GONE);
                lvClassList.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                finish();
            }
        });

        lvClassList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = lvClassList.getItemAtPosition(position).toString();
                final String finalClassName = selectedItem.substring(0,selectedItem.indexOf("-"));
                CustomAddStuDialogue dialogue = new CustomAddStuDialogue(AddNewStudent.this,finalClassName);
                dialogue.setCancelable(false);
                dialogue.show();
                Window window = dialogue.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
