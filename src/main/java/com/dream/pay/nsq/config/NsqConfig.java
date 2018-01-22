package com.dream.pay.nsq.config;

import lombok.Data;

/**
 * Nsq 客户端配置.
 * <pre>
 * 主要是从properties属性文件中获取配置内容,给消费者和生产者使用.
 * </pre>
 *
 * @author mengzhenbin
 * @since 2016-08-11
 */
@Data
public class NsqConfig {

    /**
     * 消息编码
     **/
    public final static String ENCODING = "utf-8";

    /**
     * NSQ2.3 使用NSQConfig#setLookupAddresses配置DCC URL，通过DCC进行nsqlookupd中子节点发现。
     **/
    private String serverAddresses;

    /**
     * the thread_pool_size for IO running. It is also
     */
    private int threadPoolSize4IO;

    /**
     * used for Netty.
     * 客户端和nsqd服务器连接池连接数 默认值为30
     */
    private int connectionPoolSize;

    //=================================All of Timeout==============================
    /**
     * the message interactive timeout between client and server
     */
    private int msgTimeoutInMillisecond;
    /**
     * Perform a TCP connecting action
     */
    private int connectTimeoutInMillisecond;
    /**
     * Perform one interactive action between request and response underlying Netty handling TCP
     */
    private int queryTimeoutInMillisecond;

}
