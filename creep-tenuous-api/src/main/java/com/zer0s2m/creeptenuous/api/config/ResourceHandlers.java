package com.zer0s2m.creeptenuous.api.config;

import com.zer0s2m.creeptenuous.common.components.UploadAvatar;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class ResourceHandlers implements WebMvcConfigurer {

    private final UploadAvatar uploadAvatar;

    @Autowired
    public ResourceHandlers(UploadAvatar uploadAvatar) {
        this.uploadAvatar = uploadAvatar;
    }

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")
                .addResourceLocations("file:" + uploadAvatar.getUploadAvatarDir());
    }

}
