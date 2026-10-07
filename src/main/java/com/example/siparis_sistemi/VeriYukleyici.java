package com.example.siparis_sistemi;

import com.example.siparis_sistemi.entity.Urun;
import com.example.siparis_sistemi.repository.UrunRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  //başlangıç temel ayarları
public class VeriYukleyici {
    @Bean
    CommandLineRunner veritabaniniDoldur(UrunRepository urunRepository) {
        return args -> {
            // SADECE veritabanında hiç ürün yoksa (count 0 ise) bu bloğa gir ve ürünleri ekle
            if (urunRepository.count() == 0) {
                // repository kullanarak veritabanına yeni Urun nesneleri kaydediyoruz.
                urunRepository.save(new Urun("Laptop", 25000.00,10, "Teknoloji", true));
                urunRepository.save(new Urun("Akıllı Telefon", 15000.00,5 , "Teknoloji" , true));
                urunRepository.save(new Urun("Mekanik Klavye", 2500.00,20 , "Teknoloji" , true));
                
                System.out.println(">>> Örnek ürünler veritabanına başarıyla yüklendi! <<<");
            } else {
                // Eğer ürünler zaten varsa, konsola bu mesajı yazdırıp eklemeyi atla
                System.out.println(">>> Ürünler veritabanında zaten mevcut, tekrar eklenmedi. <<<");
            }
        };
    }
}
