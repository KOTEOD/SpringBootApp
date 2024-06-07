package com.example.boot.modbus.repository;

import com.example.boot.modbus.entity.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataRepository extends JpaRepository<DataEntity, Long> {
    // Методы для взаимодействия с настройками в базе данных
}