package com.dream.pay.nsq.decode;

import com.dream.pay.nsq.config.NsqConfig;
import com.dream.pay.nsq.NsqConsumer;
import com.dream.pay.utils.JsonUtil;
import com.youzan.nsq.client.entity.NSQMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * Nsq 消息解析工具类
 *
 * @author mengzhenbin
 * @since 2016-08-11
 */
@Slf4j
public class NsqMessageDecoderUtil {

    /**
     * 将消息体解析为字符串
     */
    public static String getBodyStr(NSQMessage message, NsqConsumer nsqConsumer) {
        try {
            return new String(message.getMessageBody(), NsqConfig.ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error("[消息消费者]-转化消息字节为字符串异常,topic={},channel={},message={}", nsqConsumer.getTopic(), nsqConsumer.getChannel(), message);
        }
        return null;
    }

    /**
     * 转化消息内容为消息对象
     * <p>
     * json->bean
     */
    public static Object JSON2Bean(String json, Class cls, NsqConsumer nsqConsumer) {
        try {
            return JsonUtil.fromJson(json, cls);
        } catch (Exception e) {
            log.error("[消息消费者]-消息体转化为对象异常,topic={},channel={},json={},class={}", nsqConsumer.getTopic(), nsqConsumer.getChannel(), json, cls);
        }
        return null;
    }

}
