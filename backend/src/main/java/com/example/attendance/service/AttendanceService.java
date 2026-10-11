package com.example.attendance.service;

import com.example.attendance.dto.AttendanceSummary;
import com.example.attendance.entity.AttendanceRecord;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    public List<AttendanceRecord> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    public void clearAttendanceForDate(java.time.LocalDate date) {
        List<AttendanceRecord> records = attendanceRepository.findAllByDate(date);
        if (!records.isEmpty()) {
            attendanceRepository.deleteAll(records);
        }
    }

    public AttendanceRecord createAttendance(AttendanceRecord record) {
        if (record.getStudent() == null || record.getStudent().getId() == null) {
            throw new IllegalArgumentException("Select a student.");
        }
        if (record.getDate() == null) {
            throw new IllegalArgumentException("Select an attendance date.");
        }
        if (record.getStatus() == null || !Set.of("Present", "Absent", "Late")
                .contains(record.getStatus())) {
            throw new IllegalArgumentException("Choose Present, Absent, or Late.");
        }
        if ("Late".equals(record.getStatus()) && record.getArrivalTime() == null) {
            throw new IllegalArgumentException("Enter the late arrival time.");
        }
        if (!"Late".equals(record.getStatus())) {
            record.setArrivalTime(null);
        }

        Long studentId = record.getStudent().getId();
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        AttendanceRecord existing = attendanceRepository
                .findFirstByStudent_IdAndDate(studentId, record.getDate())
                .orElse(null);
        if (existing != null) {
            existing.setStatus(record.getStatus());
            existing.setArrivalTime(record.getArrivalTime());
            existing.setRemarks(record.getRemarks());
            return attendanceRepository.save(existing);
        }

        record.setStudent(student);
        return attendanceRepository.save(record);
    }

    public List<AttendanceSummary> getAttendanceSummary() {
        List<Student> students = studentRepository.findAll();
        List<AttendanceSummary> summaries = new ArrayList<>();

        for (Student student : students) {
            List<AttendanceRecord> records = attendanceRepository.findAll().stream()
                    .filter(record -> record.getStudent() != null && record.getStudent().getId().equals(student.getId()))
                    .toList();

            long present = records.stream().filter(r -> "Present".equalsIgnoreCase(r.getStatus())).count();
            long absent = records.stream().filter(r -> "Absent".equalsIgnoreCase(r.getStatus())).count();
            long late = records.stream().filter(r -> "Late".equalsIgnoreCase(r.getStatus())).count();
            long totalDays = records.size();
            double rate = totalDays == 0 ? 0 : (present * 100.0) / totalDays;

            summaries.add(new AttendanceSummary(student.getId(), student.getName(), totalDays, present, absent, late, rate));
        }

        return summaries;
    }
}
