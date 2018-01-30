package com.dream.pay.nsq;

import com.dream.pay.nsq.config.NsqConfig;
import com.dream.pay.utils.JsonUtil;
import com.youzan.nsq.client.Producer;
import com.youzan.nsq.client.ProducerImplV2;
import com.youzan.nsq.client.entity.Message;
import com.youzan.nsq.client.entity.NSQConfig;
import com.youzan.nsq.client.entity.Topic;
import com.youzan.nsq.client.exception.NSQException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.UnsupportedEncodingException;

/**
 * Nsq 生产者.
 * <pre>
 * 主要是从properties属性文件中获取配置内容,给消费者和生产者使用.
 * </pre>
 *
 * @author mengzhenbin
 * @since 2016-08-11
 */
@Slf4j
public class NsqProducer implements InitializingBean, DisposableBean {

    /**
     * Nsq客户端配置
     **/

    @Setter
    NsqConfig nsqConfig;
    /**
     * 主题
     */
    @Setter
    String topic;


    /**
     * 是否使用顺序消费,设置顺序消费的 shardingID
     */
    @Setter
    Long shardingID;


    //Nsq 生产者
    private Producer producer;


    /**
     * 启动, 连接NSQ服务器
     */
    public void start() throws NSQException {
        //构造配置
        NSQConfig config = new NSQConfig();
        config.setLookupAddresses(nsqConfig.getServerAddresses());
        if (nsqConfig.getThreadPoolSize4IO() > 0) {
            config.setThreadPoolSize4IO(nsqConfig.getThreadPoolSize4IO());
        }

        if (nsqConfig.getMsgTimeoutInMillisecond() > 0) {
            config.setMsgTimeoutInMillisecond(nsqConfig.getMsgTimeoutInMillisecond());
        }
        if (nsqConfig.getConnectTimeoutInMillisecond() > 0) {
            config.setConnectTimeoutInMillisecond(nsqConfig.getConnectTimeoutInMillisecond());
        }

        if (nsqConfig.getQueryTimeoutInMillisecond() > 0) {
            config.setQueryTimeoutInMillisecond(nsqConfig.getQueryTimeoutInMillisecond());
        }

        if (nsqConfig.getConnectionPoolSize() > 0) {
            config.setConnectionPoolSize(nsqConfig.getConnectionPoolSize());
        }

        //启动生产者
        producer = new ProducerImplV2(config);
        producer.start();
    }


    /**
     * 构造发送的消息体,目前采用json格式
     **/
    private byte[] buildMessage(String topic, Object msg) {

        //转化为json格式,发送消息
        String json = JsonUtil.toJson(msg);
        try {
            return json.getBytes(NsqConfig.ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("[消息生产者者]-获取消息字节流异常,topic={bizNo},,message={}", topic, msg, e);
            return null;
        }

    }

    /**
     * 发送消息
     *
     * @param topic 主题
     * @param msg   消息实体,建议纯粹是POJO,不要太复杂.
     * @return true|false
     */
    public void send(String topic, Object msg) throws NSQException {

        //指令 内容都不能为空
        if (topic == null || msg == null) {
            log.warn("无法消息:topic或消息为空,topic={},msg={}", topic, msg);
            return;
        }

        if (null != shardingID && shardingID > 0) {
            //发送顺序消息，通过 shardingID hash 到指定的 partition
            Message message = Message.create(new Topic(topic), JsonUtil.toJson(msg));
            message.setTopicShardingIDLong(shardingID);
            this.producer.publish(message);
        } else {
            // 发送普通消息
            byte[] message = buildMessage(topic, msg);
            this.producer.publish(message, topic);
        }
    }

    /**
     * 发送消息
     *
     * @param msg 消息实体,建议纯粹是POJO,不要太复杂.
     * @return true|false
     */
    public void send(Object msg) throws NSQException {

        this.send(topic, msg);

    }

    /** ----implments ---**/

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    /**
     * {@inheritDoc}
     * <p>
     * 释放资源
     */
    @Override
    public void destroy() throws Exception {
        if (producer != null) {
            producer.close();

        }
    }
}
