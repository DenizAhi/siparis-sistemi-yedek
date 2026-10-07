package com.example.siparis_sistemi.service;

import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.SifreGecmisi;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import com.example.siparis_sistemi.repository.SifreGecmisiRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KullaniciService {
    private final KullaniciRepository kullaniciRepository;
    private final SifreGecmisiRepository sifreGecmisiRepository;
    private final PasswordEncoder passwordEncoder;

    public KullaniciService(KullaniciRepository kullaniciRepository,
         SifreGecmisiRepository sifreGecmisiRepository, PasswordEncoder passwordEncoder)
    {
        this.kullaniciRepository=kullaniciRepository;
        this.sifreGecmisiRepository=sifreGecmisiRepository;
        this.passwordEncoder=passwordEncoder;
    }

    // GÜÇLÜ ŞİFRE KURALI (Regex): En az 8 karakter; 1 büyük harf, 1 küçük harf, 1 rakam ve 1 özel karakter!
    private static final String SIFRE_ZORLUK_KURALI = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!.*_?-]).{8,}$";

    @Transactional
    public void sifreDegistir(String kullaniciAdi, String eskiSifre, String yeniSifre)
    {
        Kullanici kullanici=kullaniciRepository.findByKullaniciAdi(kullaniciAdi)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));
        
        if(!passwordEncoder.matches(eskiSifre,kullanici.getSifre())){
            throw new RuntimeException("Mevcut şifenizi yanlış girdiniz.");
        }

        if(!yeniSifre.matches(SIFRE_ZORLUK_KURALI)){
            throw new RuntimeException ("Şifreniz en az 8 karakter olmalı; büyük harf, küçük harf, rakam ve özel karakter içermelidir.");
        }

        if(passwordEncoder.matches(yeniSifre,kullanici.getSifre())){
            throw new RuntimeException("Yeni şifreniz şuanki şifrenizle aynı olamaz");
        }

        List<SifreGecmisi> sonSifreler=sifreGecmisiRepository.findTop3ByKullaniciOrderByDegistirmeTarihiDesc(kullanici);
        for(SifreGecmisi gecmis : sonSifreler){
            if(passwordEncoder.matches(yeniSifre, gecmis.getSifreHash())){
                throw new RuntimeException("Güvenlik ihlali: Yeni şifreniz son 3 şifrenizden biriyle aynı olamaz!");
            }
        }
        
        String yeniSifreHash=passwordEncoder.encode(yeniSifre);
        kullanici.setSifre((yeniSifreHash));
        kullaniciRepository.save(kullanici);

        SifreGecmisi yeniGecmis=new SifreGecmisi();
        yeniGecmis.setKullanici(kullanici);
        yeniGecmis.setSifreHash(yeniSifre);
        yeniGecmis.setDegistirmeTarihi(LocalDateTime.now());
        sifreGecmisiRepository.save(yeniGecmis);
    }

}
