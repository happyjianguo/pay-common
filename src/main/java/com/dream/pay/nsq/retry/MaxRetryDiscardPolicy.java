package com.dream.pay.nsq.retry;

import com.youzan.nsq.client.entity.NSQMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 超过重发上限就丢弃消息策略
 *
 * @author xiukuan
 * @since 2016-09-06
 */
@Slf4j
public class MaxRetryDiscardPolicy implements NsqRetryHandler {

    /*消息重试间隔时间,单位为秒*/
    @Setter
    private int intervalInSecond = 60;

    /*消息最大重试次数:可配置,默认10*/
    @Setter
    int retryTimes = 10;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(NSQMessage originMessage, Object message, NsqRequeueException nse) {
        //是否超限
        if (originMessage.getReadableAttempts() <= retryTimes) {
            //消息重发
            int nextConsumingInSecond = (originMessage.getReadableAttempts() + 1) * intervalInSecond;
            try {
                //设置消息重试时间间隔, nsq客户端会根据这个字段去重试
                originMessage.setNextConsumingInSecond(nextConsumingInSecond);

                log.warn("[消息消费者]-设置消息{}s后重发,originMessage={},message={}", nextConsumingInSecond, originMessage, message);
                return true;
            } catch (Exception e) {
                log.error("[消息消费者]-设置消息重发异常,originMessage={},message={}", originMessage, message);
            }
        } else {
            //超限就丢弃了.
            log.error("[消息消费者]-消息重发次数超限[max={}],丢弃消息,originMessage={},message={}", retryTimes, originMessage, message);
        }
        return false;
    }
}
