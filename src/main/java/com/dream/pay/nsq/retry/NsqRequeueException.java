package com.dream.pay.nsq.retry;

import lombok.Data;

/**
 * 消息重发异常
 * <p>
 * 业务只要抛出这个异常,{@link com.youzan.nsq.client.Consumer}会捕捉,然后设置消息重发
 *
 * @author mengzhenbin
 * @since 2016-08-11
 */
@Data
public class NsqRequeueException extends RuntimeException {

    public NsqRequeueException(String message) {
        super(message);
    }

    public NsqRequeueException(Throwable throwable) {
        super(throwable);
    }

    public NsqRequeueException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
