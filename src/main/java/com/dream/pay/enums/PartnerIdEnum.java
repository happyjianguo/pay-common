package com.dream.pay.enums;

import lombok.Getter;

/**
 * 业务线
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
public enum PartnerIdEnum {
    SHANGCHENG("10000001", "微商城"),
    XIAODIAN("10000002", "微小店"),
    SHOUYIN("10000003", "收银"),
    LINGSHOU("10000004", "零售"),
    MEIYE("10000005", "美业"),
    CANYIN("10000006", "餐饮"),
    PIFA("10000007", "批发"),;
    @Getter
    private final String code;
    @Getter
    private final String description;

    /**
     * 私有构造函数。
     *
     * @param code        code
     * @param description 描述
     */
    PartnerIdEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param code code
     * @return 类型枚举
     */
    public static PartnerIdEnum getByCode(String code) {
        for (PartnerIdEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }


    /**
     * 通过枚举<code>code</code>获得枚举
     *
     * @param name name
     * @return 类型枚举
     */
    public static PartnerIdEnum select(String name) {
        for (PartnerIdEnum e : values()) {
            if (e.name().equals(name)) {
                return e;
            }
        }
        return null;
    }
}
