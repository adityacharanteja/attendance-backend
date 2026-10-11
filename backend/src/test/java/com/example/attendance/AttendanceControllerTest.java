package com.example.attendance;

import com.example.attendance.entity.AttendanceRecord;
import com.example.attendance.entity.Student;
import com.example.attendance.repository.AttendanceRepository;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.AttendanceService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceControllerTest {

    @Test
    void shouldReturnAttendanceList() {
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        AttendanceService service = new AttendanceService(attendanceRepository, studentRepository);

        Student student = new Student();
        student.setId(1L);
        student.setName("Alice");

        AttendanceRecord record = new AttendanceRecord();
        record.setId(1L);
        record.setStudent(student);
        record.setDate(LocalDate.of(2026, 10, 10));
        record.setStatus("Present");
        record.setRemarks("On time");

        when(attendanceRepository.findAll()).thenReturn(List.of(record));

        List<AttendanceRecord> result = service.getAllAttendance();

        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getStudentName());
        verify(attendanceRepository).findAll();
    }

    @Test
    void shouldCreateAttendanceRecord() {
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        AttendanceService service = new AttendanceService(attendanceRepository, studentRepository);

        Student student = new Student();
        student.setId(2L);
        student.setName("Bob");

        AttendanceRecord request = new AttendanceRecord();
        request.setStudent(student);
        request.setDate(LocalDate.of(2026, 10, 11));
        request.setStatus("Late");
        request.setArrivalTime(LocalTime.of(8, 15));
        request.setRemarks("Traffic delay");

        AttendanceRecord saved = new AttendanceRecord();
        saved.setId(3L);
        saved.setStudent(student);
        saved.setDate(LocalDate.of(2026, 10, 11));
        saved.setStatus("Late");
        saved.setArrivalTime(LocalTime.of(8, 15));
        saved.setRemarks("Traffic delay");

        when(studentRepository.findById(2L)).thenReturn(java.util.Optional.of(student));
        when(attendanceRepository.findFirstByStudent_IdAndDate(2L, LocalDate.of(2026, 10, 11)))
            .thenReturn(java.util.Optional.empty());
        when(attendanceRepository.save(request)).thenReturn(saved);

        AttendanceRecord result = service.createAttendance(request);

        assertEquals("Bob", result.getStudentName());
        assertEquals("Late", result.getStatus());
        assertEquals(LocalTime.of(8, 15), result.getArrivalTime());
        verify(attendanceRepository).save(request);
    }

    @Test
    void shouldUpdateExistingAttendanceForStudentAndDate() {
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        AttendanceService service = new AttendanceService(attendanceRepository, studentRepository);
        Student student = new Student();
        student.setId(4L);
        student.setName("Casey");

        AttendanceRecord request = new AttendanceRecord();
        request.setStudent(student);
        request.setDate(LocalDate.of(2026, 10, 12));
        request.setStatus("Absent");
        request.setArrivalTime(null);
        request.setRemarks("Not in class");

        AttendanceRecord existing = new AttendanceRecord();
        existing.setId(8L);
        existing.setStudent(student);
        existing.setDate(request.getDate());
        existing.setStatus("Present");

        when(studentRepository.findById(4L)).thenReturn(java.util.Optional.of(student));
        when(attendanceRepository.findFirstByStudent_IdAndDate(4L, request.getDate()))
                .thenReturn(java.util.Optional.of(existing));
        when(attendanceRepository.save(existing)).thenReturn(existing);

        AttendanceRecord result = service.createAttendance(request);

        assertSame(existing, result);
        assertEquals("Absent", result.getStatus());
        assertNull(result.getArrivalTime());
        assertEquals("Not in class", result.getRemarks());
        verify(attendanceRepository).save(existing);
        verify(attendanceRepository, never()).save(request);
    }

    @Test
    void shouldRequireArrivalTimeForLateAttendance() {
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        AttendanceService service = new AttendanceService(attendanceRepository, studentRepository);
        AttendanceRecord request = new AttendanceRecord();
        Student student = new Student();
        student.setId(5L);
        request.setStudent(student);
        request.setDate(LocalDate.of(2026, 10, 13));
        request.setStatus("Late");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createAttendance(request)
        );

        assertEquals("Enter the late arrival time.", exception.getMessage());
        verify(studentRepository, never()).findById(5L);
        verify(attendanceRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void shouldDeleteAttendanceRecordById() {
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        AttendanceService service = new AttendanceService(attendanceRepository, studentRepository);

        doNothing().when(attendanceRepository).deleteById(10L);

        service.deleteAttendance(10L);

        verify(attendanceRepository).deleteById(10L);
    }

    @Test
    void shouldClearAttendanceRecordsForSelectedDate() {
        AttendanceRepository attendanceRepository = mock(AttendanceRepository.class);
        StudentRepository studentRepository = mock(StudentRepository.class);
        AttendanceService service = new AttendanceService(attendanceRepository, studentRepository);

        when(attendanceRepository.findAllByDate(LocalDate.of(2026, 10, 14)))
                .thenReturn(List.of(new AttendanceRecord()));
        doNothing().when(attendanceRepository).deleteAll(anyList());

        service.clearAttendanceForDate(LocalDate.of(2026, 10, 14));

        verify(attendanceRepository).findAllByDate(LocalDate.of(2026, 10, 14));
        verify(attendanceRepository).deleteAll(anyList());
    }
}
