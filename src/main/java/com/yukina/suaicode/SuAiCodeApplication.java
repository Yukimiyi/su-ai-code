package com.yukina.suaicode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
@MapperScan("com.yukina.suaicode.mapper")
public class SuAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuAiCodeApplication.class, args);
    }

}
