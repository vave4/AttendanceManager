package com.believe.attendancemanager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StuRecyclerViewAdapter extends RecyclerView.Adapter<StuRecyclerViewAdapter.MyViewHolder>{

    private static final String TAG = "StuRecyclerViewAdapter";

    private String classkey, currentUser;
    private Activity activity;
    private ArrayList<ClassPojo> stuList;
    private List<Integer> selected;
    private int previousClasses;
    private DatabaseReference totalClassesref;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvStudentName;

        public MyViewHolder(View view) {
            super(view);
            tvStudentName = view.findViewById(R.id.tvStudentName);
            tvStudentName.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            int position = getLayoutPosition();

//            Log.i("POSITION",position+"");
////
////            if (!selected.contains(position))
////                selected.add(position);
////            else
////                selected.remove(position);
            //0 Present
            //1 Absent
            if(selected.get(position)==0){
                selected.set(position,1);
            }else {
                selected.set(position,0);
            }

            Log.d(TAG, "onClick: " + selected);

            notifyDataSetChanged();
        }
    }

    public StuRecyclerViewAdapter(String classkey, String currentUser, final Activity activity, ArrayList<ClassPojo> stuList, List<Integer> selected) {
        this.classkey = classkey;
        this.currentUser = currentUser;
        this.activity = activity;
        this.stuList = stuList;
        this.selected = selected;
        for(int i=0; i<stuList.size(); i++){
            selected.add(0,0);
        }
        totalClassesref =
                FirebaseDatabase.getInstance()
                        .getReference(currentUser)
                        .child(classkey)
                        .child("TotalClasses");
        totalClassesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                try{
                    previousClasses = Integer.parseInt(dataSnapshot.getValue().toString());
                }catch (NullPointerException e){
                    previousClasses = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_stuname, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        ClassPojo classPojo = stuList.get(position);
        holder.tvStudentName.setText(classPojo.getStudentName());

        holder.tvStudentName.setBackgroundColor(activity.getResources().getColor(selected.get(position)==1?R.color.red:R.color.green));
    }

    @Override
    public int getItemCount() {
        return stuList == null ? 0 : stuList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void uploadDataToDatabase(){
        Log.d(TAG, "uploadDataToDatabase: " + classkey +" - "+ currentUser +" - "+ stuList.get(1).getStudentName() +" - "+ stuList.get(1).getAttendance() +" - "+ stuList.get(1).getAttNum());
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code

                for(int i=0;i<stuList.size();i++){
                    if(selected.get(i)==0){
                        //present
                        DatabaseReference reference =
                                FirebaseDatabase.getInstance()
                                        .getReference(currentUser)
                                        .child(classkey)
                                        .child("StudentNameList")
                                        .child(stuList.get(i).getStudentName());
                        reference.setValue(stuList.get(i).getAttNum()+1);
                    }
                }
                totalClassesref.setValue(previousClasses+1);
            }
        });

    }

}