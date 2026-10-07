package com.example.siparis_sistemi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.siparis_sistemi.entity.Urun;

import com.example.siparis_sistemi.entity.Degerlendirme;
import java.util.List;


public interface DegerlendirmeRepository extends JpaRepository<Degerlendirme,Long>{

    List <Degerlendirme> findByUrun(Urun urun);
    
    // Bir ürünün yıldız ortalamasını veritabanı seviyesinde hızlıca hesaplar
    @Query("SELECT AVG(d.yildiz) FROM Degerlendirme d WHERE d.urun.id = :urunId")
    Double findOrtalamaYildizByUrunId(@Param("urunId") Long urunId);
}
