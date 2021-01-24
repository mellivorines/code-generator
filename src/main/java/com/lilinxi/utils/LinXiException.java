package com.lilinxi.utils;

/**
 * 自定义异常
 *
 * @author lilinxi lilinxi015@163.com
 */
public class LinXiException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public LinXiException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public LinXiException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public LinXiException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public LinXiException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
