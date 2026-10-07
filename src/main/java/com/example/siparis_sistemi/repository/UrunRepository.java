package com.example.siparis_sistemi.repository;

import com.example.siparis_sistemi.entity.Urun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UrunRepository extends JpaRepository<Urun, Long> {
    List<Urun> findByKategori(String kategori);

    // DİKKAT: Bu sorgu sadece ve sadece en az 1 kez favorilenmiş ürünleri getirir!
    @Query("SELECT f.urun FROM Favori f WHERE f.urun.kategori = :kategori GROUP BY f.urun ORDER BY COUNT(f) DESC")
    List<Urun> findEnCokFavorilenenUrunlerByKategori(@Param("kategori") String kategori);

    List<Urun> findByAktifMiTrue();
    List<Urun> findByKategoriAndAktifMiTrue(String kategori);
    List<Urun> findByAktifMiFalse();
}
