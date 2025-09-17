package com.yukina.suaicode.core;

import com.yukina.suaicode.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

@SpringBootTest
class AiCodeGeneratorFacadeTest {
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveHtmlCode() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode(CodeGenTypeEnum.HTML, "任务记录网站，不超过50行代码", 1L);
        Assertions.assertNotNull(file);
    }

    @Test
    void generateAndSaveMultiFileCode() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode(CodeGenTypeEnum.MULTI_FILE, "任务记录网站，不超过50行代码", 1L);
        Assertions.assertNotNull(file);
    }

    @Test
    void generateAndSaveHtmlCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(CodeGenTypeEnum.HTML, "任务记录网站，不超过50行代码", 1L);
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }

    @Test
    void generateAndSaveMultiFileCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(CodeGenTypeEnum.MULTI_FILE, "任务记录网站，不超过100行代码", 1L);
        List<String> result = codeStream.collectList().block();
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }
}