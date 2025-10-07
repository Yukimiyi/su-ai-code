package com.yukina.suaicode.core.saver;

import com.yukina.suaicode.ai.model.MultiFileCodeResult;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.exception.ThrowUtils;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;

public class MultiFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        writeToPath(baseDirPath, "index.html", result.getHtmlCode());
        writeToPath(baseDirPath, "index.css", result.getCssCode());
        writeToPath(baseDirPath, "index.js", result.getJsCode());
    }

    @Override
    protected void validInput(MultiFileCodeResult result) {
        super.validInput(result);
        ThrowUtils.throwIf(result == null, ErrorCode.SYSTEM_ERROR, "多文件保存代码内容不能为空");
    }
}
