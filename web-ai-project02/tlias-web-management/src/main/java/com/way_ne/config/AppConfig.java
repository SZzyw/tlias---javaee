package com.way_ne.config;

import com.way_ne.security.PermissionInterceptor;
import com.way_ne.security.RequestGuardInterceptor;
import com.way_ne.utils.LocalHeadStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
    private final LocalHeadStorage localHeadStorage;
    private final PermissionInterceptor permissionInterceptor;
    private final RequestGuardInterceptor requestGuardInterceptor;

    public AppConfig(LocalHeadStorage localHeadStorage,
                     PermissionInterceptor permissionInterceptor,
                     RequestGuardInterceptor requestGuardInterceptor) {
        this.localHeadStorage = localHeadStorage;
        this.permissionInterceptor = permissionInterceptor;
        this.requestGuardInterceptor = requestGuardInterceptor;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = localHeadStorage.getRootDir().toUri().toString();
        if (!resourceLocation.endsWith("/")) {
            resourceLocation = resourceLocation + "/";
        }
        registry.addResourceHandler("/head/**")
                .addResourceLocations(resourceLocation);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestGuardInterceptor).addPathPatterns("/**");
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
    }
}
