package org.qq.keeper.util;

import java.util.concurrent.atomic.AtomicLong;

public class ShortUrlGenerator {

    private static final String BASE62_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final long BASE = 62L;
    private static final int URL_LENGTH = 6;

    // 模拟的分布式ID生成器
    private static AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public static void main(String[] args) {
        SnowflakeIdGenerator idWorker = new SnowflakeIdGenerator(1, 1);

        // 测试生成并打印几个短链接
        for (int i = 0; i < 5; i++) {
            long distributedId = idWorker.nextId();
            String shortUrl = encodeToBase62(distributedId);
            System.out.println("Generated Short URL: " + shortUrl);
        }
    }

    // 模拟生成分布式ID的方法
    private static synchronized long generateDistributedId() {
        return idGenerator.incrementAndGet();
    }

    // 将长整型ID转换为Base62编码的6位字符串
    private static String encodeToBase62(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            sb.append(BASE62_CHARS.charAt((int) (id % BASE)));
            id /= BASE;
        }
        // 如果结果长度不足6位，则用'0'补齐
        while (sb.length() < URL_LENGTH) {
            sb.append('0');
        }
        // 翻转字符串以确保高位字符在前
        return sb.reverse().toString();
    }
}