package com.example.preex;

import com.example.preex.model.Student;
import com.example.preex.repository.StudentRepository;
import com.example.preex.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
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

    @Test
    void contextLoads() {
        assertThat(studentService).isNotNull();
        assertThat(AopUtils.isCglibProxy(studentService)).isTrue();
        assertThat(studentRepository).isNotNull();
        assertThat(AopUtils.isJdkDynamicProxy(studentRepository)).isTrue();
    }

    @Test
    void controllerTest() throws Exception {
        // given
        String testFirstName1 = "TestFirstName";
        String testLastName1 = "TestLastName";
        Student student1 = new Student(testFirstName1, testLastName1);

        // when
        String studentCreatedString = mockMvc.perform(MockMvcRequestBuilders.post(PATH_STUDENT)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(student1)))
                .andExpect(status().isCreated()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentCreatedString).isEqualTo("Student is created");

        Student studentEntity = studentRepository.findStudentByFirstname(testFirstName1);
        Integer studentId = studentEntity.getId();
        assertThat(studentEntity.getFirstname()).isEqualTo(testFirstName1);
        assertThat(studentEntity.getLastname()).isEqualTo(testLastName1);

        // given
        String testFirstName2 = "NewTestFirstName";
        String testLastName2 = "NewTestLastName";
        Student student2 = new Student(studentId, testFirstName2, testLastName2);

        // when
        String studentUpdatedString = mockMvc.perform(MockMvcRequestBuilders.put(PATH_STUDENT)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(student2)))
                .andExpect(status().isOk()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentUpdatedString).isEqualTo("Student is updated");
        Student studentEntity2 = studentRepository.findStudentByFirstname(testFirstName2);
        assertThat(studentEntity2.getFirstname()).isEqualTo(testFirstName2);
        assertThat(studentEntity2.getLastname()).isEqualTo(testLastName2);

        // when
        String studentDeletedString = mockMvc.perform(MockMvcRequestBuilders.delete(PATH_STUDENT + "/{id}", studentId)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentDeletedString).isEqualTo("Student is deleted");

        // when
        String studentNotFoundString = mockMvc.perform(MockMvcRequestBuilders.get(PATH_STUDENT + "/{id}", studentId)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().is4xxClientError()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentNotFoundString).isEqualTo("No Student with id = " + studentId);

        // when
        String studentEmptyDataString = mockMvc.perform(MockMvcRequestBuilders.post(PATH_STUDENT)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(Json.createObjectBuilder()
                                .add("id", 1)
                                .build()
                                .toString()))
                .andExpect(status().is5xxServerError()).andReturn().getResponse()
                .getContentAsString();
        // then
        assertThat(studentEmptyDataString).isEqualTo("Параметр Firstname должен быть заполнен\n" +
                "Параметр Lastname должен быть заполнен");
    }
}
