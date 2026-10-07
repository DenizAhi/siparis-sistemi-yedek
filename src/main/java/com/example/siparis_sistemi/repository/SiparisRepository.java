package com.example.siparis_sistemi.repository;

import com.example.siparis_sistemi.entity.Kullanici;
import com.example.siparis_sistemi.entity.Siparis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiparisRepository extends JpaRepository<Siparis, Long> {
    // Kullanıcının geçmiş siparişlerini listelemek için kullanacağız
    List<Siparis> findByKullaniciOrderBySiparisTarihiDesc(Kullanici kullanici);
    
    List<Siparis> findAllByOrderBySiparisTarihiDesc();
}