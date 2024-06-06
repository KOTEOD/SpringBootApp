package com.example.boot.modbus.controller;

import com.example.boot.modbus.entity.SettingsEntity;
import com.example.boot.modbus.repository.SettingsRepository;
import com.example.boot.modbus.service.COMPortManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/modbus")
public class ModbusTcpServerController {

    private final SettingsRepository settingsRepository;
    private final COMPortManager comPortManager;

    @Autowired
    public ModbusTcpServerController(SettingsRepository settingsRepository, COMPortManager comPortManager) {
        this.settingsRepository = settingsRepository;
        this.comPortManager = comPortManager;
    }

    @GetMapping
    public SettingsEntity getSettings() {
        return settingsRepository.findTopByOrderByIdDesc();
    }

    @PostMapping
    public void updateSettings(@RequestBody SettingsEntity newSettings) {
        // Сохранение новых настроек в базе данных
        settingsRepository.save(newSettings);

        // Перезапуск COM-порта с новыми настройками
        comPortManager.closePort();
        comPortManager.openPort(newSettings.getPortName(), newSettings.getBaudRate(), newSettings.getDataBits(), newSettings.getStopBits(), newSettings.getParity());
    }

}
