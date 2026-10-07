package com.example.siparis_sistemi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Urun {
    @Id   //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //auto-increment
    private long id;
    private String urunAdi;
    private double fiyat;

    private int stokAdedi;

    private String kategori;

    private boolean aktifMi=true;

    public Urun() {

    }

    public Urun(String urunAdi, double fiyat, int stokAdedi, String kategori, boolean aktifMi){
        this.urunAdi=urunAdi;
        this.fiyat=fiyat;   
        this.stokAdedi=stokAdedi;
        this.kategori=kategori;
        this.aktifMi=true;
    }
    
    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id=id;
    }

    public String getUrunAdi(){
        return urunAdi;
    }

    public void setUrunAdi(String urunAdi){
        this.urunAdi=urunAdi;
    }

    public double getFiyat(){
        return fiyat;
    }

    public void setFiyat(double fiyat){
        this.fiyat=fiyat;
    }

    public int getStokAdedi()
    {return stokAdedi;}

    public void setStokAdedi(int stokAdedi)
    {this.stokAdedi=stokAdedi;}

    public String getKategori()
    {return kategori;}

    public void setKategori(String kategori)
    { this.kategori=kategori;}

    public boolean isAktifMi()
    {return aktifMi;}

    public void setAktifMi(boolean aktifMi)
    { this.aktifMi=aktifMi;}
}
