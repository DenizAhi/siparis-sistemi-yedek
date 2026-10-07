package com.example.siparis_sistemi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.core.userdetails.User;  //
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;  //
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager; //
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable()) // Test kolaylığı için
        .headers(headers -> headers
            // 1. Clickjacking (Tıklama Hırsızlığı) Koruması: Sitemizin başka bir sitede IFrame (çerçeve) içinde açılmasını kesinlikle yasaklar.
            .frameOptions(frame -> frame.deny())
            
            // 2. XSS (Cross-Site Scripting) Koruması: Tarayıcının dahili XSS filtresini zorunlu olarak aktif eder ve saldırı sezerse sayfayı bloklar.
            .xssProtection(xss -> xss.headerValue(org.springframework.security.web.header.writers.XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            
            // 3. İçerik Türü Koklama (MIME Sniffing) Koruması: Zararlı bir dosyanın kendini resim gibi gösterip çalışmasını engeller.
            .contentTypeOptions(contentType -> contentType.disable() )
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/kayit", "/css/**", "/js/**").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/urun/ekle").hasAnyRole("SATICI", "ADMIN")
            .requestMatchers("/sepet/**", "/anasayfa", "/urunler", "/profil", "/siparis/detay/**", "/yonetim/siparisler").authenticated()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/urunler", true) // Başarılı girişte gittiğin sayfa
            .permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        )
        .sessionManagement(session -> session
                .maximumSessions(1) // Aynı anda sadece 1 girişe izin ver
                .maxSessionsPreventsLogin(false) 
                .expiredUrl("/login?expired=true") 
        );

        return http.build();
    }

    @Bean  //şifreyi şifrelemek için gerekli
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
