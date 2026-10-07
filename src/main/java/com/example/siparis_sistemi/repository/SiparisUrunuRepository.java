package com.example.siparis_sistemi.repository;

import com.example.siparis_sistemi.entity.Siparis;
import com.example.siparis_sistemi.entity.SiparisUrunu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiparisUrunuRepository extends JpaRepository<SiparisUrunu, Long> {
    List<SiparisUrunu> findBySiparis(Siparis siparis);
}