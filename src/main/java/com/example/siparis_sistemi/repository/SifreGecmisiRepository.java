package com.example.siparis_sistemi.repository;

import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.SifreGecmisi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SifreGecmisiRepository extends JpaRepository<SifreGecmisi, Long>{
    List<SifreGecmisi> findTop3ByKullaniciOrderByDegistirmeTarihiDesc(Kullanici kullanici);
}
