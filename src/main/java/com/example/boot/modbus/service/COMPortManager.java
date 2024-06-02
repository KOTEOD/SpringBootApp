package com.example.boot.modbus.service;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.stereotype.Component;

@Component
public class COMPortManager {

    COMPortManager (){}

    private SerialPort serialPort;

    public void openPort(String portName, int baudRate, int dataBits, int stopBits, int parity) {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.setBaudRate(baudRate);
        serialPort.setNumDataBits(dataBits);
        serialPort.setNumStopBits(stopBits);
        serialPort.setParity(parity);
        if (!serialPort.openPort()) {
            throw new RuntimeException("Failed to open COM port " + portName);
        }
    }

    public void closePort() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    public void writeData(byte[] data) {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.writeBytes(data, data.length);
        } else {
            throw new RuntimeException("COM port is not open");
        }
    }

    public byte[] readData() {
        if (serialPort != null && serialPort.isOpen()) {
            byte[] readBuffer = new byte[1024];
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            byte[] actualData = new byte[numRead];
            System.arraycopy(readBuffer, 0, actualData, 0, numRead);
            return actualData;
        } else {
            throw new RuntimeException("COM port is not open");
        }
    }
}
