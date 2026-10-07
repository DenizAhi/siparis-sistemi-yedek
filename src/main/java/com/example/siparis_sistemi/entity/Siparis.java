package com.example.siparis_sistemi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import java.util.List;

@Entity
public class Siparis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id")
    private Kullanici kullanici;

    @OneToMany(mappedBy = "siparis", cascade = CascadeType.ALL)
    private List<SiparisUrunu> siparisUrunleri;

    private LocalDateTime siparisTarihi;
    private Double toplamTutar;
    private String durum; // Örn: "ALINDI", "HAZIRLANIYOR", "TAMAMLANDI"

    public Siparis() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Kullanici getKullanici() { return kullanici; }
    public void setKullanici(Kullanici kullanici) { this.kullanici = kullanici; }

    public LocalDateTime getSiparisTarihi() { return siparisTarihi; }
    public void setSiparisTarihi(LocalDateTime siparisTarihi) { this.siparisTarihi = siparisTarihi; }

    public Double getToplamTutar() { return toplamTutar; }
    public void setToplamTutar(Double toplamTutar) { this.toplamTutar = toplamTutar; }

    public String getDurum() { return durum; }
    public void setDurum(String durum) { this.durum = durum; }

    public List<SiparisUrunu> getSiparisUrunleri() { 
        return siparisUrunleri; 
    }
    
    public void setSiparisUrunleri(List<SiparisUrunu> siparisUrunleri) { 
        this.siparisUrunleri = siparisUrunleri; 
    }

}
