package com.dream.pay.nsq.decode;

import com.dream.pay.nsq.NsqConsumer;
import com.dream.pay.reflect.Reflections;
import com.youzan.nsq.client.entity.NSQMessage;

/**
 * 默认的 NSQ 消息解析器
 *
 * 通过 listener 中参数注解推断数据类型
 * String or Bean
 *
 * @author 冰宁 on 2017/7/31.
 */
public class DefaultNsqMessageDecoder implements NsqMessageDecoder {

    @Override
    public Object decode(NSQMessage message, NsqConsumer nsqConsumer) {

        // 将body字符串转化为这个类型的对象
        String bodyStr = NsqMessageDecoderUtil.getBodyStr(message, nsqConsumer);

        return decode(bodyStr, nsqConsumer);
    }

    /**
     * 对 nsq 消息体进行解析
     *
     * @param bodyMessage nsq 消息体字符串
     * @param nsqConsumer 使用本次解析的 nsqConsumer 对象
     * @return
     */
    protected Object decode(String bodyMessage, NsqConsumer nsqConsumer) {
        // 反射拿到消息体类型
        Class bodyType = Reflections.getClassGenricType(nsqConsumer.getListener().getClass());

        if (bodyType == String.class) {
            // 如果是String,有可能不是json格式的(兼容以前的用法)
            return bodyMessage;
        } else {
            // JSON->Object  [默认是json格式哦]
            return NsqMessageDecoderUtil.JSON2Bean(bodyMessage, bodyType, nsqConsumer);
        }
    }

}
