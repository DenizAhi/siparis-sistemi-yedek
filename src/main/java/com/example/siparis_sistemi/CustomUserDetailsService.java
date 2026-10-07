package com.example.siparis_sistemi;

import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.siparis_sistemi.service.LoginAttemptService; 
import org.springframework.security.authentication.LockedException;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private final KullaniciRepository kullaniciRepository;
    private final LoginAttemptService loginAttemptService;

    // Veritabanı depomuzu buraya bağlıyoruz
    public CustomUserDetailsService(KullaniciRepository kullaniciRepository, LoginAttemptService loginAttemptService) {
        this.kullaniciRepository = kullaniciRepository;
        this.loginAttemptService=loginAttemptService;
    }

    // Spring Security, biri giriş yapmaya çalıştığında bu metodu tetikler
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Brute force kontrolü
        if (loginAttemptService.isBlocked(username)) {
            throw new LockedException("Çok fazla hatalı giriş yaptınız. Lütfen 15 dakika bekleyin.");
        }
        
        // Veritabanında bu kullanıcı adını arıyoruz
        Kullanici kullanici = kullaniciRepository.findByKullaniciAdi(username)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + username));

        // 2. Bulduğumuz kullanıcıyı Spring Security'nin anlayacağı formata (UserDetails) çeviriyoruz
        return User.builder()
                .username(kullanici.getKullaniciAdi())
                .password(kullanici.getSifre()) // Şifreli parolayı veriyoruz, Spring kendi karşılaştıracak
                .roles(kullanici.getRol())
                .build();
    }
    
}
