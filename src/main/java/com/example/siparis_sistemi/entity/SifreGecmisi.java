package com.example.siparis_sistemi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SifreGecmisi {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @Column(nullable = false)
    private String sifreHash;

    @Column(nullable = false)
    private LocalDateTime degistirmeTarihi;

    public long getId() {return id;}
    public void setId(long id) {this.id=id;}

    public Kullanici getKullanici() {return kullanici;}
    public void setKullanici(Kullanici kullanici) {this.kullanici=kullanici;}

    public String getSifreHash() {return sifreHash;}
    public void setSifreHash(String sifreHash) {this.sifreHash=sifreHash;}

    public LocalDateTime getDegistirmeTarihi() {return degistirmeTarihi;}
    public void setDegistirmeTarihi(LocalDateTime degistirmeTarihi) {this.degistirmeTarihi=degistirmeTarihi;}
}
