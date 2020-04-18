package com.believe.attendancemanager;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private GoogleSignInAccount signInAccount;
    private GoogleSignInClient signInClient;

    private TextView userName, userEmail, currentDate, classRemaining;
    private ImageView userImage;

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this,gso);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        signInAccount = getIntent().getParcelableExtra("GSO");

        userName = findViewById(R.id.tvUserName);
        userEmail = findViewById(R.id.tvUserEmail);
        currentDate = findViewById(R.id.tvCurrentDate);
        classRemaining = findViewById(R.id.tvRemainingClass);

        userImage = findViewById(R.id.ivUserImage);

        init();

    }

    private void init(){
        Log.d(TAG, "init: " + signInAccount.getDisplayName() + signInAccount.getEmail() + signInAccount.getPhotoUrl());
        userName.setText(signInAccount.getDisplayName());
        userEmail.setText(signInAccount.getEmail());
        if(signInAccount.getPhotoUrl() != null){
            Picasso.with(this)
                    .load(signInAccount.getPhotoUrl())
                    .into(userImage);
        }
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        currentDate.setText(formattedDate);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        classRemaining.setText(dayOfTheWeek.substring(0,3));

        initList();
    }

    public void initList(){

    }

    public void addClass(android.view.View view){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        if(!dayOfTheWeek.equals("Sunday")){
            startActivity(new Intent(MainActivity.this,AddClass.class));
        }
    }

    public void setting(android.view.View view){
        startActivity(new Intent(MainActivity.this,Settings.class));
    }

    public void signOut(android.view.View view){
        signInClient.signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void TakeAttendance(View view) {
        startActivity(new Intent(MainActivity.this,ChooseClass.class));
    }

    public void ShowAttendance(View view) {
        startActivity(new Intent(MainActivity.this,ShowAttendence.class));
    }
}
