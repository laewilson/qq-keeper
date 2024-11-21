package org.qq.keeper.boot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;

@Slf4j
@Component
public class ServerInitListener implements ApplicationListener<WebServerInitializedEvent> {

    private static int port;
    public static int getPort() {
        return port;
    }
    private String systemUrl;
    @Autowired
    private ServletContext servletContext;
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        ServerInitListener.port = event.getWebServer().getPort();
        log.info("Detected real server port: " + port);
        openSystemUrl();
        QQKeeperBootUtil.writeAppInfo(getUrl());
    }

    public  void openSystemUrl() {
        String url = getUrl();
        log.info("open system url:  {}", url);
        QQKeeperBootUtil.openUrl(url);
    }
    public String getUrl() {
        if (systemUrl != null) {
            return systemUrl;
        }
        int port = ServerInitListener.getPort();
        String contextPath = servletContext.getContextPath();
        return systemUrl = "http://localhost:" + port + "/"+contextPath;
    }

}
