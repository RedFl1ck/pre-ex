package com.example.preex.controller;

import com.example.preex.model.Student;
import com.example.preex.service.Impl.StudentServiceImpl;
import com.example.preex.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
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
     * Обновление студента по ИД.
     *
     * @param id             ИД студента
     * @param updatedStudent модель студента
     * @return сообщение об успешном выполнении операции
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateStudentById(@PathVariable Integer id, @RequestBody Student updatedStudent) {
        updatedStudent.setId(id);
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
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public String notFoundException(StudentServiceImpl.StudentNotFoundException exception) {
        return exception.getMessage();
    }

    /**
     * Обработка ошибки передачи студента.
     *
     * @param exception      ошибка
     * @param servletRequest объект запроса
     * @param principal      текущий пользователь
     * @return ошибка
     */
    @SuppressWarnings("ThrowableNotThrown")
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationExceptionByPath(ConstraintViolationException exception, HttpServletRequest servletRequest, Principal principal) {
        String message = NestedExceptionUtils.getRootCause(exception) != null ?
                NestedExceptionUtils.getRootCause(exception).getMessage() : exception.getMessage();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);

        Integer studentId = Integer.parseInt(servletRequest.getPathInfo().replace(PATH_STUDENT + "/", ""));
        Student student = studentService.getStudentById(studentId);
        if (student.getUsername().equals(principal.getName())) {
            stringBuilder.append("\n");
            stringBuilder.append("Ваш текущий e-mail = ");
            stringBuilder.append(student.getMail());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(stringBuilder.toString());
    }

    /**
     * Обработка ошибки передачи студента.
     *
     * @param exception ошибка
     * @param request   объект запроса
     * @param principal текущий пользователь
     * @return ошибка
     */
    @SuppressWarnings("ThrowableNotThrown")
//    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException exception, ContentCachingRequestWrapper request, Principal principal) {
        try {
            String message = NestedExceptionUtils.getRootCause(exception) != null ?
                    NestedExceptionUtils.getRootCause(exception).getMessage() : exception.getMessage();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(message);

            Integer studentId = objectMapper.readValue(request.getContentAsByteArray(), Student.class).getId();
            if (studentId != null) {
                Student student = studentService.getStudentById(studentId);
                if (student.getUsername().equals(principal.getName())) {
                    stringBuilder.append("\n");
                    stringBuilder.append("Ваш текущий e-mail = ");
                    stringBuilder.append(student.getMail());
                }
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(stringBuilder.toString());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неправильно передан студент");
        }
    }
}
