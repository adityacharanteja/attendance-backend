package com.example.attendance.repository;

import com.example.attendance.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
	Optional<AttendanceRecord> findFirstByStudent_IdAndDate(Long studentId, LocalDate date);
	List<AttendanceRecord> findAllByDate(LocalDate date);
}
