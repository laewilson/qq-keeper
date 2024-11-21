package org.qq.keeper.boot;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class DynamicAppInfoReader {

    public static <T> T readAppInfo(String filePath,Class<T> clazz) throws InstantiationException, IllegalAccessException, IOException {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }

        T bean = clazz.newInstance(); // 创建一个新的Bean实例
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); // 必须设置为true才能访问私有字段
            String key = field.getName();
            String value = properties.getProperty(key);
            if (value != null) {
                Class<?> fieldType = field.getType();
                Object fieldValue = convertStringToType(value, fieldType);
                field.set(bean, fieldValue);
            }
        }

        return bean;
    }

    private static Object convertStringToType(String value, Class<?> fieldType) throws IllegalAccessException {
        if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(value);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == float.class || fieldType == Float.class) {
            return Float.parseFloat(value);
        } else if (fieldType == double.class || fieldType == Double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == String.class) {
            return value;
        } else {
            throw new IllegalArgumentException("Unsupported type: " + fieldType);
        }
    }

    public static void main(String[] args) {
        try {
            AppInfo appInfo = readAppInfo("filePath",AppInfo.class);
            System.out.println("name: " + appInfo.getName());
            System.out.println("version: " + appInfo.getVersion());
            System.out.println("description: " + appInfo.getDescription());
            System.out.println("url: " + appInfo.getUrl());
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }
}