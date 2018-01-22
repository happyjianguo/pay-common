package com.dream.pay.nsq.decode;

import com.dream.pay.nsq.NsqConsumer;
import com.youzan.nsq.client.entity.NSQMessage;

/**
 * Nsq 消息解析器
 * <pre>
 * 主要是从properties属性文件中获取配置内容,给消费者和生产者使用.
 * </pre>
 *
 * @author mengzhenbin
 * @since 2016-08-11
 */
public interface NsqMessageDecoder {

    /**
     * 消息解析方法
     * <p>
     * 不要在实现中做任何消息解析以外的操作
     *
     * @param message     nsq 原始消息
     * @param nsqConsumer 使用本次解析的 nsqConsumer 对象
     * @return
     */
    Object decode(NSQMessage message, NsqConsumer nsqConsumer);

}
