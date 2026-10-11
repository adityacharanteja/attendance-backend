package com.example.attendance.controller;

import com.example.attendance.dto.AttendanceSummary;
import com.example.attendance.entity.AttendanceRecord;
import com.example.attendance.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/attendance")
    public List<AttendanceRecord> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }

    @GetMapping("/attendance/summary")
    public List<AttendanceSummary> getAttendanceSummary() {
        return attendanceService.getAttendanceSummary();
    }

    @PostMapping("/attendance")
    public ResponseEntity<AttendanceRecord> createAttendance(@RequestBody AttendanceRecord record) {
        AttendanceRecord saved = attendanceService.createAttendance(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/attendance/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/attendance/date/{date}")
    public ResponseEntity<Void> clearAttendanceForDate(@PathVariable String date) {
        attendanceService.clearAttendanceForDate(LocalDate.parse(date));
        return ResponseEntity.noContent().build();
    }
}
