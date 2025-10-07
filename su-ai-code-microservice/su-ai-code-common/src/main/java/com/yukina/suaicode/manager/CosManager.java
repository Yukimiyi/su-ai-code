package com.yukina.suaicode.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.yukina.suaicode.config.CosClientConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@ConditionalOnBean(CosClientConfig.class)
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key
     * @param file
     * @return
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传文件到对象存储并返回 URL
     * @param key
     * @param file
     * @return
     */
    public String uploadFile(String key, File file) {
        PutObjectResult putObjectResult = putObject(key, file);
        if (putObjectResult != null) {
            String url = String.format("%s%s", cosClientConfig.getHost(), key);
            log.info("文件上传 COS 成功: {}->{}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传 COS 失败，返回结果为空");
            return null;
        }
    }
}
