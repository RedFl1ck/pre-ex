package com.example.preex.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация настроек веб безопасности.
 *
 * @author Mikhail Nikiforov
 * @since 2023.12.18
 */
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Разрешаем отправлять запросы всем авторизованным пользователм.
     *
     * @return цепочка фильтра
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll().anyRequest().authenticated();
        return http.build();
    }
}
