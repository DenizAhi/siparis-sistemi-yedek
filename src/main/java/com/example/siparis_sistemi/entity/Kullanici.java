package com.example.siparis_sistemi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Column;

@Entity
public class Kullanici {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable=false, unique=true)
    private String kullaniciAdi;

    private String sifre; // Güvenlik kuralı: Bu şifre ASLA düz metin olarak kaydedilmemeli!
    private String rol;   // "USER" veya "ADMIN"

    // Boş Constructor (JPA için zorunlu)
    public Kullanici() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getKullaniciAdi() { return kullaniciAdi; }
    public void setKullaniciAdi(String kullaniciAdi) { this.kullaniciAdi = kullaniciAdi; }
    
    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
