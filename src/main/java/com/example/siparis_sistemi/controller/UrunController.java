package com.example.siparis_sistemi.controller;

import com.example.siparis_sistemi.entity.Degerlendirme;
import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.Urun;
import com.example.siparis_sistemi.repository.UrunRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;

import com.example.siparis_sistemi.service.OneriService;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import com.example.siparis_sistemi.repository.FavoriRepository;
import com.example.siparis_sistemi.repository.DegerlendirmeRepository;

import org.springframework.transaction.annotation.Transactional;

@Controller
public class UrunController {

    private final UrunRepository urunRepository;
    private final OneriService oneriService;
    private final KullaniciRepository kullaniciRepository;
    private final FavoriRepository favoriRepository;
    private final DegerlendirmeRepository degerlendirmeRepository;

    public UrunController(UrunRepository urunRepository, OneriService oneriService, KullaniciRepository kullaniciRepository, 
        FavoriRepository favoriRepository, DegerlendirmeRepository degerlendirmeRepository) {
        this.urunRepository = urunRepository;
        this.oneriService=oneriService;
        this.kullaniciRepository=kullaniciRepository;
        this.favoriRepository=favoriRepository;
        this.degerlendirmeRepository=degerlendirmeRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/urunler")
    public String urunleriListele(
            @RequestParam(required = false) String kategori,
            @RequestParam(required = false) String arama,
            @RequestParam(required = false) String siralama,
            Model model, Principal principal) {

        List<Urun> urunListesi;

        if (kategori != null && !kategori.isEmpty()) {
            urunListesi = urunRepository.findByKategoriAndAktifMiTrue(kategori);
            List<Urun> enPopulerUrunler = urunRepository.findEnCokFavorilenenUrunlerByKategori(kategori);
            model.addAttribute("enPopulerUrunler", enPopulerUrunler);
            model.addAttribute("kategoriSecildiMi", true);
        } else {
            urunListesi = urunRepository.findByAktifMiTrue();
            model.addAttribute("kategoriSecildiMi", false);
        }

        if (arama != null && !arama.trim().isEmpty()) {
            String arananKelime = arama.toLowerCase().trim();
            urunListesi = urunListesi.stream()
                    .filter(u -> u.getUrunAdi().toLowerCase().contains(arananKelime))
                    .collect(Collectors.toList());
            model.addAttribute("arananKelime", arama);
        }

        if ("artan".equals(siralama)) {
            urunListesi.sort(java.util.Comparator.comparingDouble(Urun::getFiyat));
        } else if ("azalan".equals(siralama)) {
            urunListesi.sort(java.util.Comparator.comparingDouble(Urun::getFiyat).reversed());
        }

        // Verileri ekrana gönder
        model.addAttribute("urunler", urunListesi);
        model.addAttribute("seciliKategori", kategori);
        model.addAttribute("seciliSiralama", siralama);

        // Öneriler ve Favoriler
        if (principal != null) {
            Kullanici aktifKullanici = kullaniciRepository.findByKullaniciAdi(principal.getName()).orElseThrow();
            if (kategori == null || kategori.isEmpty()) {
                List<Urun> akilliOneriler = oneriService.kullaniciyaOzelOneriler(aktifKullanici);
                model.addAttribute("oneriler", akilliOneriler);
            }
            List<Long> favoriUrunIds = favoriRepository.findByKullanici(aktifKullanici)
                    .stream().map(f -> f.getUrun().getId()).collect(Collectors.toList());
            model.addAttribute("favoriUrunIds", favoriUrunIds);
        }

        return "urunler";
    }
    
    @GetMapping("/urun/ekle")
    @Transactional(readOnly = true)
    public String urunEkleSayfasiniGoster() {
        return "urun_ekle"; // templates klasöründeki urun-ekle.html dosyasını açar
    }

    @PostMapping("/urun/ekle")
    @Transactional
    public String yeniUrunKaydet(@RequestParam String urunAdi, @RequestParam Double fiyat,
                                @RequestParam int stokAdedi, @RequestParam String kategori ) 
    {
        Urun yeniUrun = new Urun();
        yeniUrun.setUrunAdi(urunAdi);
        yeniUrun.setFiyat(fiyat);
        yeniUrun.setStokAdedi(stokAdedi);
        yeniUrun.setKategori(kategori);
        yeniUrun.setAktifMi(true);
        
        urunRepository.save(yeniUrun); 
        
        return "redirect:/urunler"; 
    }

    @PostMapping("/urun/sil/{id}")
    @Transactional
    public String urunSil(@PathVariable Long id) {
        Urun urun = urunRepository.findById(id).orElseThrow();
        urun.setAktifMi(false); // Ürünü pasife çek (Soft Delete)
        urunRepository.save(urun);
        return "redirect:/urunler";
    }

    @Transactional(readOnly = true)
    @GetMapping("/urun/detay/{id}")
    public String urunDetayGoster(@PathVariable Long id, Model model) {
        Urun urun = urunRepository.findById(id).orElseThrow();
        
        List<Degerlendirme> yorumlar = degerlendirmeRepository.findByUrun(urun);
        
        Double ortalamaPuan = degerlendirmeRepository.findOrtalamaYildizByUrunId(id);
        if (ortalamaPuan == null) {
            ortalamaPuan = 0.0;
        }

        model.addAttribute("urun", urun);
        model.addAttribute("yorumlar", yorumlar);

        model.addAttribute("ortalamaPuan", String.format("%.1f", ortalamaPuan)); 
        
        return "urun_detay";
    }

    @Transactional(readOnly = true)
    @GetMapping("/yonetim/arsiv")
    public String arsivlenmisUrunleriGetir(Model model) {
        List<Urun> pasifUrunler = urunRepository.findByAktifMiFalse();
        model.addAttribute("urunler", pasifUrunler);
        return "arsiv";
    }
    
    @PostMapping("/urun/kurtar/{id}")
    @Transactional
    public String urunuKurtar(@PathVariable Long id) {
        Urun urun = urunRepository.findById(id).orElseThrow();
        urun.setAktifMi(true); 
        urunRepository.save(urun);   
        return "redirect:/yonetim/arsiv";
    }
    
    @Transactional(readOnly = true)
    @GetMapping("/urun/duzenle/{id}")
    public String urunDuzenleSayfasiniGoster(@PathVariable Long id, Model model) {
        Urun urun = urunRepository.findById(id).orElseThrow(() -> new RuntimeException("Ürün bulunamadı!"));
        model.addAttribute("urun", urun);
        return "urun_duzenle"; 
    }

    @Transactional
    @PostMapping("/urun/duzenle/{id}")
    public String urunGuncelle(@PathVariable Long id,
            @RequestParam String urunAdi,
            @RequestParam Double fiyat,
            @RequestParam int stokAdedi,
            @RequestParam String kategori) {

        Urun mevcutUrun = urunRepository.findById(id).orElseThrow();

        mevcutUrun.setUrunAdi(urunAdi);
        mevcutUrun.setFiyat(fiyat);
        mevcutUrun.setStokAdedi(stokAdedi);
        mevcutUrun.setKategori(kategori);

        urunRepository.save(mevcutUrun);

        return "redirect:/urun/detay/" + id;
    }
    
}
