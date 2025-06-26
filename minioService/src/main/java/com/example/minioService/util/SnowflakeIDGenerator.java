package com.example.minioService.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnowflakeIDGenerator {
    private final long epoch = 1672531200000L; // Custom epoch (e.g., Jan 1, 2023)
    private final long machineIdBits = 10L;
    private final long sequenceBits = 12L;
    private final long maxMachineId = (1L << machineIdBits) - 1;
    private final long maxSequence = (1L << sequenceBits) - 1;

    private final long machineIdShift = sequenceBits;
    private final long timestampShift = sequenceBits + machineIdBits;

    private long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIDGenerator(@Value("${snowflake.machine-id}") long machineId) {
        if (machineId > maxMachineId || machineId < 0) {
            throw new IllegalArgumentException("Machine ID out of range");
        }
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards!");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0) {
                while ((timestamp = System.currentTimeMillis()) <= lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - epoch) << timestampShift) | (machineId << machineIdShift) | sequence;
    }
}
