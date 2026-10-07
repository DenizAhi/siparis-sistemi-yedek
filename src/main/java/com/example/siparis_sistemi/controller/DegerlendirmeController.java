package com.example.siparis_sistemi.controller;

import com.example.siparis_sistemi.entity.Degerlendirme;
import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.Urun;
import com.example.siparis_sistemi.repository.DegerlendirmeRepository;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import com.example.siparis_sistemi.repository.UrunRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class DegerlendirmeController {

    private final DegerlendirmeRepository degerlendirmeRepository;
    private final KullaniciRepository kullaniciRepository;
    private final UrunRepository urunRepository;

    public DegerlendirmeController(DegerlendirmeRepository degerlendirmeRepository, KullaniciRepository kullaniciRepository, UrunRepository urunRepository){
        this.degerlendirmeRepository=degerlendirmeRepository;
        this.kullaniciRepository=kullaniciRepository;
        this.urunRepository=urunRepository;
    }

    @Transactional
    @PostMapping("/degerlendirme/ekle")
    public String yorumYap(@RequestParam Long urunId, 
                           @RequestParam int yildiz, 
                           @RequestParam String yorumMetni, 
                           Principal principal) {
        
        // Giriş yapmamışsa login sayfasına at
        if (principal == null) return "redirect:/login";

        Kullanici aktifKullanici = kullaniciRepository.findByKullaniciAdi(principal.getName()).orElseThrow();
        Urun urun = urunRepository.findById(urunId).orElseThrow();

        // Yeni değerlendirmeyi oluştur ve kaydet
        Degerlendirme yeniDegerlendirme = new Degerlendirme();
        yeniDegerlendirme.setKullanici(aktifKullanici);
        yeniDegerlendirme.setUrun(urun);
        yeniDegerlendirme.setYildiz(yildiz);
        yeniDegerlendirme.setYorumMetni(yorumMetni);

        degerlendirmeRepository.save(yeniDegerlendirme);

        return "redirect:/urunler"; // Yorum yapıldıktan sonra ürünler sayfasına dön
    }
}
