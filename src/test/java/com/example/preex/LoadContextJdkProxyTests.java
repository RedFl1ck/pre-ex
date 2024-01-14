package com.example.preex;

import com.example.preex.repository.StudentRepository;
import com.example.preex.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тест поднятия контекста с JDK proxy.
 *
 * @author Mikhail Nikiforov
 * @since 2023.01.14
 */
@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"})
@AutoConfigureMockMvc
class LoadContextJdkProxyTests {

    /**
     * Менеджер транзакций.
     */
    @Autowired
    protected PlatformTransactionManager transactionManager;

    /**
     * Репозиторий студентов.
     */
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Сервис для работы со студентами.
     */
    @Autowired
    private StudentService studentService;

    @Test
    void contextLoads() {
        assertThat(studentService).isNotNull();
        assertThat(AopUtils.isJdkDynamicProxy(studentService)).isTrue();
        assertThat(studentRepository).isNotNull();
        assertThat(AopUtils.isJdkDynamicProxy(studentRepository)).isTrue();
    }
}
