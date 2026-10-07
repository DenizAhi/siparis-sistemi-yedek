package com.example.siparis_sistemi.controller;

import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KayitController {
    private final KullaniciRepository kullaniciRepository;
    private final PasswordEncoder passwordEncoder;

    public KayitController(KullaniciRepository kullaniciRepository, PasswordEncoder passwordEncoder) {
        this.kullaniciRepository = kullaniciRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Kayıt sayfasını göster
    @Transactional(readOnly = true)
    @GetMapping("/kayit")
    public String kayitSayfasiniGoster() {
        return "kayit"; // templates içindeki kayit.html'i açacak
    }

    // Formdan gelen verileri al ve veritabanına kaydet
    @Transactional
    @PostMapping("/kayit")
    public String yeniKullaniciKaydet(@RequestParam String username, @RequestParam String password) {
        Kullanici yeniKullanici = new Kullanici();
        yeniKullanici.setKullaniciAdi(username);
        
        // Şifreyi şifreleyerek kaydediyoruz!
        yeniKullanici.setSifre(passwordEncoder.encode(password));
        yeniKullanici.setRol("USER"); // Yeni kayıt olan herkes standart kullanıcı olsun
        
        kullaniciRepository.save(yeniKullanici);
        
        // Başarılı kayıttan sonra giriş sayfasına yönlendir
        return "redirect:/login?kayitBasarili";
    }
}
