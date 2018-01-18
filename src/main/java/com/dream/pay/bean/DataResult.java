package com.dream.pay.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据结果集
 *
 * @author mengzhenbin on 2017/1/10.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode
public class DataResult<T> extends BaseResult {

    /**
     * 结果集数据信息
     */
    private T data;

}
