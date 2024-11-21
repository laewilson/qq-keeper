package org.qq.keeper.stop;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Win10Stop implements StopApp {

    private static List<String> stopTwiceApps = new ArrayList<>(Arrays.asList("mgtv"));

    @Override
    public void stop(String appName) {
        stopInternal(appName);
        //
        if (stopTwiceApps.contains(appName)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stopInternal(appName);
        }

    }

    @Override
    public void shutdown(String shutdownType) {
        String shutdownParam = "-p";
        if ("shutdown-h".equals(shutdownType)) {
            shutdownParam = "-h";
        }

        try {
            ProcessBuilder pb = new ProcessBuilder("shutdown",shutdownParam);
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                log.info("Shutting down the system...");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void stopInternal(String appName) {
        List<String> pids = findPids(appName);
        StringBuilder killCommand = new StringBuilder("taskkill");
        for (String pid : pids) {
            killCommand.append(" /PID ").append(pid);
        }
        log.info("kill command:{}",killCommand);

        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", killCommand.toString());
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                log.info("Process with PID(s) " + String.join(",", pids) + " was successfully killed.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private List<String> findPids(String appName){
        List<String> pids = new ArrayList<>();
        try {
            // 调用批处理脚本并传递参数
            String command = String.format("tasklist |findstr \"%s\"",appName);
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
            Process p = pb.start();
            // 读取批处理脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(),"GBK"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                //多个空格替换成一个
                line = line.replaceAll("\\s+"," ");
                line = line.replaceAll("\t"," ");
                String[] parts = line.split(" ");
                if (parts.length > 1) {
                    String pid = parts[1];
                    pids.add(pid);
                }

            }
            // 读取错误流（如果有）
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
            // 等待批处理脚本执行完毕
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                log.info("Process with name \"" + appName + "\" was found and its PID is " + String.join(",", pids));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pids;
    }
    public static void main(String[] args) {
        Win10Stop win10Stop = new Win10Stop();
            win10Stop.stop("mgtv");
    }
}
