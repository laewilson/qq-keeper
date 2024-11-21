package org.qq.keeper.boot;

import lombok.Data;

@Data
public class AppInfo {


    private String name = "QQTVKeeper";
    private String version = "1.0";
    private String description = "QQKeeper is a tool kids' TV keeper";

    private String url;
    private String pid;
    private String email = "laewilson72@gmail.com";
    private String author = "Wilson Lai";
    private String license = "MIT";
    private String bootTime;


}
