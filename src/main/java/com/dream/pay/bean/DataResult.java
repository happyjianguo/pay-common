package com.dream.pay.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 数据结果集
 *
 * @author mengzhenbin
 * @since 2016-09-06
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
