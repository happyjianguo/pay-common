package com.dream.pay.bean;

import lombok.Data;

/**
 * 参数校验结果类
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
@Data
public class ValidationResult {

    /*校验通过?*/
    private boolean success;

    /*校验失败描述*/
    private String message;

    public ValidationResult() {

    }

    public ValidationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
