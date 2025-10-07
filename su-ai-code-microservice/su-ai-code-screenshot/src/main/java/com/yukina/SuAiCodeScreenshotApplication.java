package com.yukina;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class SuAiCodeScreenshotApplication {
    public static void main(String[] args) {
        SpringApplication.run(SuAiCodeScreenshotApplication.class, args);
    }
}
