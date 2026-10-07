package com.example.siparis_sistemi.entity;

import jakarta.persistence.*;

@Entity
public class Favori {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "kullanici_id", nullable = false)
    private Kullanici kullanici;

    @ManyToOne 
    @JoinColumn(name = "urun_id", nullable = false)
    private Urun urun;

    public Long getId(){
        return id;
    }
    public void setId(long id){
        this.id=id;
    }

    public Kullanici getKullanici(){
        return kullanici;
    }

    public void setKullanici(Kullanici kullanici)
    {
        this.kullanici=kullanici;
    }

    public Urun getUrun(){
        return urun;
    }

    public void setUrun(Urun urun){
        this.urun=urun;
    }
}
