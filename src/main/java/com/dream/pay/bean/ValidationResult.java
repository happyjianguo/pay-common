package com.dream.pay.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数校验结果类
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    /*校验通过?*/
    private boolean success;

    /*校验失败描述*/
    private String message;

}
