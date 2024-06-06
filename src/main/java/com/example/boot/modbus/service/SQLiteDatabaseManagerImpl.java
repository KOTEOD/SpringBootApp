package com.example.boot.modbus.service;

import com.example.boot.modbus.entity.DataEntity;
import com.example.boot.modbus.entity.SettingsEntity;
import com.example.boot.modbus.repository.DataRepository;
import com.example.boot.modbus.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class SQLiteDatabaseManagerImpl implements SQLiteService {
    private final DataRepository dataRepository;
    private final SettingsRepository settingsRepository;

    @Autowired
    public SQLiteDatabaseManagerImpl(DataRepository dataRepository, SettingsRepository settingsRepository) {
        this.dataRepository = dataRepository;
        this.settingsRepository = settingsRepository;
    }

    @Transactional
    @Override
    public void saveData(byte[] data) {
        DataEntity entity = new DataEntity();
        entity.setData(data);
        dataRepository.save(entity);
    }

    @Transactional
    @Override
    public String readSettings() {
        SettingsEntity entity = settingsRepository.findById(1L).orElse(null);
        return entity != null ? entity.getSettingsJson() : "{}";
    }

    @Transactional
    @Override
    public void updateSettings(String jsonSettings) {
        SettingsEntity entity = settingsRepository.findById(1L).orElse(null);
        if (entity == null) {
            entity = new SettingsEntity();
            entity.setId(1L);
        }
        entity.setSettingsJson(jsonSettings);
        settingsRepository.save(entity);
    }
}
