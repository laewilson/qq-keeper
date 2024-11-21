package org.qq.keeper.dto;



import java.io.Serializable;



public class Result<T> implements Serializable {

    public final static int SUCCESS = 200;
    public final static int SERVER_ERROR = 500;
    public final static int REQUEST_ERROR = 400;
    public final static int LOGIN_ERROR = 401;
    public final static int PERM_ERROR = 403;


    private int code = SUCCESS;
    private String message = "success";
    private T data;

    /**
     * Here is  the default method for
     *
     * @return
     */
    public  boolean isSuccess(){
        return this.code == SUCCESS || this.code == 0;
    }
    public  boolean isFailed(){
        return !isSuccess();
    }

    public Result<?> success() {
        return new Result<>();
    }

    public Result<T> success(T data) {
        return new Result<T>().setData(data);
    }

    public Result<?> success(String message) {
        return new Result<>().setMessage(message);
    }
    public Result<T>  success(String message,T data) {
        return new Result<T>().setMessage(message).setData(data);
    }


    public Result<T>  error(int code, String message, T data) {
        return new Result<T>()
                .setCode(code)
                .setMessage(message)
                .setData(data);
    }

    public Result<?> error400() {
        return error400("request error");
    }

    public Result<?> error400(String message) {
        return error(REQUEST_ERROR, message, null);
    }

    public Result<?> error500(String message) {
        return error(SERVER_ERROR, message, null);
    }

    public Result<?> error500() {
        return error500("server error");
    }

    public Object error(Integer code, String message) {
        return error(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }
}
