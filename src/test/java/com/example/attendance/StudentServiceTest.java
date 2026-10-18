package com.example.attendance;

import com.example.attendance.entity.Student;
import com.example.attendance.repository.StudentRepository;
import com.example.attendance.service.StudentService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudentServiceTest {

    @Test
    void shouldRejectDuplicateRollNumberBeforeSaving() {
        StudentRepository repository = mock(StudentRepository.class);
        StudentService service = new StudentService(repository);
        Student student = new Student();
        student.setRollNumber(" 21 ");

        when(repository.existsByRollNumberIgnoreCase("21")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.saveStudent(student)
        );

        assertEquals("A student with this roll number already exists.", exception.getMessage());
        verify(repository, never()).save(student);
    }

    @Test
    void shouldTrimRollNumberBeforeSaving() {
        StudentRepository repository = mock(StudentRepository.class);
        StudentService service = new StudentService(repository);
        Student student = new Student();
        student.setName("Taylor");
        student.setRollNumber(" 22 ");
        student.setSection(" A ");

        when(repository.existsByRollNumberIgnoreCase("22")).thenReturn(false);
        when(repository.save(student)).thenReturn(student);

        service.saveStudent(student);

        assertEquals("22", student.getRollNumber());
        assertEquals("A", student.getSection());
        assertEquals("A", student.getClassName());
        assertEquals(null, student.getGrade());
        verify(repository).save(student);
    }
}