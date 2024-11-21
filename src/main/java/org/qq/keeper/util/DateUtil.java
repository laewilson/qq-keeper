package org.qq.keeper.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String now() {
        return new SimpleDateFormat(FORMAT).format(new Date());
    }
}
