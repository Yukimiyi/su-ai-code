package com.yukina.suaicode.ai;

import com.yukina.suaicode.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用名称生成服务工厂
 *
 * @author yukina
 */
@Slf4j
@Configuration
public class AiCodeAppNameGeneratorServiceFactory {


    /**
     * 创建AI代码生成类型路由服务实例
     */
    public AiCodeAppNameGeneratorService createAiCodeAppNameGeneratorService() {
        ChatModel chatModel = SpringContextUtil.getBean("appNameChatModelPrototype", ChatModel.class);
        return AiServices.builder(AiCodeAppNameGeneratorService.class)
                .chatModel(chatModel)
                .build();
    }

    /**
     * 默认提供一个 Bean
     * @return
     */
    @Bean
    public AiCodeAppNameGeneratorService aiCodeAppNameGeneratorService() {
        return createAiCodeAppNameGeneratorService();
    }
}
