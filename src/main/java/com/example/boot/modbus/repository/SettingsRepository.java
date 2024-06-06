package com.example.boot.modbus.repository;

import com.example.boot.modbus.entity.SettingsEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<SettingsEntity, Long> {
    SettingsEntity findTopByOrderByIdDesc();
    // Методы для взаимодействия с настройками в базе данных
}
