package com.dream.pay.bean;

import lombok.*;

import java.io.Serializable;

/**
 * 分页对象
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PageInfo implements Serializable {

    private static final long serialVersionUID = -5073276071155636598L;

    /**
     * 页码
     */
    private int pageNo;

    /**
     * 页数据量
     */
    private int pageSize;

    /**
     * 总条数
     */
    private long totalNum;

    /**
     * 总页数
     */
    public int getTotalPage() {
        int totalPage = ((int) Math.ceil(1.0 * totalNum / pageSize));
        return totalPage == 0 ? 1 : totalPage;
    }
}
