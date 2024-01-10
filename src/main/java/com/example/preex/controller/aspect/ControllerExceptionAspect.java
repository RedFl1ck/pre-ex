package com.example.preex.controller.aspect;

import com.example.preex.model.Student;
import com.example.preex.service.StudentService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Обработчик, добавляющий обработку ошибок контроллера.
 *
 * @author Mikhail Nikiforov
 * @since 2023.01.08
 */
@Aspect
@Component
public class ControllerExceptionAspect {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionAspect.class);

    /**
     * Сервис для работы со студентами.
     */
    private final StudentService studentService;

    /**
     * Конструктор.
     *
     * @param studentService сервис для работы со студентами
     */
    public ControllerExceptionAspect(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * Обрабатывает ошибку {@link ConstraintViolationException}.
     *
     * @param joinPoint точка подключения
     */
    @Around("execution(* com.example.preex.controller.StudentController.*(com.example.preex.model.Student))")
    public ResponseEntity<String> handleException(ProceedingJoinPoint joinPoint) {
        try {
            return (ResponseEntity<String>) joinPoint.proceed();
        } catch (Throwable exception) {
            LOG.info("Handling exception: " + exception);
            if (exception instanceof DataIntegrityViolationException) {
                String message = NestedExceptionUtils.getRootCause(exception) != null ? NestedExceptionUtils.getRootCause(exception)
                        .getMessage() : exception.getMessage();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(message);

                Integer studentId = ((Student) Arrays.asList(joinPoint.getArgs()).get(0)).getId();
                if (studentId != null) {
                    Student student = studentService.getStudentById(studentId);
                    Authentication principal = SecurityContextHolder.getContext().getAuthentication();
                    if (student.getUsername().equals(principal.getName())) {
                        stringBuilder.append("\n");
                        stringBuilder.append("Ваш текущий e-mail = ");
                        stringBuilder.append(student.getMail());
                    }
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(stringBuilder.toString());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неправильно передан студент");
            }
        }
    }
}