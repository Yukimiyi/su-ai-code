package com.yukina.suaicode.ai.tools;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ToolManager {

    private final Map<String, BaseTool> toolMap = new HashMap<>();

    @Resource
    private BaseTool[] baseTools;

    @PostConstruct
    public void initTools() {
        for (BaseTool baseTool : baseTools) {
            toolMap.put(baseTool.getToolName(), baseTool);
            log.info("注册工具：{} -> {}", baseTool.getToolName(), baseTool.getDisplayName());
        }
        log.info("工具管理器注册完成，共 {} 个工具", toolMap.size());
    }

    /**
     * 根据工具名称获取工具对象
     *
     * @param toolName 工具名称
     * @return 工具对象
     */
    public BaseTool getTool(String toolName) {
        return toolMap.get(toolName);
    }

    public BaseTool[] getAllTools() {
        return baseTools;
    }
}
