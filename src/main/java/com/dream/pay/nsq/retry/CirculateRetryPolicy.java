package com.dream.pay.nsq.retry;

import com.dream.pay.nsq.retry.NsqRequeueException;
import com.dream.pay.nsq.retry.NsqRetryHandler;
import com.youzan.nsq.client.entity.NSQMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 循环重试的消息策略
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
@Slf4j
public class CirculateRetryPolicy implements NsqRetryHandler {

    /**
     * 消息重试间隔时间,单位为秒
     */
    @Setter
    private int intervalInSecond = 60;

    /**
     * 最大延迟时间
     */
    private int MAX_REQUEUE_DELAY_SECOND = 60 * (60 - 1);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean handle(NSQMessage originMessage, Object message, NsqRequeueException nse) {
        //是否超限 消息重发
        int nextConsumingInSecond = (originMessage.getReadableAttempts() + 1) * intervalInSecond;
        try {
            if (nextConsumingInSecond > MAX_REQUEUE_DELAY_SECOND) {
                nextConsumingInSecond = MAX_REQUEUE_DELAY_SECOND;
            }
            //设置消息重试时间间隔, nsq客户端会根据这个字段去重试
            originMessage.setNextConsumingInSecond(nextConsumingInSecond);

            log.warn("[消息消费者]-设置消息{}s后重发,originMessage={},message={}",
                    nextConsumingInSecond,
                    originMessage, message);
            return true;
        } catch (Exception e) {
            log.error("[消息消费者]-设置消息重发异常,originMessage={},message={}", originMessage, message, e);
        }

        return false;
    }
}
