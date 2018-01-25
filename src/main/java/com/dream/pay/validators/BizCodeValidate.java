package com.dream.pay.validators;

import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption 四码一号校验<br/>
 */
@NoArgsConstructor
public class BizCodeValidate {
    private static final String REG_EXP_CODE = "^[0-9A-F]*$";

    public static void checkSingleCode(String code) {
        if (code != null && code.length() == 4) {
            checkLegal(code);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void checkFullCode(String code) {
        if (code != null && code.length() == 20) {
            checkLegal(code);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void checkLegal(String code) {
        if (!Pattern.matches("^[0-9A-F]*$", code)) {
            throw new IllegalArgumentException();
        }
    }
}
