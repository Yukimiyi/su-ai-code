package com.yukina.suaicode.core.saver;

import com.yukina.suaicode.ai.model.HtmlCodeResult;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.exception.ThrowUtils;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {


    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToPath(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validInput(HtmlCodeResult result) {
        super.validInput(result);
        ThrowUtils.throwIf(result == null, ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
    }
}
