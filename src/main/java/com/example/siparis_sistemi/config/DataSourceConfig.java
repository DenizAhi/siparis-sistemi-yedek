package com.example.siparis_sistemi.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {
    
    @Bean      //Master veritabanını ayağa kaldır
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean 
    @ConfigurationProperties(prefix = "spring.datasource.replica")
    public DataSource replicaDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean 
    public DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource,
                                        @Qualifier("replicaDataSource") DataSource replicaDataSource){

            RoutingDataSource routingDataSource = new RoutingDataSource();

            Map<Object, Object> dataSourceMap = new HashMap<>();
            dataSourceMap.put(DbType.MASTER, masterDataSource);
            dataSourceMap.put(DbType.REPLICA, replicaDataSource);

            routingDataSource.setTargetDataSources(dataSourceMap);
            routingDataSource.setDefaultTargetDataSource(masterDataSource);  //varsayılan

            return routingDataSource;
    }

    @Primary 
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource){
        //Veritabanına bağlanmadan önce saltokunur olup olmadığını anlamak için bekletir
        return new LazyConnectionDataSourceProxy(routingDataSource);  //çok önemli!!
    }
    


}
