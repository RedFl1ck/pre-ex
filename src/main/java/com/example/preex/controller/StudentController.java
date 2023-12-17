package com.example.preex.controller;

import com.example.preex.model.Student;
import com.example.preex.service.Impl.StudentServiceImpl;
import com.example.preex.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Контроллер для работы со студентами.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
@RestController
@RequestMapping(StudentController.PATH_STUDENT)
public class StudentController {

    /**
     * Общий путь до точки студентов.
     */
    public static final String PATH_STUDENT = "/api/student";

    /**
     * Сериализатор.
     */
    private final ObjectMapper objectMapper;

    /**
     * Сервис для работы со студентами.
     */
    private final StudentService studentService;

    /**
     * Конструктор.
     *
     * @param objectMapper   сериализатор
     * @param studentService сервис для работы со студентами
     */
    public StudentController(ObjectMapper objectMapper, StudentService studentService) {
        this.objectMapper = objectMapper;
        this.studentService = studentService;
    }

    /**
     * Создание студента.
     *
     * @param student модель студента
     * @return сообщение об успешном выполнении операции
     */
    @PostMapping
    public ResponseEntity<String> createStudent(@RequestBody Student student) {
        studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body("Student is created");
    }

    /**
     * Обновление студента.
     *
     * @param updatedStudent модель студента
     * @return сообщение об успешном выполнении операции
     */
    @PutMapping
    public ResponseEntity<String> updateStudent(@RequestBody Student updatedStudent) {
        studentService.updateStudent(updatedStudent);
        return ResponseEntity.ok("Student is updated");
    }

    /**
     * Удаление студента.
     *
     * @param id ИД студента
     * @return сообщение об успешном выполнении операции
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudentById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Student is deleted");
    }

    /**
     * Получение студента по ИД.
     *
     * @param id ИД студента
     * @return студент
     */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable Integer id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    /**
     * Получение всех студентов.
     *
     * @return список всех студентов
     */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    /**
     * Обработка ошибки поиска студента.
     *
     * @param exception ошибка поиска студента
     * @return ошибка
     */
    @ExceptionHandler(StudentServiceImpl.StudentNotFoundException.class)
    public ResponseEntity<String> notFoundException(StudentServiceImpl.StudentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    /**
     * Обработка ошибки передачи студента.
     *
     * @param request объект запроса
     * @return ошибка
     */
    @ExceptionHandler(PropertyValueException.class)
    public ResponseEntity<String> studentEmptyDataException(HttpServletRequest request) {
        try {
            Student student = objectMapper.readValue(request.getInputStream(), Student.class);
            StringBuilder stringBuilder = new StringBuilder();
            if (student.getFirstname() == null) {
                stringBuilder.append("Параметр Firstname должен быть заполнен");
            }
            if (student.getLastname() == null) {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append("Параметр Lastname должен быть заполнен");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(stringBuilder.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Неправильно передан студент");
        }
    }
}
