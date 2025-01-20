// ImpalaExampleApplication.java
package com.example.impala.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.impala.example.mapper")
public class ImpalaExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImpalaExampleApplication.class, args);
    }
}