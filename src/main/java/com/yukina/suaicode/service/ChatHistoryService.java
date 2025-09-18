package com.yukina.suaicode.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.yukina.suaicode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.yukina.suaicode.model.entity.ChatHistory;
import com.yukina.suaicode.model.entity.User;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author <a href="https://github.com/Yukimiyi">yukina</a>
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    boolean addChatHistory(String message, String messageType, Long appId, Long userId);

    public boolean deleteByAppId(Long appId);

    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);

    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                               LocalDateTime lastCreateTime,
                                               User loginUser);
}
