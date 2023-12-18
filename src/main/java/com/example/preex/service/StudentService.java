package com.example.preex.service;

import com.example.preex.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для работы со студентами.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
@Service
public interface StudentService {

    /**
     * Создание студента.
     *
     * @param student модель студента
     */
    void createStudent(Student student);

    /**
     * Обновление студента.
     *
     * @param updatedStudent модель студента
     */
    void updateStudent(Student updatedStudent);

    /**
     * Удаление студента.
     *
     * @param id ИД студента
     */
    void deleteStudentById(Integer id);

    /**
     * Получение студента по ИД.
     *
     * @param id ИД студента
     * @return студент
     */
    Student getStudentById(Integer id);

    /**
     * Получение студента по лошину.
     *
     * @param username логин студента
     * @return студент
     */
    Student getStudentByUsername(String username);

    /**
     * Получение всех студентов.
     *
     * @return список всех студентов
     */
    List<Student> getAllStudents();
}
