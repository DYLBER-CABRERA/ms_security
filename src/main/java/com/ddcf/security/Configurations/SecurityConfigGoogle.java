package com.ddcf.security.Configurations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigGoogle {

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfigGoogle(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }
    //Definimos un bean de tipo SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //configuramos nuetros autorazehttprequest y lo hacemos con una funcion lambda
                .authorizeHttpRequests(request -> {
            /*cuando se haga una peticion a los empoints publicos la raiz: "/" o a "/hello"
            le permitimos que accede alguna de las 2 pero PERO si nos hacen
             una peticion http metodo GET
            */
                    request.requestMatchers(HttpMethod.GET,"/","/hello").permitAll();
                    //Cuaquier request tiene que estar autenticado
                    request.anyRequest().authenticated();

                })
                //Asi funciona el formulario por defecto
                .formLogin(Customizer.withDefaults())
                //colocamos unas propiedades para podernos logiar con oauth2Login con google o github
                //.oauth2Login(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2 //aqui configura el inicio de session con oauth2
                        .userInfoEndpoint(userInfo -> userInfo//especifica el servicio de usuarioo persinalizado
                                .userService(customOAuth2UserService)//especifica para obtener los detalles del usuario del provedor
                        )
                )
                .build();//contruye la configuracion de seguridad
    }
}