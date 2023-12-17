package com.example.preex.service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Обработчик, добавляющий логгирование сервисов приложения.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
@Aspect
@Component
public class ServiceLoggingAspect {

    /**
     * Логгер.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    /**
     * Логгирует запуск создания сущностей.
     *
     * @param joinPoint точка подключения
     */
    @Before("execution(* com.example.preex.service.StudentService.createStudent(*))")
    public void logBeforeMethodCall(JoinPoint joinPoint) {
        LOG.info("Start of new object save: " + Arrays.toString(joinPoint.getArgs()));
    }
}