package com.sinby.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ：sinby
 * @Date :2022/04/27 10:35
 * @Version 1.0
 */

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  //配置可以被跨域的路径
                .allowedOrigins("*")    //表示允许任何域名使用
                .allowCredentials(true) //表示是否将请求的响应暴露给页面
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","HEAD")   //允许请求类型
                .maxAge(3600);  //配置客户端缓存预检请求的响应时间
    }
}
