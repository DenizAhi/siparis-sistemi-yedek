package com.example.siparis_sistemi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Transactional(readOnly = true)
    @GetMapping("/login")
    public String loginSayfasiniGoster() {
        // src/main/resources/templates/login.html dosyasını arar
        return "login";
    }

}
