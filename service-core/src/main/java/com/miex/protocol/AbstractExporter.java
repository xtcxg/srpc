package com.miex.protocol;

public abstract class AbstractExporter<T> implements Exporter<T> {

    protected Class<T> type;

    protected T target;

    protected String name;

    public AbstractExporter(String name, Class<T> type, T target) {
        this.name = name;
        this.type = type;
        this.target = target;
    }

    public AbstractExporter(Class<T> type, T target) {
        this.type = type;
        this.target = target;
    }

    @Override
    public void export() {

    }

    @Override
    public Result invoke(InvocationHandler handler) {
        return doInvoke(handler);
    }

    public abstract Result doInvoke(InvocationHandler handler);

    public T getTarget() {
        return this.target;
    }
}
