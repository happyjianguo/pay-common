package com.dream.pay.nsq.retry;

import com.dream.pay.nsq.NsqConsumer;
import com.youzan.nsq.client.entity.NSQMessage;


/**
 * NSQ消息重发处理器.
 * 配合{@link NsqConsumer}使用,配置时注入.业务在listener抛出异常,就调用此处理器.
 *
 * @author mengzhenbin
 * @since 2016-08-11
 */
public interface NsqRetryHandler<T> {

    /**
     * 消息重发逻辑.
     *
     * @param originMessage 原始消息
     * @param bodyObj       消息体对象,已解码
     * @param nse           重发的异常
     * @return true: 继续重发 |false: 不再重发
     */
    boolean handle(NSQMessage originMessage, T bodyObj, NsqRequeueException nse);
}
