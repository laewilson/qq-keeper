package org.qq.keeper.boot;

import lombok.extern.slf4j.Slf4j;
import org.qq.keeper.QQKeeperStarter;
import org.qq.keeper.util.DateUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QQKeeperBootUtil {
    private static final Class<?> CLASS = QQKeeperStarter.class;
    private static final String APP_NAME = "qq-keeper.jar";
    
    
    
    public static boolean appIsRunning() {
        List<String> pidList = getPid();
        if (pidList.isEmpty()) {
            return false;
        }
        AppInfo appInfo = readAppInfo();
        for (String pid : pidList) {
            if (pid.equalsIgnoreCase(appInfo.getPid())) {
                return true;
            }
        }
        return false ;
    }
    public static List<String> getPid() {
        try {
            return execJpsCmd();
        } catch (IOException e) {
            throw new IllegalStateException("获取进程号失败", e);
        }
    }

    private static List<String> execJpsCmd() throws IOException {
        Process process = Runtime.getRuntime().exec("jps -l");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        List<String> pidList= new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.contains(CLASS.getName())) {
                String[] parts = line.split(" ");
                String pid = parts[0];
                log.info("当前实例 {} 的 PID: {}", CLASS.getName(), pid);
                pidList.add(pid);
            }
            if (line.contains(APP_NAME)) {
                String[] parts = line.split(" ");
                String pid = parts[0];
                log.info("当前实例 {} 的 PID: {}", APP_NAME, pid);
                pidList.add(pid);
            }

        }
        return pidList;
    }
    public static void writeAppInfo(String url) {
        AppInfo appInfo = new AppInfo();
        appInfo.setUrl(url);
        List<String> pidList = getPid();
        appInfo.setPid(pidList.isEmpty() ? "" : pidList.get(0));
        appInfo.setBootTime(DateUtil.now());
        try {
            DynamicAppInfoWriter.writeAppInfo(appInfo, getAppInfoFilePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void openAppPage() {
        try {
            AppInfo appInfo = readAppInfo();
            openUrl(appInfo.getUrl());
        }catch (Exception e) {
            log.error("openSystemUrlFromFile error", e);
        }
    }

    public static AppInfo readAppInfo() {
        try {
            return DynamicAppInfoReader.readAppInfo(getAppInfoFilePath(), AppInfo.class);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void openUrl(String url) {
        try {
            Runtime.getRuntime().exec("cmd /c start " + url);
        } catch (Exception e) {
            log.error("openSystemUrl error", e);
        }
    }
    public static String getAppInfoDir() {
        String path = "c:\\qq-keeper\\";
        File file = new File(path);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                log.error("创建目录失败:" + path);
                return null;
            }
        }
        return path;
    }
    public static String getAppInfoFilePath() {
        return getAppInfoDir() + "app.info.properties";
    }
}
