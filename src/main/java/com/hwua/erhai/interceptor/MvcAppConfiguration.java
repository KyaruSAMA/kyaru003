package com.hwua.erhai.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcAppConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/error",
                        "/", "/**/index.jsp",
                        "/**/login.jsp", "/doLogin",
                        "/static/**","/**/register.jsp",
                        "/doRegister","/**/doRegister.jsp","/**/register"
                );
    }
}
