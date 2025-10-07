package com.yukina;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yukina.suaicode.mapper")
@EnableDubbo
public class SuAiCodeUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuAiCodeUserApplication.class, args);
    }

}
