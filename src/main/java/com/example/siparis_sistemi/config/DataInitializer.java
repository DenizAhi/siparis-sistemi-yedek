package com.example.siparis_sistemi.config;

import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner loadData(KullaniciRepository kullaniciRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. ADMIN Kullanıcısı Oluşturma
            if (kullaniciRepository.findByKullaniciAdi("admin").isEmpty()) {
                Kullanici admin = new Kullanici();
                admin.setKullaniciAdi("admin");
                admin.setSifre(passwordEncoder.encode("1234")); // Şifre otomatik şifrelenir (hashlenir)
                admin.setRol("ADMIN");
                kullaniciRepository.save(admin);
            }

            // 2. SATICI Kullanıcısı Oluşturma
            if (kullaniciRepository.findByKullaniciAdi("satici").isEmpty()) {
                Kullanici satici = new Kullanici();
                satici.setKullaniciAdi("satici");
                satici.setSifre(passwordEncoder.encode("1234"));
                satici.setRol("SATICI");
                kullaniciRepository.save(satici);
            }

            // 3. ALICI (USER) Kullanıcısı Oluşturma
            if (kullaniciRepository.findByKullaniciAdi("alici").isEmpty()) {
                Kullanici alici = new Kullanici();
                alici.setKullaniciAdi("alici");
                alici.setSifre(passwordEncoder.encode("1234"));
                alici.setRol("USER");
                kullaniciRepository.save(alici);
            }
            
            System.out.println(">>> Test kullanıcıları (Admin, Satıcı, Alıcı) veritabanına başarıyla yüklendi! <<<");
        };
    }

}
