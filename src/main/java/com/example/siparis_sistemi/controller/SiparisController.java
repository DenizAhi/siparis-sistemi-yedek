package com.example.siparis_sistemi.controller;

import com.example.siparis_sistemi.entity.*;
import com.example.siparis_sistemi.repository.*;

import com.example.siparis_sistemi.service.KullaniciService; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import java.security.Principal; 

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.transaction.annotation.Transactional;

@Controller
public class SiparisController {
    private final SiparisRepository siparisRepository;
    private final SiparisUrunuRepository siparisUrunuRepository;
    private final SepetRepository sepetRepository;
    private final KullaniciRepository kullaniciRepository;

    private final KullaniciService kullaniciService;

    public SiparisController(SiparisRepository siparisRepository, SiparisUrunuRepository siparisUrunuRepository,
    SepetRepository sepetRepository, KullaniciRepository kullaniciRepository, UrunRepository urunRepository, KullaniciService kullaniciService)
    {
        this.siparisRepository=siparisRepository;
        this.siparisUrunuRepository=siparisUrunuRepository;
        this.sepetRepository=sepetRepository;
        this.kullaniciRepository=kullaniciRepository;
        this.kullaniciService=kullaniciService;
    }

    @Transactional
    @PostMapping("/siparis/tamamla")
    public String siparisiTamamla() {
        // 1. Sisteme giriş yapan kullanıcıyı bul
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Kullanici kullanici = kullaniciRepository.findByKullaniciAdi(authentication.getName()).orElseThrow();

        // 2. Kullanıcının sepetindeki ürünleri getir
        List<SepetUrunu> sepetUrunleri = sepetRepository.findByKullanici(kullanici);

        // Eğer sepet boşsa işlem yapma, sepete geri dön
        if (sepetUrunleri.isEmpty()) {
            return "redirect:/sepet";
        }

        // 3. Toplam tutarı hesapla
        double toplamTutar = 0;
        for (SepetUrunu su : sepetUrunleri) {
            toplamTutar += (su.getUrun().getFiyat() * su.getMiktar());
        }

        // 4. Yeni bir Sipariş (Başlık) oluştur ve veritabanına kaydet
        Siparis yeniSiparis = new Siparis();
        yeniSiparis.setKullanici(kullanici);
        yeniSiparis.setSiparisTarihi(LocalDateTime.now());
        yeniSiparis.setToplamTutar(toplamTutar);
        yeniSiparis.setDurum("ÖDEME BEKLİYOR (HAVALE)"); // Banka olmadığı için durumu böyle belirledik
        siparisRepository.save(yeniSiparis);

        // 5. Sepetteki her ürünü SiparisUrunu (Detay) tablosuna aktar ve STOK DÜŞ
        for (SepetUrunu su : sepetUrunleri) {
            Urun urun = su.getUrun(); // Sepetteki ürünü al

            // STOK KONTROLÜ: Eğer istenen miktar stoktan fazlaysa sistemi durdur
            if (urun.getStokAdedi() < su.getMiktar()) {
                throw new RuntimeException("HATA: " + urun.getUrunAdi() + " için yeterli stok yok! Mevcut Stok: " + urun.getStokAdedi());
            }

            // Sipariş detayını oluştur
            SiparisUrunu siparisUrunu = new SiparisUrunu();
            siparisUrunu.setSiparis(yeniSiparis);
            siparisUrunu.setUrun(urun);
            siparisUrunu.setMiktar(su.getMiktar());
            siparisUrunu.setBirimFiyat(urun.getFiyat()); 
            
            siparisUrunuRepository.save(siparisUrunu);
        }

        // 6. Sipariş başarıyla oluştuğuna göre sepeti tamamen boşalt
        sepetRepository.deleteAll(sepetUrunleri);

        // 7. Başarılı sayfasına yönlendir
        return "redirect:/siparis_basarili";
    }

    // Başarılı sayfasını gösterecek metot (endpoint)
    @Transactional(readOnly = true)
    @GetMapping("/siparis_basarili")
    public String siparisBasariliSayfasi() {
        return "siparis_basarili"; 
    }

    @Transactional(readOnly = true)
    @GetMapping("/profil")
    public String profilSayfasiniGoster(Model model) {
        // 1. Sisteme giriş yapan kullanıcıyı bul
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Kullanici kullanici = kullaniciRepository.findByKullaniciAdi(authentication.getName()).orElseThrow();

        List<Siparis> gecmisSiparisler = siparisRepository.findByKullaniciOrderBySiparisTarihiDesc(kullanici);

        // 3. Verileri HTML sayfasına gönder
        model.addAttribute("kullanici", kullanici);
        model.addAttribute("siparisler", gecmisSiparisler);

        return "profil"; // profil.html dosyasını açacak
    }

