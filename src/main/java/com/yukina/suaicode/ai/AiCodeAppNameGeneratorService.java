package com.yukina.suaicode.ai;

import dev.langchain4j.service.SystemMessage;

/**
 * 应用名称生成服务
 * 使用结构化输出直接返回枚举类型
 *
 * @author yukina
 */
public interface AiCodeAppNameGeneratorService {

    /**
     * 根据用户需求智能生成应用名称
     *
     * @param userPrompt 用户输入的需求描述
     * @return 应用名称
     */
    @SystemMessage(fromResource = "prompt/codegen-appname-system-prompt.txt")
    String generateAppName(String userPrompt);
}
