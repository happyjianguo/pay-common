package com.dream.pay.nsq;

/**
 * 消息过滤接口.
 *
 * @since 2016-08-15
 */
public interface Filter<T> {

    /**
     * 过滤方法,决定方法是否继续向下执行
     *
     * @param msg 消息
     * @return true:过滤掉消息,不继续执行|false:继续执行
     */
    boolean filter(T msg);

}
