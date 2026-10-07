package com.example.siparis_sistemi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class I18nConfig implements WebMvcConfigurer {

    // Varsayılan dili Türkçe yapıyoruz ve seçimi Session'da (Oturumda) tutuyoruz
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("tr", "TR"));
        return slr;
    }

    // URL'deki parametreyi dinleyerek dili değiştiren yakalayıcı (Interceptor)
    // Örn: ?lang=en geldiğinde sistemi İngilizce yapacak
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); 
        return lci;
    }

    // Yakalayıcıyı sisteme dahil ediyoruz
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}