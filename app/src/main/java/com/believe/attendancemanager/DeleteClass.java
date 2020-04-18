package com.believe.attendancemanager;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class DeleteClass extends AppCompatActivity {

    private static final String TAG = "DeleteClass";

    private ListView lvClassList;
    public ArrayList<Integer> selectedForDelete = new ArrayList<>();
    public ArrayList<String> classList;
    private ProgressBar pbClassLoad;
    private LinearLayout ll1;
    private TextView tvEmptyNotification;

    private GoogleSignInAccount currentUser;
    private FirebaseDatabase firebaseDatabase;

    private String classname, subjectname, classkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        currentUser = GoogleSignIn.getLastSignedInAccount(DeleteClass.this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        lvClassList = findViewById(R.id.lvClassList);
        pbClassLoad = findViewById(R.id.pbClassLoad);
        ll1 = findViewById(R.id.ll1);
        tvEmptyNotification = findViewById(R.id.tvEmptyNotification);

        DatabaseReference classRef = firebaseDatabase.getReference(currentUser.getId());
        classRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: " + dataSnapshot.getValue());
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
                if(classList.isEmpty()){
                    tvEmptyNotification.setVisibility(View.VISIBLE);
                    pbClassLoad.setVisibility(View.GONE);
                    ll1.setVisibility(View.GONE);
                }else{
                    ArrayAdapter adapter =
                            new ArrayAdapter<String>(DeleteClass.this,R.layout.custom_textview,R.id.tvCustomClassName,classList);
                    lvClassList.setAdapter(adapter);
                    pbClassLoad.setVisibility(View.GONE);
                    ll1.setVisibility(View.VISIBLE);
                    tvEmptyNotification.setVisibility(View.GONE);
                }

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

                new AlertDialog.Builder(DeleteClass.this)
                        .setTitle("Delete Class")
                        .setMessage("Are you sure you want to delete this Class?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference classref = firebaseDatabase.getReference(currentUser.getId());
                                classref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot d: dataSnapshot.getChildren()){
                                            for(DataSnapshot d1: d.getChildren()){
                                                if(d1.getKey().equals("ClassName") && d1.getValue().equals(finalClassName)){
                                                    classkey = d.getKey();
                                                    DatabaseReference deleteref =
                                                            firebaseDatabase.getReference(currentUser.getId())
                                                            .child(classkey);
                                                    deleteref.removeValue();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.ic_warning)
                        .show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
