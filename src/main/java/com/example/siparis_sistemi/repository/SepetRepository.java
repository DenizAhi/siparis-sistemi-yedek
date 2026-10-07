package com.example.siparis_sistemi.repository;

import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.SepetUrunu;
import com.example.siparis_sistemi.entity.Urun;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional; 

public interface SepetRepository extends JpaRepository<SepetUrunu, Long> {
    // Güvenlik: Sadece aktif giriş yapmış kullanıcının sepet ürünlerini getirir
    List<SepetUrunu> findByKullanici(Kullanici kullanici);

    // YENİ EKLENEN KISIM: Sepette belirli bir kullanıcının belirli bir ürünü var mı diye kontrol eden metot
    Optional<SepetUrunu> findByKullaniciAndUrun(Kullanici kullanici, Urun urun);
}
