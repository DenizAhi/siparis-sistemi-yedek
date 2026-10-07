package com.example.siparis_sistemi.service;

import com.example.siparis_sistemi.entity.*;
import com.example.siparis_sistemi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OneriService {
    private final SiparisRepository siparisRepository;
    private final SiparisUrunuRepository siparisUrunuRepository;
    private final UrunRepository urunRepository;

    public OneriService(SiparisRepository siparisRepository, SiparisUrunuRepository siparisUrunuRepository,
                        UrunRepository urunRepository)
    {
        this.siparisRepository=siparisRepository;
        this.siparisUrunuRepository=siparisUrunuRepository;
        this.urunRepository=urunRepository;
    }

    @Transactional(readOnly = true)
    public List <Urun> kullaniciyaOzelOneriler(Kullanici kullanici){
        List<Siparis> gecmisSiparisler = siparisRepository.findByKullaniciOrderBySiparisTarihiDesc(kullanici);

        if(gecmisSiparisler.isEmpty()){
            List<Urun> tumUrunler = urunRepository.findAll();
            Collections.shuffle(tumUrunler);
            return tumUrunler.stream().limit(3).collect(Collectors.toList());
        }

        Map<String, Integer> kategoriSkorlari = new HashMap<>();

    for(Siparis siparis:gecmisSiparisler){
        List<SiparisUrunu> detaylar = siparisUrunuRepository.findBySiparis(siparis);
        for(SiparisUrunu detay: detaylar)
        {
            String kategori = detay.getUrun().getKategori();
            kategoriSkorlari.put(kategori,kategoriSkorlari.getOrDefault(kategori,0 ) + 1);
        }
    }
    String favoriKategori = "";
    int maxSkor=0;
    for(Map.Entry<String, Integer> entry : kategoriSkorlari.entrySet()){
        if (entry.getValue() > maxSkor) {  // ← if kontrolü EKLENDİ
                maxSkor = entry.getValue();
                favoriKategori = entry.getKey();
            }
    }

    if (favoriKategori == null || favoriKategori.isEmpty()) {
        return new ArrayList<>();
    }

    List<Urun> onerilenler = urunRepository.findByKategori(favoriKategori);
    Collections.shuffle(onerilenler);

    return onerilenler.stream().limit(3).collect(Collectors.toList());
    }
    
}
