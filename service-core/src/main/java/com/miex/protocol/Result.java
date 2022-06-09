package com.miex.protocol;

import java.io.Serializable;

public class Result implements Serializable {

    Object value;

    Throwable throwable;

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