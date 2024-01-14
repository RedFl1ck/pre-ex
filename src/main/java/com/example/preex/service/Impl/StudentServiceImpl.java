package com.example.preex.service.Impl;

import com.example.preex.model.Student;
import com.example.preex.repository.StudentRepository;
import com.example.preex.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

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
                .orElseThrow(() -> new StudentNotFoundException(updatedStudent.getId()));
        if (updatedStudent.getPassword() != null) {
            if (!oldStudent.isAccountNonExpired()) {
                throw new AccountIsExpiredException();
            }
            oldStudent.setPassword(updatedStudent.getPassword());
        }
        if (updatedStudent.getFirstname() != null) {
            oldStudent.setFirstname(updatedStudent.getFirstname());
        }
        if (updatedStudent.getLastname() != null) {
            oldStudent.setLastname(updatedStudent.getLastname());
        }
        if (updatedStudent.getMail() != null) {
            oldStudent.setMail(updatedStudent.getMail());
        }
        studentRepository.save(oldStudent);
    }

    @Override
    public void deleteStudentById(Integer id) {
        Student oldStudent = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.delete(oldStudent);
    }

    @Override
    public Student getStudentById(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Override
    public Student getStudentByUsername(String username) {
        return studentRepository.findStudentByUsername(username);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Студент не найден.
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public static class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(Integer id) {
            super("No Student with id = " + id);
        }
    }

    /**
     * Истек срок действия аккаунта.
     */
    public static class AccountIsExpiredException extends RuntimeException {
        public AccountIsExpiredException() {
            super("Cannot update password because account is expired");
        }
    }

}
