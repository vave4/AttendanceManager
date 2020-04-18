package com.believe.attendancemanager;

import java.util.ArrayList;

public class ClassPojo {

    String studentName, attendance;
    int attNum;

    public ClassPojo() {
    }

    public ClassPojo(String studentName, String attendance, int attNum) {
        this.studentName = studentName;
        this.attendance = attendance;
        this.attNum = attNum;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public int getAttNum() {
        return attNum;
    }

    public void setAttNum(int attNum) {
        this.attNum = attNum;
    }
}
