package com.example.boot.modbus.entity;



import javax.persistence.*;

@Table

@Entity
public class DataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private byte[] data;

    public DataEntity(Long id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    public DataEntity() {

    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
