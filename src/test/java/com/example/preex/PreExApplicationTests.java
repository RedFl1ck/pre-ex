package com.example.preex;

import com.example.preex.model.Student;
import com.example.preex.repository.StudentRepository;
import com.example.preex.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.PlatformTransactionManager;

import javax.json.Json;

import static com.example.preex.controller.StudentController.PATH_STUDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PreExApplicationTests {

    /**
     * Spring MVC mock.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Менеджер транзакций.
     */
    @Autowired
    protected PlatformTransactionManager transactionManager;

    /**
     * Сериализатор.
     */
    @Autowired
    private ObjectMapper objectMapper;

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

    @Value("${spring.aop.proxy-target-class}")
    private Boolean proxyTargetClass;

    /**
     * Пользователь - отправитель запросов.
     */
    private Student principal;

    @Test
    void contextLoads() {
        assertThat(studentService).isNotNull();
        if (proxyTargetClass) {
            assertThat(AopUtils.isCglibProxy(studentService)).isTrue();
        } else {
            assertThat(AopUtils.isJdkDynamicProxy(studentService)).isTrue();
        }
        assertThat(studentRepository).isNotNull();
        assertThat(AopUtils.isJdkDynamicProxy(studentRepository)).isTrue();
    }

    /**
     * Тест контроллера студентов {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    @Test
    void controllerTest() throws Exception {
        // given
        // Создаем пользователя, который будет отправлять запросы
        String testUsername = "testUser";
        principal = createStudent(testUsername);
        studentRepository.save(principal);

        Integer studentId = apiCreateApiStudentTest();
        apiUpdateStudentTest(studentId);
        apiUpdateStudentTestUniqueError(studentId);
        apiAccountExpiredUpdatePasswordStudentErrorTest(studentId);
        apiDeleteStudentTest(studentId);
        apiNotFoundStudentErrorTest(studentId);
    }

    /**
     * Тест создания студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @return ИД созданного студента
     * @throws Exception ошибка
     */
    private Integer apiCreateApiStudentTest() throws Exception {
        Student student = createStudent("username");
        String firstname = student.getFirstname();
        String lastname = student.getLastname();

        // when
        String studentCreatedString = mockMvc.perform(MockMvcRequestBuilders.post(PATH_STUDENT)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isCreated()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentCreatedString).isEqualTo("Student is created");
        Student studentEntity = studentRepository.findStudentByFirstname(firstname);
        Integer studentId = studentEntity.getId();
        assertThat(studentEntity.getFirstname()).isEqualTo(firstname);
        assertThat(studentEntity.getLastname()).isEqualTo(lastname);
        return studentId;
    }

    /**
     * Тест изменения студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    private void apiUpdateStudentTest(Integer studentId) throws Exception {
        // given
        String testFirstName2 = "NewTestFirstName";
        String testLastName2 = "NewTestLastName";

        // when
        String studentUpdatedString = mockMvc.perform(MockMvcRequestBuilders.put(PATH_STUDENT)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(Json.createObjectBuilder()
                                .add("id", studentId)
                                .add("firstname", testFirstName2)
                                .add("lastname", testLastName2)
                                .build()
                                .toString()))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentUpdatedString).isEqualTo("Student is updated");
        Student studentEntity2 = studentRepository.findStudentByFirstname(testFirstName2);
        assertThat(studentEntity2.getFirstname()).isEqualTo(testFirstName2);
        assertThat(studentEntity2.getLastname()).isEqualTo(testLastName2);
    }

    /**
     * Тест удаления студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    private void apiDeleteStudentTest(Integer studentId) throws Exception {
        // when
        String studentDeletedString = mockMvc.perform(MockMvcRequestBuilders.delete(PATH_STUDENT + "/{id}", studentId)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentDeletedString).isEqualTo("Student is deleted");
    }

    /**
     * Тест ошибки поиска студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    private void apiNotFoundStudentErrorTest(Integer studentId) throws Exception {
        // when
        String studentNotFoundString = mockMvc.perform(MockMvcRequestBuilders.get(PATH_STUDENT + "/{id}", studentId)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().is4xxClientError()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentNotFoundString).isEqualTo("No Student with id = " + studentId);
    }

    /**
     * Тест ошибки изменения студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    private void apiUpdateStudentTestUniqueError(Integer studentId) throws Exception {
        // when
        String result = mockMvc.perform(MockMvcRequestBuilders.put(PATH_STUDENT + "/{id}", studentId)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(Json.createObjectBuilder()
                                .add("mail", principal.getMail())
                                .build()
                                .toString()))
                .andExpect(status().is4xxClientError()).andReturn().getResponse()
                .getContentAsString();

        // then
        assertThat(result).isEqualTo("ERROR: duplicate key value violates unique constraint \"unique_mail\"\n" +
                "  Подробности: Key (mail)=(testUser_test@mail.ru) already exists.");

        // when
        result = mockMvc.perform(MockMvcRequestBuilders.put(PATH_STUDENT)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(Json.createObjectBuilder()
                                .add("id", studentId)
                                .add("mail", principal.getMail())
                                .build()
                                .toString()))
                .andExpect(status().is4xxClientError()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(result).isEqualTo("ERROR: duplicate key value violates unique constraint \"unique_mail\"\n" +
                "  Подробности: Key (mail)=(testUser_test@mail.ru) already exists.");

        Student student = studentRepository.findById(studentId).get();

        // when
        result = mockMvc.perform(MockMvcRequestBuilders.put(PATH_STUDENT)
                        .with(user(student))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(Json.createObjectBuilder()
                                .add("id", studentId)
                                .add("mail", principal.getMail())
                                .build()
                                .toString()))
                .andExpect(status().is4xxClientError()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(result).isEqualTo("ERROR: duplicate key value violates unique constraint \"unique_mail\"\n" +
                "  Подробности: Key (mail)=(testUser_test@mail.ru) already exists.\n" +
                "Ваш текущий e-mail = username_test@mail.ru");
    }

    /**
     * Тест ошибки обновления пароля для истекшего аккаунта студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    private void apiAccountExpiredUpdatePasswordStudentErrorTest(Integer studentId) throws Exception {
        Student student = studentRepository.findById(studentId).get();
        student.setAccountNonExpired(false);
        studentRepository.save(student);

        String studentUpdatedString = mockMvc.perform(MockMvcRequestBuilders.put(PATH_STUDENT)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(Json.createObjectBuilder()
                                .add("id", studentId)
                                .add("password", "test")
                                .build()
                                .toString()))
                .andExpect(status().is5xxServerError()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentUpdatedString).isEqualTo("Cannot update password because account is expired");
    }

    /**
     * Создание модели студента.
     *
     * @param username имя пользователя
     * @return студент
     */
    private Student createStudent(String username) {
        String testFirstName1 = username + "_TestFirstName";
        String testLastName1 = username + "_TestLastName";
        String testMail1 = username + "_test@mail.ru";
        String password = "test";
        return new Student(testFirstName1, testLastName1, testMail1, username, password);
    }
}
