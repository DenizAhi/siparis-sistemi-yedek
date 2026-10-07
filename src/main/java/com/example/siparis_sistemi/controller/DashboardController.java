package com.example.siparis_sistemi.controller;

import com.example.siparis_sistemi.entity.Siparis;
import com.example.siparis_sistemi.repository.KullaniciRepository;
import com.example.siparis_sistemi.repository.SiparisRepository;
import com.example.siparis_sistemi.repository.UrunRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {
    
    private final SiparisRepository siparisRepository;
    private final UrunRepository urunRepository;
    private final KullaniciRepository kullaniciRepository;

    public DashboardController(SiparisRepository siparisRepository,UrunRepository urunRepository, KullaniciRepository kullaniciRepository){
        this.siparisRepository=siparisRepository;
        this.urunRepository=urunRepository;
        this.kullaniciRepository=kullaniciRepository;
    }

    @Transactional(readOnly = true)
    @GetMapping("/yonetim/dashboard")
    public String dashboardGoster(Model model) {
        long toplamUrun = urunRepository.count();
        long toplamKullanici = kullaniciRepository.count();

        List<Siparis> gecerliSiparisler = siparisRepository.findAll().stream()
                .filter(s -> s.getDurum() != null && !s.getDurum().equals("İPTAL EDİLDİ"))
                .collect(Collectors.toList());
        
        double toplamCiro = gecerliSiparisler.stream()
                .mapToDouble(Siparis::getToplamTutar)
                .sum();
        
        model.addAttribute("toplamUrun", toplamUrun);
        model.addAttribute("toplamKullanici", toplamKullanici);
        model.addAttribute("toplamCiro", String.format("%.2f",toplamCiro));
        model.addAttribute("toplamSiparis", gecerliSiparisler.size());

        Map<LocalDate,Double> gunlukSatislar = gecerliSiparisler.stream()
                .filter(s -> s.getSiparisTarihi()!=null)
                .collect(Collectors.groupingBy(
                    s -> s.getSiparisTarihi().toLocalDate(),
                    Collectors.summingDouble(Siparis::getToplamTutar)
                ));
        
        //Son 7 günü sırasıyla oluşturdum (Satış olmayan günler 0)
        List<String> tarihler = new ArrayList<>();
        List<Double> tutarlar = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM");

        for(int i=6; i>=0; i--)
        {
            LocalDate tarih = LocalDate.now().minusDays(i);
            tarihler.add(tarih.format(formatter));
            tutarlar.add(gunlukSatislar.getOrDefault(tarih, 0.0));
        }

        model.addAttribute("grafikTarihler", tarihler);
        model.addAttribute("grafikTutarlar", tutarlar);

        return "dashboard";
    }
    
}
