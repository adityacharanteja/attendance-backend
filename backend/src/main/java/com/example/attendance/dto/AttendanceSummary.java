package com.example.attendance.dto;

public class AttendanceSummary {
    private Long studentId;
    private String studentName;
    private long totalDays;
    private long present;
    private long absent;
    private long late;
    private double attendanceRate;

    public AttendanceSummary() {
    }

    public AttendanceSummary(Long studentId, String studentName, long totalDays, long present, long absent, long late, double attendanceRate) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalDays = totalDays;
        this.present = present;
        this.absent = absent;
        this.late = late;
        this.attendanceRate = attendanceRate;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public long getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(long totalDays) {
        this.totalDays = totalDays;
    }

    public long getPresent() {
        return present;
    }

    public void setPresent(long present) {
        this.present = present;
    }

    public long getAbsent() {
        return absent;
    }

    public void setAbsent(long absent) {
        this.absent = absent;
    }

    public long getLate() {
        return late;
    }

    public void setLate(long late) {
        this.late = late;
    }

    public double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }
}
