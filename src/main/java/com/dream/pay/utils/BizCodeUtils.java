package com.dream.pay.utils;

import com.dream.pay.enums.UnifiedBizCode;
import com.dream.pay.validators.BizCodeValidate;

/**
 * @author mengzhenbin
 * @version V1.0
 * @sine 2017年3月1日
 * @descrption 四码一号解析工具类<br/>
 */
public class BizCodeUtils {
    private static final String REG_EXP = "(?<=\\G.{4})";

    public static UnifiedBizCode parse(String code) {
        BizCodeValidate.checkFullCode(code);
        String[] codes = code.split("(?<=\\G.{4})");
        UnifiedBizCode unifiedBizCode = new UnifiedBizCode();
        unifiedBizCode.setBizProdCode(codes[0]);
        unifiedBizCode.setBizModeCode(codes[1]);
        unifiedBizCode.setBizActionCode(codes[2]);
        unifiedBizCode.setBizPayToolCode(codes[3]);
        unifiedBizCode.setBizChannelCode(codes[4]);
        return unifiedBizCode;
    }
}
