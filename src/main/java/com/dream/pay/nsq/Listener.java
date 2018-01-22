package com.dream.pay.nsq;

import com.dream.pay.reflect.Genericable;
import lombok.Data;

/**
 * 消息监听器.
 * T: 消息对象类型
 * <pre>
 * 接收消息后的业务逻辑通过继承此类实现
 * </pre>
 *
 * @author mengzhenbin
 * @since 2017-08-11
 */
@Data
public abstract class Listener<T> implements Genericable<T> {

    /**
     * 收到消息后会调用此方法
     * <p>
     * 如果方法抛出异常,nsq会重发消息的
     *
     * @param msg 消息实体
     */
    public abstract void onEvent(T msg);

}
