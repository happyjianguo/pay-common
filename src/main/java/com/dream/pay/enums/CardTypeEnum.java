package com.dream.pay.enums;

/**
 * 卡类型
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
public enum CardTypeEnum {
    DEBIT("D", "借记卡"), CREDIT("C", "贷记卡");
    private String value;
    private String desc;

    CardTypeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static CardTypeEnum select(String value) {
        if (value == null) {
            return null;
        }
        for (CardTypeEnum e : CardTypeEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        return null;
    }

    public static String toDesc(String value) {
        CardTypeEnum cardType = select(value);
        return cardType == null ? null : cardType.desc;
    }
}
