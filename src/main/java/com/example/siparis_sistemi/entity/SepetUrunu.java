package com.example.siparis_sistemi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SepetUrunu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bir sepet ürünü, tek bir kullanıcıya aittir 
    @ManyToOne
    @JoinColumn(name = "kullanici_id")
    private Kullanici kullanici;

    // Birden çok sepete eklenen ürün , aslında tek bir ürüne aittir (tek id'li ürüne)
    @ManyToOne
    @JoinColumn(name = "urun_id")
    private Urun urun;

    // Üründen kaç adet alındığı
    private int miktar;

    // Boş Constructor
    public SepetUrunu() {}

    // Getter ve Setter metotları
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Kullanici getKullanici() { return kullanici; }
    public void setKullanici(Kullanici kullanici) { this.kullanici = kullanici; }

    public Urun getUrun() { return urun; }
    public void setUrun(Urun urun) { this.urun = urun; }

    public int getMiktar() { return miktar; }
    public void setMiktar(int miktar) { this.miktar = miktar; }

}
