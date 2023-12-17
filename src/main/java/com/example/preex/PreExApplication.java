package com.example.preex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class PreExApplication {

    public static void main(String[] args) {
        SpringApplication.run(PreExApplication.class, args);
    }

}
