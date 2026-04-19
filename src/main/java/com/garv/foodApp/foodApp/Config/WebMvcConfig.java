package com.garv.foodApp.foodApp.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Redirect root to admin panel
        registry.addViewController("/").setViewName("forward:/admin/index.html");
        registry.addViewController("/admin").setViewName("forward:/admin/index.html");
    }
}
