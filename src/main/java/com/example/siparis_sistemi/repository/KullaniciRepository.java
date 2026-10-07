package com.example.siparis_sistemi.repository;

import com.example.siparis_sistemi.entity.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KullaniciRepository extends JpaRepository<Kullanici, Long> {
    // Spring Data JPA, "findBy" kelimesini görünce bu sorguyu kendi yazar:
    // SELECT * FROM Kullanici WHERE kullaniciAdi = ?
    Optional<Kullanici> findByKullaniciAdi(String kullaniciAdi);
}