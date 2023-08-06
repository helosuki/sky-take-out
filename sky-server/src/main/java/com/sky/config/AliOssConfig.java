package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建阿里云文件上传工具类对象
 */
@Configuration
@Slf4j
public class AliOssConfig {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties ap){
        log.info("开始创建阿里云文件上传工具类对象：{}",ap);
        return new AliOssUtil(ap.getEndpoint(),ap.getAccessKeyId(),ap.getAccessKeySecret(),ap.getBucketName());
    }
}
