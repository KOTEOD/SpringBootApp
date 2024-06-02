package com.example.boot.modbus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Table
@Data
@Entity
public class DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private byte[] data;
}
