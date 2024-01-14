package com.example.preex;

import com.example.preex.model.Student;
import com.example.preex.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * Тест контроллера {@link com.example.preex.controller.StudentController}.
 *
 * @author Mikhail Nikiforov
 * @since 2023.01.14
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentControllerTests {

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
     * Пользователь - отправитель запросов.
     */
    private Student principal;

    @BeforeAll
    public void beforeAll() {
        // Создаем пользователя, который будет отправлять запросы
        String testUsername = "testUser";
        principal = createStudent(testUsername);
        studentRepository.save(principal);
    }

    @AfterAll
    public void afterAll() {
        // Удаляем пользователя, который отправляет запросы
        studentRepository.delete(principal);
    }

    /**
     * Тест создания студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    @Test
    public void apiCreateApiStudentTest() throws Exception {
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
        assertThat(studentEntity.getFirstname()).isEqualTo(firstname);
        assertThat(studentEntity.getLastname()).isEqualTo(lastname);

        studentRepository.deleteById(studentEntity.getId());
    }

    /**
     * Тест изменения студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    @Test
    public void apiUpdateStudentTest() throws Exception {
        // given
        Student student = createStudent("username");
        String testFirstName2 = "NewTestFirstName";
        String testLastName2 = "NewTestLastName";

        // when
        String studentUpdatedString = mockMvc.perform(MockMvcRequestBuilders.put(PATH_STUDENT)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(Json.createObjectBuilder()
                                .add("id", student.getId())
                                .add("firstname", testFirstName2)
                                .add("lastname", testLastName2)
                                .build()
                                .toString()))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentUpdatedString).isEqualTo("Student is updated");
        Student studentEntity = studentRepository.findStudentByFirstname(testFirstName2);
        assertThat(studentEntity.getFirstname()).isEqualTo(testFirstName2);
        assertThat(studentEntity.getLastname()).isEqualTo(testLastName2);

        studentRepository.deleteById(studentEntity.getId());
    }

    /**
     * Тест удаления студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    @Test
    public void apiDeleteStudentTest() throws Exception {
        // given
        Student student = createStudent("username");

        // when
        String studentDeletedString = mockMvc.perform(MockMvcRequestBuilders.delete(PATH_STUDENT + "/{id}", student.getId())
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
    @Test
    public void apiNotFoundStudentErrorTest() throws Exception {
        // given
        Integer studentId = Integer.MAX_VALUE;

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PATH_STUDENT + "/{id}", studentId)
                        .with(user(principal))
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    /**
     * Тест ошибки изменения студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    @Test
    public void apiUpdateStudentTestUniqueError() throws Exception {
        // given
        Student student = createStudent("username");
        Integer studentId = student.getId();

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

        studentRepository.deleteById(studentId);
    }

    /**
     * Тест ошибки обновления пароля для истекшего аккаунта студента контроллера {@link com.example.preex.controller.StudentController}.
     *
     * @throws Exception ошибка
     */
    @Test
    public void apiAccountExpiredUpdatePasswordStudentErrorTest() throws Exception {
        // given
        Student student = createStudent("username");
        student.setAccountNonExpired(false);
        studentRepository.save(student);
        Integer studentId = student.getId();

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
        studentRepository.deleteById(studentId);
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
        Student student = new Student(testFirstName1, testLastName1, testMail1, username, password);
        studentRepository.save(student);
        return studentRepository.findStudentByUsername(username);
    }
}
