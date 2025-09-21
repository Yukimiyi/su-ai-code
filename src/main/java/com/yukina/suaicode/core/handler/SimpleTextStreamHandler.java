package com.yukina.suaicode.core.handler;

import cn.hutool.core.util.StrUtil;
import com.yukina.suaicode.model.entity.User;
import com.yukina.suaicode.model.enums.ChatHistoryMessageTypeEnum;
import com.yukina.suaicode.service.ChatHistoryService;
import reactor.core.publisher.Flux;

public class SimpleTextStreamHandler {
    public Flux<String> handle(Flux<String> originFlux, ChatHistoryService chatHistoryService, long appId, User loginUser) {
        StringBuilder codeBuilder = new StringBuilder();
        return originFlux.map(chunk -> {
                    // 收集 AI 响应内容
                    codeBuilder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> {
                    // 流失响应完成，将AI消息保留到对话
                    String aiResponse = codeBuilder.toString();
                    if (StrUtil.isNotBlank(aiResponse)) {
                        chatHistoryService.addChatHistory(aiResponse, ChatHistoryMessageTypeEnum.AI.getValue(), appId, loginUser.getId());
                    }
                })
                .doOnError(error -> {
                    // 失败时，也将消息保留到历史对话
                    String errorMessage = "AI回复失败" + error.getMessage();
                    chatHistoryService.addChatHistory(errorMessage, ChatHistoryMessageTypeEnum.AI.getValue(), appId, loginUser.getId());
                });
    }
}
