package com.yukina.suaicode.core.saver;

import com.yukina.suaicode.ai.model.HtmlCodeResult;
import com.yukina.suaicode.ai.model.MultiFileCodeResult;
import com.yukina.suaicode.exception.BusinessException;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;

import java.io.File;

public class CodeFileSaverExecutor {

    public static final CodeFileSaverTemplate<HtmlCodeResult> HTML_CODE_SAVER = new HtmlCodeFileSaverTemplate();
    public static final CodeFileSaverTemplate<MultiFileCodeResult> MULTI_FILE_CODE_SAVER = new MultiFileSaverTemplate();

    public static File executeSaver(CodeGenTypeEnum codeGenType, Object result)
    {
        return switch (codeGenType) {
            case HTML -> HTML_CODE_SAVER.saveCode((HtmlCodeResult) result);
            case MULTI_FILE -> MULTI_FILE_CODE_SAVER.saveCode((MultiFileCodeResult) result);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型" + codeGenType);
        };
    }
}
