package org.qq.keeper;

import lombok.extern.slf4j.Slf4j;
import org.qq.keeper.boot.QQKeeperBootUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.PortInUseException;

@Slf4j
@SpringBootApplication
public class QQKeeperStarter {
    private static final Class<?> CLASS = QQKeeperStarter.class;

    /**
     * 启动应用程序
     * 1.检查应用程序是否已经在运行,如果已经在运行，则不启动,顺便打开已启动的应用页面
     * 2.启动应用程序
     * 3.如果启动失败，并且是端口号冲突导致的，尝试重新启动，端口号加1
     * 4.启动成功，把应用信息写入启动文件
     * 5.打开浏览器，打开应用首页
     * @param args
     */
    public static void main(String[] args) {
        if (QQKeeperBootUtil.appIsRunning()) {
            log.info("============> 检测到其他进程正在运行, 直接打开其首页...");
            QQKeeperBootUtil.openAppPage();
            System.exit(0);
            return;
        }
        start(args);

    }


    public static void start(String[] args) {
        try {
            SpringApplication.run(CLASS, args);
        } catch (Exception e) {
            // 如果启动失败，并且是端口号冲突导致的，尝试重新启动，端口号加1
            if (e.getCause() instanceof PortInUseException) {
                startAnotherPort(args, (PortInUseException) e.getCause());
                return;
            }
            log.error("启动失败", e);
        }
    }


    private static void startAnotherPort(String[] args,PortInUseException portInUseException) {
        int port = portInUseException.getPort();
        int newPort = port + 1;
        System.setProperty("server.port", String.valueOf(newPort));
        log.error("============> 端口号({})冲突, 重新设置端口: {} ,并尝试重新启动....", port, newPort);
        start(args);
    }


}