package com.dream.pay.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付工具类型枚举
 *
 * @author mengzhenbin
 * @since 2016-09-06
 */
public enum PayToolType {
    /**
     * 支付工具类型
     */
    QUICK_PAY((byte) 1, "QUICK_PAY", "快捷支付"),//借记卡快捷，贷记卡快捷
    PLAT_PAY((byte) 2, "PLAT_PAY", "平台支付"),//支付宝，微信，快钱，易宝，银联，财付通，百付宝
    BANK_PAY((byte) 4, "BANK_PAY", "网银支付"),//银行直连，三方间连
    ACCOUNT_PAY((byte) 8, "ACCOUNT_PAY", "虚币支付"),//E卡
    VIRTUAL_PAY((byte) 16, "VIRTUAL_PAY", "账户支付"),//余额，储值卡余额，礼品卡余额
    CREDIT_PAY((byte) 32, "CREDIT_PAY", "信用支付"),//白条，花呗，买呗
    ACTIVE_PAY((byte) 64, "ACTIVE_PAY", "营销活动抵扣"),//立减，立返，满减，满返
    DAI_SHOU((byte) 128, "DAI_SHOU", "代收"),//水电燃气费，信用卡还款业务
    DAI_FU((byte) 256, "DAI_FU", "代付"),//提现
    ;

    private final byte key;
    private final String chlCode;
    private final String value;

    PayToolType(byte key, String chlCode, String value) {
        this.value = value;
        this.chlCode = chlCode;
        this.key = key;
    }

    public int key() {
        return key;
    }

    public String value() {
        return value;
    }

    public String chlCode() {
        return chlCode;
    }

    public static PayToolType toPayToolType(int value) {
        for (PayToolType payToolType : PayToolType.values()) {
            if (payToolType.key == value) {
                return valueOf(payToolType.name());
            }
        }
        return null;
    }

    public static PayToolType toPayToolType(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        for (PayToolType payToolType : PayToolType.values()) {
            if (StringUtils.equals(payToolType.name().toLowerCase(), name.toLowerCase())) {
                return valueOf(payToolType.name());
            }
        }
        return null;
    }

    public static List<PayToolType> toPayToolArray(Integer payMode) {
        List<PayToolType> payTools = new ArrayList<PayToolType>();
        for (PayToolType payToolType : PayToolType.values()) {
            if ((payMode & payToolType.key()) > 0) {
                payTools.add(payToolType);
            }
        }
        return payTools;
    }
}
