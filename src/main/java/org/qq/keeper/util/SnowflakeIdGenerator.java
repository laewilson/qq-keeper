package org.qq.keeper.util;

public class SnowflakeIdGenerator {

    // 每一部分占用的位数
    private final static long WORKER_ID_BITS = 5L;
    private final static long DATACENTER_ID_BITS = 5L;
    private final static long SEQUENCE_BITS = 12L;

    // 每一部分的最大值
    private final static long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS);
    private final static long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BITS);

    // 每一部分向左移动的位数
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    // 纪元时间（单位：毫秒），可以根据需要调整
    private final static long EPOCH = 1288834974657L; // 2010-11-04 09:42:54.657

    // 成员变量
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", MAX_DATACENTER_ID));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // 获取下一个ID
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            // 同一毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 同一毫秒的序列号溢出，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置为0
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // ID组合与移位
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT) |
               (datacenterId << DATACENTER_ID_SHIFT) |
               (workerId << WORKER_ID_SHIFT) |
               sequence;
    }

    // 等待直到下一毫秒
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    // 获取当前时间戳（单位：毫秒）
    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator idWorker = new SnowflakeIdGenerator(1, 1);
        for (int i = 0; i < 10; i++) {
            System.out.println(idWorker.nextId());
        }
    }
}