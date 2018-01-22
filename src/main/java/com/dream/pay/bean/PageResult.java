package com.dream.pay.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 分页数据结果集
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode
public class PageResult<T> extends DataResult<List<T>> {

    /**
     * 分页信息
     */
    private PageInfo pageInfo;

}
