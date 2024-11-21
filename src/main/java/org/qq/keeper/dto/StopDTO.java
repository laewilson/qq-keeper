package org.qq.keeper.dto;




public class StopDTO  {

    private String appName;
    private int hours;
    private int minutes;

    private String shutdownType;
    public boolean isTimeToStop() {
        return (hours <= 0 && minutes <= 0);
    }


    public String getAppName() {
        return appName;
    }

    public StopDTO setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public int getHours() {
        return hours;
    }

    public StopDTO setHours(int hours) {
        this.hours = hours;
        return this;
    }

    public int getMinutes() {
        return minutes;
    }

    public StopDTO setMinutes(int minutes) {
        this.minutes = minutes;
        return this;
    }

    public String getShutdownType() {
        return shutdownType;
    }

    public StopDTO setShutdownType(String shutdownType) {
        this.shutdownType = shutdownType;
        return this;
    }
}
