package com.example.siparis_sistemi.controller;

import com.example.siparis_sistemi.entity.Favori;
import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.Urun;
import com.example.siparis_sistemi.repository.FavoriRepository;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import com.example.siparis_sistemi.repository.UrunRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class FavoriController {
    
    private final FavoriRepository favoriRepository;
    private final KullaniciRepository kullaniciRepository;
    private final UrunRepository urunRepository;

    public FavoriController(FavoriRepository favoriRepository, KullaniciRepository kullaniciRepository, UrunRepository urunRepository){
        this.favoriRepository=favoriRepository;
        this.kullaniciRepository=kullaniciRepository;
        this.urunRepository=urunRepository;
    }

    @Transactional
    @PostMapping("/favori/degistir")
    public String favoriDegistir(@RequestParam Long urunId, Principal principal) {
        if(principal==null) return "redirect:/login";

        Kullanici kullanici = kullaniciRepository.findByKullaniciAdi(principal.getName()).orElseThrow();
        Urun urun = urunRepository.findById(urunId).orElseThrow();

        Optional<Favori> mevcutFavori = favoriRepository.findByKullaniciAndUrun(kullanici, urun);

        if (mevcutFavori.isPresent()) {
            // Zaten favorilerdeyse (Kalp doluysa), tıklanınca favorilerden çıkar
            favoriRepository.delete(mevcutFavori.get());
        } else {
            // Favorilerde değilse (Kalp boşsa), favorilere ekle
            Favori yeniFavori = new Favori();
            yeniFavori.setKullanici(kullanici);
            yeniFavori.setUrun(urun);
            favoriRepository.save(yeniFavori);
        }

        // Tıklandıktan sonra geldiği sayfaya (ürünler) geri dönsün
        return "redirect:/urunler";
        
    }

    @Transactional(readOnly = true)
    @GetMapping("/favorilerim")
    public String favorilerimSayfasi(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        Kullanici kullanici = kullaniciRepository.findByKullaniciAdi(principal.getName()).orElseThrow();
        
        // Kullanıcının favorilerini bul ve içinden sadece Ürünleri çekip listele
        List<Favori> favoriler = favoriRepository.findByKullanici(kullanici);
        List<Urun> favoriUrunler = favoriler.stream().map(Favori::getUrun).collect(Collectors.toList());

        model.addAttribute("urunler", favoriUrunler);
        return "favoriler"; // favoriler.html dosyasını açacak
    }
    
}
