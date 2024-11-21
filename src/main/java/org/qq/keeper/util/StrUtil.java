package org.qq.keeper.util;

/**
 * 字符串工具类
 */
public class StrUtil {

    /**
     *  判断多个字符串，是否有一个为空
     * @param strings
     * @return
     */
    public static boolean isEmpty(String... strings) {
        for (String s : strings) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isEmpty(String str){
        return str == null || str.length() == 0 || str.trim().isEmpty()||"null".equals(str);
    }
    public static boolean isEmpty(Object value){
        if (null == value){
            return true;
        }
        if (value instanceof  String){
            return isEmpty(String.valueOf(value));
        }
        return false;
    }

    public static boolean isNotEmpty(String... strs){
        for (String s : strs) {
            if (isEmpty(s)) {
                return false;
            }
        }
        return true;
    }



}
