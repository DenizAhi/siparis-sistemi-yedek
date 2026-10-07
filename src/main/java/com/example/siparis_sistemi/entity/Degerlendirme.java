package com.example.siparis_sistemi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Degerlendirme {
    
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime tarih = LocalDateTime.now();

    @Column(nullable = false)
    private int yildiz;

    @Column (length = 500)
    private String yorumMetni;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @ManyToOne
    @JoinColumn(name="urun_id", nullable = false)
    private Urun urun;

    public long getId() {   return id;  }
    public void setId(long id) {    this.id=id; }

    public LocalDateTime getTarih () {     return tarih;   }
    public void setTarih( LocalDateTime tarih) {    this.tarih=tarih;   }

    public int getYildiz () {   return yildiz; }
    public void setYildiz(int yildiz) { this.yildiz=yildiz;}

    public String getYorumMetni ()  {   return yorumMetni; }
    public void setYorumMetni( String yorumMetni)  {    this.yorumMetni=yorumMetni; }

    public Kullanici getKullanici() {   return kullanici;   }
    public void setKullanici(Kullanici kullanici) {     this.kullanici=kullanici;   }

    public Urun getUrun() {     return urun;    }
    public void setUrun(Urun urun) {    this.urun=urun;     }
    
}
