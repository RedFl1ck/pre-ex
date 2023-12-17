package com.example.preex.service.Impl;

import com.example.preex.model.Student;
import com.example.preex.repository.StudentRepository;
import com.example.preex.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Имплементация сервиса для работы со студентами.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
@Service
public class StudentServiceImpl implements StudentService {

    /**
     * Репозиторий студентов.
     */
    final StudentRepository studentRepository;

    /**
     * Конструктор.
     *
     * @param studentRepository репозиторий студентов
     */
    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void createStudent(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void updateStudent(Student updatedStudent) {
        Student oldStudent = studentRepository.findById(updatedStudent.getId())
                .orElseThrow(() -> new StudentNotFoundException("No Student with id = " + updatedStudent.getId()));
        oldStudent.setFirstname(updatedStudent.getFirstname());
        oldStudent.setLastname(updatedStudent.getLastname());
        studentRepository.save(oldStudent);
    }

    @Override
    public void deleteStudentById(Integer id) {
        Student oldStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("No Student with id = " + id));
        studentRepository.delete(oldStudent);
    }

    @Override
    public Student getStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("No Student with id = " + id));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Студент не найден.
     */
    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) {
            super(message);
        }
    }

}
