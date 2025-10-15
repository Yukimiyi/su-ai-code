package com.yukina.suaicode.ai;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeAppNameGeneratorServiceFactoryTest {

    @Resource
    private AiCodeAppNameGeneratorServiceFactory aiCodeAppNameGeneratorServiceFactory;

    @Test
    void createAiCodeAppNameGeneratorService() {
        AiCodeAppNameGeneratorService service = aiCodeAppNameGeneratorServiceFactory.createAiCodeAppNameGeneratorService();
        System.out.println(service.generateAppName("创建一个个人博客网站，不超过200行代码"));
    }

}