package com.example.siparis_sistemi.controller;

import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.SepetUrunu;
import com.example.siparis_sistemi.entity.Urun;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import com.example.siparis_sistemi.repository.SepetRepository;
import com.example.siparis_sistemi.repository.UrunRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional; // Hatanın kaynağı büyük ihtimalle bu import'un eksikliğiydi

import org.springframework.transaction.annotation.Transactional;

@Controller
public class SepetController {

    private final SepetRepository sepetRepository;
    private final UrunRepository urunRepository;
    private final KullaniciRepository kullaniciRepository;

    public SepetController(SepetRepository sepetRepository, UrunRepository urunRepository, KullaniciRepository kullaniciRepository) {
        this.sepetRepository = sepetRepository;
        this.urunRepository = urunRepository;
        this.kullaniciRepository = kullaniciRepository;
    }

    // Ürün Ekleme (veya miktar arttırma) Uç Noktası
    @Transactional
    @PostMapping("/sepete-ekle")
    public String sepeteEkle(@RequestParam Long urunId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Kullanici kullanici = kullaniciRepository.findByKullaniciAdi(authentication.getName()).orElseThrow();
        Urun urun = urunRepository.findById(urunId).orElseThrow();

        Optional<SepetUrunu> mevcutSepetUrunu = sepetRepository.findByKullaniciAndUrun(kullanici, urun);

        if (mevcutSepetUrunu.isPresent()) {
            SepetUrunu su = mevcutSepetUrunu.get();
            su.setMiktar(su.getMiktar() + 1);
            sepetRepository.save(su);
        } else {
            SepetUrunu yeniSepetUrunu = new SepetUrunu();
            yeniSepetUrunu.setKullanici(kullanici);
            yeniSepetUrunu.setUrun(urun);
            yeniSepetUrunu.setMiktar(1);
            sepetRepository.save(yeniSepetUrunu);
        }
        return "redirect:/urunler";
    }

    // Artı Butonu Uç Noktası (Güvenli)
    @Transactional
    @PostMapping("/sepet/arttir")
    public String miktariArttir(@RequestParam Long sepetItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String aktifKullaniciAdi = authentication.getName(); // Sisteme giriş yapan kişi

        SepetUrunu su = sepetRepository.findById(sepetItemId).orElseThrow();

        // GÜVENLİK KONTROLÜ: Sepet ürünü bu kullanıcıya mı ait?
        if (!su.getKullanici().getKullaniciAdi().equals(aktifKullaniciAdi)) {
            throw new RuntimeException("Yetkisiz işlem! Başkasının sepetine müdahale edemezsiniz.");
        }

        su.setMiktar(su.getMiktar() + 1);
        sepetRepository.save(su);
        return "redirect:/sepet";
    }

    // Eksi Butonu Uç Noktası (Güvenli)
    @Transactional
    @PostMapping("/sepet/azalt")
    public String miktariAzalt(@RequestParam Long sepetItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String aktifKullaniciAdi = authentication.getName();

        SepetUrunu su = sepetRepository.findById(sepetItemId).orElseThrow();

        // GÜVENLİK KONTROLÜ
        if (!su.getKullanici().getKullaniciAdi().equals(aktifKullaniciAdi)) {
            throw new RuntimeException("Yetkisiz işlem! Başkasının sepetine müdahale edemezsiniz.");
        }

        if (su.getMiktar() > 1) {
            su.setMiktar(su.getMiktar() - 1);
            sepetRepository.save(su);
        } else {
            sepetRepository.delete(su);
        }
        return "redirect:/sepet";
    }

    // Sil (Çöp Kutusu) Butonu Uç Noktası (Güvenli)
    @Transactional
    @PostMapping("/sepet/sil")
    public String sepettenSil(@RequestParam Long sepetItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String aktifKullaniciAdi = authentication.getName();

        SepetUrunu su = sepetRepository.findById(sepetItemId).orElseThrow();

        // GÜVENLİK KONTROLÜ
        if (!su.getKullanici().getKullaniciAdi().equals(aktifKullaniciAdi)) {
            throw new RuntimeException("Yetkisiz işlem! Başkasının sepetine müdahale edemezsiniz.");
        }

        sepetRepository.deleteById(sepetItemId);
        return "redirect:/sepet";
    }

    // Sepet Sayfasını Görüntüleme Uç Noktası
    @Transactional(readOnly = true)
    @GetMapping("/sepet")
    public String sepetimiGoster(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Kullanici kullanici = kullaniciRepository.findByKullaniciAdi(authentication.getName()).orElseThrow();

        List<SepetUrunu> sepetUrunleri = sepetRepository.findByKullanici(kullanici);

        double toplamTutar = 0;
        for (SepetUrunu sepetItem : sepetUrunleri) {
            toplamTutar += (sepetItem.getUrun().getFiyat() * sepetItem.getMiktar());
        }

        model.addAttribute("sepetUrunleri", sepetUrunleri);
        model.addAttribute("toplamTutar", toplamTutar);

        return "sepet";
    }
}