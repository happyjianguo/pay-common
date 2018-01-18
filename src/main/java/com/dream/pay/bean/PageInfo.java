package com.dream.pay.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分页信息
 *
 * @author 冰宁 on 2017/1/10.
 */
@Data
@ToString
@NoArgsConstructor
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
        int totalPage = ((int) Math.ceil(1.0*totalNum/pageSize));
        return totalPage == 0 ? 1 : totalPage;
    }

    /**
     * 分页信息对象构造方法
     *
     * @param pageNo 页码
     * @param pageSize 每页数据量
     * @param totalNum 总数据量
     */
    public PageInfo(int pageNo, int pageSize, long totalNum) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalNum = totalNum;
    }

}
