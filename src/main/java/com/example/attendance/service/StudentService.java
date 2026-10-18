package com.example.attendance.service;

import com.example.attendance.entity.Student;
import com.example.attendance.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student saveStudent(Student student) {
        String rollNumber = student.getRollNumber() == null ? "" : student.getRollNumber().trim();
        if (rollNumber.isEmpty()) {
            throw new IllegalArgumentException("Enter a roll number.");
        }
        if (studentRepository.existsByRollNumberIgnoreCase(rollNumber)) {
            throw new IllegalArgumentException("A student with this roll number already exists.");
        }
        String name = student.getName() == null ? "" : student.getName().trim();
        String grade = student.getGrade() == null ? "" : student.getGrade().trim();
        String section = student.getSection() == null ? "" : student.getSection().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Enter the student's name.");
        }
        if (!grade.isEmpty() && !section.isEmpty()) {
            student.setGrade(grade);
            student.setSection(section);
            student.setClassName("Grade " + grade + ", Section " + section);
        } else if (!section.isEmpty()) {
            student.setGrade(null);
            student.setSection(section);
            student.setClassName(section);
        } else if (student.getClassName() == null || student.getClassName().isBlank()) {
            throw new IllegalArgumentException("Enter a section.");
        } else {
            student.setClassName(student.getClassName().trim());
        }
        student.setName(name);
        student.setRollNumber(rollNumber);
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
    }
}
