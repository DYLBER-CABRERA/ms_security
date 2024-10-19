package com.ddcf.security.Configurations;

import com.ddcf.security.Interceptors.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//esta clase es el muro
@Configuration
public class WebConfig implements WebMvcConfigurer {
    //implementamos la interfaz WebMvcConfigurer
    @Autowired
    private SecurityInterceptor securityInterceptor;

    //registro todos los interceptores que yo quiero colacar @Override es una sobreescritura
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //regestry es el proyecto
        //security ntercepton inyecta y se va al paquete de los interceptores
        registry.addInterceptor(securityInterceptor)
                //va a mirar el muro todo lo que tenga esa ruta lo tenga protegido el interceptor
                //excluyo nas y otras quedo abiertas
                .addPathPatterns("/api/**")//las tiene en cuenta
                .excludePathPatterns("/api/public/**"); //las excluye


    }
}