    //şifre değiştirme işlemi
    @Transactional(readOnly = true)
    @GetMapping("/profil/sifre-degistir-sayfasi")
    public String sifreDegistirSayfasiGoster() {
        return "sifre_degistir"; // Yeni açacağımız HTML sayfasının adı
    }
    
    @Transactional
    @PostMapping("/profil/sifre-degistir")
    public String sifreDegistir(@RequestParam String eskiSifre,
                                @RequestParam String yeniSifre,
                                Principal principal, 
                                RedirectAttributes redirectAttributes) {
        try {
            String kullaniciAdi = principal.getName();
            kullaniciService.sifreDegistir(kullaniciAdi, eskiSifre, yeniSifre);
            redirectAttributes.addFlashAttribute("basari", "Şifreniz güvenlik standartlarına uygun olarak başarıyla güncellendi!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("hata", e.getMessage());
        }
        
        return "redirect:/profil/sifre-degistir-sayfasi"; 
    }

    // 1. Yönetim Sayfasını Göster
    @Transactional(readOnly = true)
    @GetMapping("/yonetim/siparisler")
    public String butunSiparisleriGoster(Model model) {
        // Sistemdeki tüm siparişleri Repository'den çek
        List<Siparis> tumSiparisler = siparisRepository.findAllByOrderBySiparisTarihiDesc();
        model.addAttribute("siparisler", tumSiparisler);
        
        return "siparis_yonetim"; // HTML dosyasının adı
    }

    // 2. Sipariş Durumunu Güncelle ("Kargoya Verildi" vb.)
    @Transactional
    @PostMapping("/yonetim/siparis/guncelle")
    public String siparisDurumGuncelle(@RequestParam Long siparisId, @RequestParam String yeniDurum) {
        // İlgili siparişi bul
        Siparis siparis = siparisRepository.findById(siparisId).orElseThrow();
        
        // Durumunu formdan gelen yeni durumla değiştir ve kaydet
        siparis.setDurum(yeniDurum);
        siparisRepository.save(siparis);
        
        // İşlem bitince yönetim sayfasına geri dön
        return "redirect:/yonetim/siparisler";
    }

    @Transactional(readOnly = true)
    @GetMapping("/siparis/detay/{id}")
    public String siparisDetayGoster(@PathVariable Long id, Model model) {
        // İlgili siparişi bul
        Siparis siparis = siparisRepository.findById(id).orElseThrow();
        
        // Aktif kullanıcıyı bul
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Kullanici aktifKullanici = kullaniciRepository.findByKullaniciAdi(authentication.getName()).orElseThrow();
        
        // GÜVENLİK KONTROLÜ: Kullanıcı admin/satıcı değilse VE sipariş ona ait değilse engelle!
        boolean isAdminVeyaSatici = authentication.getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SATICI"));
              
        if (!isAdminVeyaSatici && !siparis.getKullanici().getId().equals(aktifKullanici.getId())) {
            throw new RuntimeException("Yetkisiz erişim! Başkasının sipariş detayını göremezsiniz.");
        }

        // Siparişe ait olan ürün kalemlerini getir
        List<SiparisUrunu> detaylar = siparisUrunuRepository.findBySiparis(siparis);
        
        model.addAttribute("siparis", siparis);
        model.addAttribute("detaylar", detaylar);
        
        return "siparis_detay";
    }

    @Transactional
    @PostMapping("/siparis/iptal/{id}")
    public String siparisIptalEt(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";

        Siparis siparis = siparisRepository.findById(id).orElseThrow();

        // Güvenlik: Kullanıcı başkasının siparişini iptal edemesin
        if (!siparis.getKullanici().getKullaniciAdi().equals(principal.getName())) {
            return "redirect:/profil"; 
        }

        if (siparis.getDurum().equals("BEKLİYOR") || siparis.getDurum().equals("HAZIRLANIYOR")) {
            
            siparis.setDurum("İPTAL EDİLDİ");

            // 2. STOK İADESİ: Bu siparişteki tüm ürünleri dön ve stokları depoya geri ekle
            // DİKKAT: STOK İADESİNİ TRIGGERA BIRAKTIĞIMIZ İÇİN BURALARI SİLİYORUZ
            // for (SiparisUrunu siparisUrunu : siparis.getSiparisUrunleri()) {
                
            //     Urun urun = siparisUrunu.getUrun();
                
            //     // Senin sınıfındaki getMiktar() metodunu kullanıyoruz
            //     int iadeEdilecekMiktar = siparisUrunu.getMiktar();
                
            //     urun.setStokAdedi(urun.getStokAdedi() + iadeEdilecekMiktar); 
                
            //     urunRepository.save(urun);
            // }

            siparisRepository.save(siparis);
        }

        return "redirect:/profil"; 
    }

}
