package com.yukina.suaicode.core;

import cn.hutool.json.JSONUtil;
import com.yukina.suaicode.ai.AiCodeGeneratorService;
import com.yukina.suaicode.ai.AiCodeGeneratorServiceFactory;
import com.yukina.suaicode.ai.model.HtmlCodeResult;
import com.yukina.suaicode.ai.model.MultiFileCodeResult;
import com.yukina.suaicode.ai.model.message.AiResponseMessage;
import com.yukina.suaicode.ai.model.message.ToolExecutedMessage;
import com.yukina.suaicode.ai.model.message.ToolRequestMessage;
import com.yukina.suaicode.constant.AppConstant;
import com.yukina.suaicode.core.builder.VueProjectBuilder;
import com.yukina.suaicode.core.parser.CodeParserExecutor;
import com.yukina.suaicode.core.saver.CodeFileSaverExecutor;
import com.yukina.suaicode.exception.BusinessException;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
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
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    @Resource
    private VueProjectBuilder vueProjectBuilder;


    /**
     * 生成代码并保存
     *
     * @param codeGenTypeEnum
     * @param userMessage
     */
    public File generateAndSaveCode(CodeGenTypeEnum codeGenTypeEnum, String userMessage, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(CodeGenTypeEnum.HTML, htmlCodeResult, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaver(CodeGenTypeEnum.MULTI_FILE, multiFileCodeResult, appId);
            }
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
    public Flux<String> generateAndSaveCodeStream(CodeGenTypeEnum codeGenTypeEnum, String userMessage, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(stringFlux, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(stringFlux, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            case VUE_PROJECT -> {
                TokenStream tokenStream = aiCodeGeneratorService.generateVueProjectCodeStream(appId, userMessage);
                yield processTokenStream(tokenStream, appId);
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型" + codeGenTypeEnum.getValue());
        };
    }

//    /**
//     * 生成 HTML 代码并保存
//     *
//     * @param userMessage
//     * @return
//     */
//    private File generateAndSaveHtmlCode(String userMessage) {
//        // 生成 HTML 代码
//        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
//        // 保存 HTML 代码
//        return CoreFileSaver.saveHtmlFile(htmlCodeResult);
//    }

//    /**
//     * 获取多文件代码并保存
//     *
//     * @param userMessage
//     * @return
//     */
//    private File generateAndSaveMultiFileCode(String userMessage) {
//        // 获取多文件代码生成结果
//        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
//        // 保存多文件代码
//        return CoreFileSaver.saveMultiFile(multiFileCodeResult);
//    }

//    /**
//     * 获取 HTML 代码并保存（流式）
//     *
//     * @param userMessage
//     * @return
//     */
//    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage, Long appId) {
//        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
//        Flux<String> stringFlux = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
//        return processCodeStream(stringFlux, CodeGenTypeEnum.HTML, appId);
//    }
//
//    /**
//     * 获取多文件代码并保存（流式）
//     *
//     * @param userMessage
//     * @return
//     */
//    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage, Long appId) {
//        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
//        Flux<String> stringFlux = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
//        return processCodeStream(stringFlux, CodeGenTypeEnum.MULTI_FILE, appId);
//    }

    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    try {
                        Object CodeResult = CodeParserExecutor.executeParser(codeBuilder.toString(), codeGenType);
                        File file = CodeFileSaverExecutor.executeSaver(codeGenType, CodeResult, appId);
                        log.info("保存成功，路径为：{}", file.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败：{}", e.getMessage());
                    }
                });
    }

    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream, Long appId) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    .onCompleteResponse((ChatResponse response) -> {
                        // 同步构建vue项目
                        String projectPath = AppConstant.CODE_OUTPUT_ROOT_DIR + "/vue_project_" + appId;
                        vueProjectBuilder.buildProject(projectPath);
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    .start();
        });
    }

}
