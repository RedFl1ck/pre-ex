package com.example.preex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Collection;

/**
 * Сущность студента.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.17
 */
@Data
@Entity
@Table(name = "student", uniqueConstraints = {@UniqueConstraint(columnNames = "mail", name = "unique_mail")})
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class Student implements UserDetails {

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
    @Column(name = "mail", nullable = false)
    private String mail;
    @NonNull
    @Column(name = "username", nullable = false)
    private String username;
    @NonNull
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "account_non_expired")
    private Boolean accountNonExpired = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
