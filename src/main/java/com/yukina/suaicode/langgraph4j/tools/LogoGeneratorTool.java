package com.yukina.suaicode.langgraph4j.tools;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.yukina.suaicode.langgraph4j.state.ImageCategoryEnum;
import com.yukina.suaicode.langgraph4j.state.ImageResource;
import com.yukina.suaicode.manager.CosManager;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LogoGeneratorTool {

    @Resource
    private CosManager cosManager;

    @Value("${dashscope.api-key:}")
    private String dashScopeApiKey;

    @Value("${dashscope.image-model:wan2.2-t2i-flash}")
    private String imageModel;

    @Tool("根据描述生成 Logo 设计图片，用于网站品牌标识")
    public List<ImageResource> generateLogos(@P("Logo 设计描述，如名称、行业、风格等，尽量详细") String description) {
        List<ImageResource> logoList = new ArrayList<>();
        try {
            // 构建 Logo 设计提示词
            String logoPrompt = String.format("生成 Logo，Logo 中禁止包含任何文字！Logo 介绍：%s", description);
            ImageSynthesisParam param = ImageSynthesisParam.builder()
                    .apiKey(dashScopeApiKey)
                    .model(imageModel)
                    .prompt(logoPrompt)
                    .size("512*512")
                    .n(1) // 生成 1 张足够，因为 AI 不知道哪张最好
                    .build();
            ImageSynthesis imageSynthesis = new ImageSynthesis();
            ImageSynthesisResult result = imageSynthesis.call(param);
            if (result != null && result.getOutput() != null && result.getOutput().getResults() != null) {
                List<Map<String, String>> results = result.getOutput().getResults();
                for (Map<String, String> imageResult : results) {
                    String imageUrl = imageResult.get("url");
                    if (StrUtil.isNotBlank(imageUrl)) {
                        // 创建临时文件
                        Path tempFile = Files.createTempFile("logo-", ".png");
                        try {
                            // 从网络下载图片到临时文件
                            try (InputStream in = java.net.URI.create(imageUrl).toURL().openStream()) {
                                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                            }

                            // 上传到COS
                            String uploadUrl = cosManager.uploadFile(String.format("/logo/%s/%s",
                                            RandomUtil.randomString(5), tempFile.getFileName().toString()),
                                    tempFile.toFile());

                            logoList.add(ImageResource.builder()
                                    .category(ImageCategoryEnum.LOGO)
                                    .description(description)
                                    .url(uploadUrl)
                                    .build());
                        } finally {
                            // 删除临时文件
                            Files.deleteIfExists(tempFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("生成 Logo 失败: {}", e.getMessage(), e);
        }
        return logoList;
    }
}
