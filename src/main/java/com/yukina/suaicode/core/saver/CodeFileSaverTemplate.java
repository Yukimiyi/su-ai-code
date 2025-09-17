package com.yukina.suaicode.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yukina.suaicode.constant.AppConstant;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.exception.ThrowUtils;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;

import java.io.File;

public abstract class CodeFileSaverTemplate<T> {

    public static final String FILE_SAVE_ROOT_PATH = AppConstant.CODE_OUTPUT_ROOT_DIR;

    public final File saveCode(T result, Long appId) {
        // 1.验证输入
        validInput(result);
        // 2.构建目录
        String uniqueDir = buildUniqueDir(appId);
        // 3.保存文件（子类实现）
        saveFiles(result, uniqueDir);
        // 4。返回文件对象
        return new File(uniqueDir);
    }

    protected void validInput(T result) {
        ThrowUtils.throwIf(result == null, ErrorCode.SYSTEM_ERROR, "代码对象不能为空");
    }

    protected String buildUniqueDir(Long appId) {
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_PATH + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    protected static void writeToPath(String dirPath, String fileName, String content) {
        FileUtil.writeUtf8String(content, dirPath + File.separator + fileName);
    }

    protected abstract CodeGenTypeEnum getCodeType();

    protected abstract void saveFiles(T result, String baseDirPath);
}
