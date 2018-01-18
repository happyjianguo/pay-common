package com.dream.pay.bean;

import lombok.*;

import java.io.Serializable;

/**
 * Created by mengzhenbin on 2017/10/9.
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class BaseResult implements Serializable {
    private static final long serialVersionUID = 1949910043360896391L;
    private boolean success;
    private String code;
    private String message;
    private String requestId;
    private String responseId;
}
