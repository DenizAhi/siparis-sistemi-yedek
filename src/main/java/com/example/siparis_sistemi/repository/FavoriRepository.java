package com.example.siparis_sistemi.repository;

import com.example.siparis_sistemi.entity.Favori;
import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.Urun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriRepository extends JpaRepository<Favori,Long> {

    List<Favori> findByKullanici(Kullanici kullanici);

    boolean existsByKullaniciAndUrun(Kullanici kullanici, Urun urun);

    Optional<Favori> findByKullaniciAndUrun(Kullanici kullanici, Urun urun);
    
}
