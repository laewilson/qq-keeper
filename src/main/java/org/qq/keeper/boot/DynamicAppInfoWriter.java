package org.qq.keeper.boot;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class DynamicAppInfoWriter {

    public static void writeAppInfo(Object bean,String filePath) throws IllegalAccessException, IOException {
        Properties properties = new Properties();
        // 使用反射获取所有字段
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // 必须设置为true才能访问私有字段
            String key = field.getName();
            String value = field.get(bean).toString();
            properties.setProperty(key, value);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // 添加注释
            properties.store(fos, "Dynamic Application Information");
        }
    }

    public static void main(String[] args) {
        AppInfo appInfo = new AppInfo();
        appInfo.setUrl("https://github.com/zhangjianqiang19850624/QQKeeper");
        appInfo.setPid("2365");
        try {
            writeAppInfo(appInfo,"C:\\qq-keeper\\app.info.properties");
            System.out.println("App info has been written to file dynamically.");
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }
}