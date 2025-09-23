package com.yukina.suaicode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yukina.suaicode.exception.ErrorCode;
import com.yukina.suaicode.exception.ThrowUtils;
import com.yukina.suaicode.manager.CosManager;
import com.yukina.suaicode.service.ScreenShotService;
import com.yukina.suaicode.utils.WebScreenshotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class ScreenShotServiceImpl implements ScreenShotService {

    @Resource
    private CosManager cosManager;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        // 1.生成本地文件
        ThrowUtils.throwIf(webUrl == null, ErrorCode.PARAMS_ERROR);
        String screenshotPath = WebScreenshotUtils.saveWebPageScreenshot(webUrl);
        try {
            ThrowUtils.throwIf(screenshotPath == null, ErrorCode.SYSTEM_ERROR, "生成截图失败");
            // 2.上传到对象存储
            String cosUrl = uploadScreenShotToCos(screenshotPath);
            ThrowUtils.throwIf(cosUrl == null, ErrorCode.SYSTEM_ERROR, "上传截图失败");
            log.info("网页截图生成并上传成功：{} -> {}", webUrl, cosUrl);
            return cosUrl;
        } finally {
            // 3. 清除本地文件
            clearLocalFile(screenshotPath);
        }
    }

    /**
     * 清理本地文件
     * @param localFilePath
     */
    private void clearLocalFile(String localFilePath) {
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            File parentFile = localFile.getParentFile();
            FileUtil.del(parentFile);
            log.info("本地截图文件已清理：{}", localFilePath);
        }
    }

    private String uploadScreenShotToCos(String screenshotPath) {
        if (StrUtil.isBlank(screenshotPath)) {
            log.error("截图文件路径为空：{}", screenshotPath);
            return null;
        }
        File uploadFile = new File(screenshotPath);
        if (!uploadFile.exists()) {
            log.error("截图文件不存在：{}", screenshotPath);
            return null;
        }
        String fileName = UUID.randomUUID().toString().substring(0, 8) + "compressed.jpg";
        String cosKey = generateScreenshotPath(fileName);
        return cosManager.uploadFile(cosKey, uploadFile);
    }

    private String generateScreenshotPath(String fileName) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("screenshot/%s/%s", datePath, fileName);
    }
}

