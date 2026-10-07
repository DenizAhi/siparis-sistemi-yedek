package com.example.siparis_sistemi.service;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LoginEventListener {

    private final LoginAttemptService loginAttemptService;
    
    private static final Logger logger = LoggerFactory.getLogger(LoginEventListener.class);

    public LoginEventListener(LoginAttemptService loginAttemptService)
    {   this.loginAttemptService=loginAttemptService;   }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event)
    {
        String kullaniciAdi = event.getAuthentication().getName();
        loginAttemptService.loginFailed(kullaniciAdi);
        logger.warn("Güvenlik Uyarısı: Başarısız giriş denemesi - Hedef Hesap: {}", kullaniciAdi);
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event)
    {
        String kullaniciAdi = event.getAuthentication().getName();
        loginAttemptService.loginSucceeded(kullaniciAdi);
    }
}
