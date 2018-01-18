package com.dream.pay.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 分页数据结果集
 *
 * @author 冰宁 on 2017/1/10.
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
