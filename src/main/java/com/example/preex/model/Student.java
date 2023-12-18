package com.example.preex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Сущность студента.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
@Data
@Entity
@Table(name = "student")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Student implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @NonNull
    @Column(name = "firstname", nullable = false)
    private String firstname;
    @NonNull
    @Column(name = "lastname", nullable = false)
    private String lastname;
    @NonNull
    @Column(name = "username", nullable = false)
    private String username;
    @NonNull
    @Column(name = "password", nullable = false)
    private String password;
}
