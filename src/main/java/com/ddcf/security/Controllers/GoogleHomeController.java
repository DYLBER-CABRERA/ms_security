package com.ddcf.security.Controllers;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin

@RestController
public class GoogleHomeController {

    @GetMapping
    public String init() {
        return "hola Bienvenido a la aplicacion de transporte de Carga ingresando por google o github";    }
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/";
    }
    @GetMapping("/hello")
    public String hello() {
        return "Welcom to Google User";
    }
    @GetMapping("/token")
    public Map<String, Object> getToken(@AuthenticationPrincipal OAuth2User principal) {
        return principal.getAttributes();
    }

}
