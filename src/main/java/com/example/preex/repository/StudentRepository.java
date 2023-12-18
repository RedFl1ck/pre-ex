package com.example.preex.repository;

import com.example.preex.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий студентов.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    Student findStudentByFirstname(String firstname);

    Student findStudentByUsername(String username);
}