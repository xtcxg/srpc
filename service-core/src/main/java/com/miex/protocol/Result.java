package com.miex.protocol;

import java.io.Serializable;
import java.util.Map;

public class Result implements Serializable {

    Object value;

    int code;

    String msg;

    Map<String, Object> extra;

    Throwable throwable;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public Result() {

    }

    public Result(Object value) {
        this.value = value;
    }

    /**
     * Get invoke result.
     *
     * @return result. if no result return null.
     */
    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Get exception.
     *
     * @return exception. if no exception return null.
     */
    public Throwable getException() {
        return this.throwable;
    }

    public void setException(Throwable t) {
        this.throwable = t;
    }

    /**
     * Has exception.
     *
     * @return has exception.
     */
    public boolean hasException() {
        return null == this.throwable;
    }
}