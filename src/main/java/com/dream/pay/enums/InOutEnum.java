package com.dream.pay.enums;


/**
 * 收支类型通用配置
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
public enum InOutEnum {
    IN("收入", 1),
    OUT("支出", 2);

    private String name;
    private int value;

    private InOutEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public static InOutEnum select(int value) {
        for (InOutEnum inout : InOutEnum.values()) {
            if (inout.getValue() == value) {
                return inout;
            }
        }
        return null;
    }

}
