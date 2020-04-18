package com.believe.attendancemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class customAttendenceListAdapter extends BaseAdapter {

    private static final String TAG = "customAttendenceListAda";

    private ArrayList<StudentAttendencePojo> attendencePojoArrayList;
    private String classkey, classname;
    private Activity activity;
    private int totalClasses;
    private static LayoutInflater inflater=null;

    public customAttendenceListAdapter(ArrayList<StudentAttendencePojo> attendencePojoArrayList, String classkey, String classname, Activity activity, int totalClasses) {
        this.attendencePojoArrayList = attendencePojoArrayList;
        this.classkey = classkey;
        this.classname = classname;
        this.activity = activity;
        this.totalClasses = totalClasses;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return attendencePojoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.custom_student_attendence, null);
        TextView tvStudentNameAtt = vi.findViewById(R.id.tvStudentNameAtt); // title
        TextView tvAttPercentage = vi.findViewById(R.id.tvAttPercentage); // artist name

        Log.i(TAG, "getView: " + totalClasses);

        tvStudentNameAtt.setText(attendencePojoArrayList.get(position).getStudentName());

        if(totalClasses==0){
            tvAttPercentage.setText("0 %");
        }else{
            int finalperc = (Integer.parseInt(attendencePojoArrayList.get(position).getStudentAttendence())*100)/totalClasses;
            tvAttPercentage.setText(finalperc+" %");
            if(finalperc>=75){
                tvAttPercentage.setBackground(activity.getDrawable(R.drawable.btgreen));
            }else {
                tvAttPercentage.setBackground(activity.getDrawable(R.drawable.btred));
            }
        }
        return vi;
    }
}
