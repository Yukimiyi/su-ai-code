package com.yukina.suaicode.core;

import com.yukina.suaicode.ai.AiCodeGeneratorService;
import com.yukina.suaicode.ai.model.HtmlCodeResult;
import com.yukina.suaicode.ai.model.MultiFileCodeResult;
import com.yukina.suaicode.exception.BusinessException;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成器外观类，组合生成代码和保存的功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 生成代码并保存
     *
     * @param codeGenTypeEnum
     * @param userMessage
     */
    public File generateAndSaveCode(CodeGenTypeEnum codeGenTypeEnum, String userMessage) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage);
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型" + codeGenTypeEnum.getValue());
        };
    }

    /**
     * 生成代码并保存(保存)
     *
     * @param codeGenTypeEnum
     * @param userMessage
     */
    public Flux<String> generateAndSaveCodeStream(CodeGenTypeEnum codeGenTypeEnum, String userMessage) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCodeStream(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage);
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型" + codeGenTypeEnum.getValue());
        };
    }

    /**
     * 生成 HTML 代码并保存
     *
     * @param userMessage
     * @return
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        // 生成 HTML 代码
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        // 保存 HTML 代码
        return CoreFileSaver.saveHtmlFile(htmlCodeResult);
    }

    /**
     * 获取多文件代码并保存
     *
     * @param userMessage
     * @return
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        // 获取多文件代码生成结果
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        // 保存多文件代码
        return CoreFileSaver.saveMultiFile(multiFileCodeResult);
    }

    /**
     * 获取 HTML 代码并保存（流式）
     *
     * @param userMessage
     * @return
     */
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        Flux<String> stringFlux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return stringFlux.doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(codeBuilder.toString());
                        File file = CoreFileSaver.saveHtmlFile(htmlCodeResult);
                        log.info("保存 HTML 代码成功，路径为：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存 HTML 代码失败：{}", e.getMessage());
                    }
                });
    }

    /**
     * 获取多文件代码并保存（流式）
     *
     * @param userMessage
     * @return
     */
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        Flux<String> stringFlux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return stringFlux.doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(codeBuilder.toString());
                        File file = CoreFileSaver.saveMultiFile(multiFileCodeResult);
                        log.info("保存多文件代码成功，路径为：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存多文件代码失败：{}", e.getMessage());
                    }
                });
    }
}
