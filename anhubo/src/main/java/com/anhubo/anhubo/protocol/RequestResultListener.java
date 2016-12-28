package com.anhubo.anhubo.protocol;

/**
 * 请求结果的监听器
 *
 * @param <T>
 * @author dzl
 */
public interface RequestResultListener<T> {

    /**
     * 当请求完成的时候会调用这个方法
     *
     * @param data 请求回来的JavaBean或者集合对象
     */
    void onRequestFinish(T data, int what);
    void showJsonStr(String str, int what);
}
