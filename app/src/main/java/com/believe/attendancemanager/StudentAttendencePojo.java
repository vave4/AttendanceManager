package com.believe.attendancemanager;

public class StudentAttendencePojo {

    String studentName, studentAttendence;

    public StudentAttendencePojo(String studentName, String studentAttendence) {
        this.studentName = studentName;
        this.studentAttendence = studentAttendence;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentAttendence() {
        return studentAttendence;
    }

    public void setStudentAttendence(String studentAttendence) {
        this.studentAttendence = studentAttendence;
    }
}
