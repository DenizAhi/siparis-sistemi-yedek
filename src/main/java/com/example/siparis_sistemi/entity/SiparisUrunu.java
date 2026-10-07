package com.example.siparis_sistemi.entity;
import jakarta.persistence.*;

@Entity
public class SiparisUrunu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "siparis_id")
    private Siparis siparis;

    @ManyToOne
    @JoinColumn(name = "urun_id")
    private Urun urun;

    private int miktar;
    private Double birimFiyat; // Ürünün sipariş verildiği andaki fiyatı

    public SiparisUrunu() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Siparis getSiparis() { return siparis; }
    public void setSiparis(Siparis siparis) { this.siparis = siparis; }

    public Urun getUrun() { return urun; }
    public void setUrun(Urun urun) { this.urun = urun; }

    public int getMiktar() { return miktar; }
    public void setMiktar(int miktar) { this.miktar = miktar; }

    public Double getBirimFiyat() { return birimFiyat; }
    public void setBirimFiyat(Double birimFiyat) { this.birimFiyat = birimFiyat; }

}
