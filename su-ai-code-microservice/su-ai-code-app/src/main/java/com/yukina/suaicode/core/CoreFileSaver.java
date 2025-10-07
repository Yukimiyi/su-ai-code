package com.yukina.suaicode.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yukina.suaicode.ai.model.HtmlCodeResult;
import com.yukina.suaicode.ai.model.MultiFileCodeResult;
import com.yukina.suaicode.model.enums.CodeGenTypeEnum;

import java.io.File;

@Deprecated
public class CoreFileSaver {
    public static final String FILE_SAVE_ROOT_PATH = System.getProperty("user.dir") + "/tmp/code_output";

    public static File saveHtmlFile(HtmlCodeResult htmlCodeResult) {
        String dirPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        writeToPath(dirPath, "index.html", htmlCodeResult.getHtmlCode());
        return new File(dirPath);
    }

    public static File saveMultiFile(MultiFileCodeResult multiFileCodeResult) {
        String dirPath = buildUniqueDir(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeToPath(dirPath, "index.html", multiFileCodeResult.getHtmlCode());
        writeToPath(dirPath, "index.css", multiFileCodeResult.getCssCode());
        writeToPath(dirPath, "index.js", multiFileCodeResult.getJsCode());
        return new File(dirPath);
    }

    public static String buildUniqueDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = FILE_SAVE_ROOT_PATH + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    public static void writeToPath(String dirPath, String fileName, String content) {
        FileUtil.writeUtf8String(content, dirPath + File.separator + fileName);
    }
}
