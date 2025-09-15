package com.yukina.suaicode.core.parser;

import com.yukina.suaicode.exception.BusinessException;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;

/**
 * 代码解析执行器
 */
public class CodeParserExecutor {
    public static final HtmlCodeParser HTML_CODE_PARSER = new HtmlCodeParser();
    public static final MultiFileCodeParser MULTI_FILE_CODE_PARSER = new MultiFileCodeParser();

    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML -> HTML_CODE_PARSER.parseCode(codeContent);
            case MULTI_FILE -> MULTI_FILE_CODE_PARSER.parseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型" + codeGenType.getValue());
        };
    }
}
