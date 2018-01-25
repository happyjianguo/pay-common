package com.dream.pay.utils;

import java.math.BigDecimal;

/**
 * @author mengzhenbin
 * @version V1.0
 * @sine 2017年3月1日
 * @descrption 数字处理工具类<br/>
 * <p>
 * <li>用于从包装类型转为基本类型，主要为了防止空指针异常
 * <li>如果需要从String转为数值，请使用org.apache.commons.lang3.math.NumberUtils
 */
public class NumberUtil {

    /**
     * Shot转shot, null返回0
     *
     * @param source
     * @return
     */
    public static short shortValue(Short source) {
        return shortValue(source, (short) 0);
    }

    /**
     * Shot转shot, null返回默认值
     *
     * @param source
     * @param defaultValue 默认值
     * @return
     */
    public static short shortValue(Short source, final short defaultValue) {
        if (null == source)
            return defaultValue;
        return source.shortValue();
    }

    /**
     * Integer转int, null返回0
     *
     * @param source
     * @return
     */
    public static int intValue(Integer source) {
        return intValue(source, 0);
    }

    /**
     * Integer转int, null返回默认值
     *
     * @param source
     * @param defaultValue 默认值
     * @return
     */
    public static int intValue(Integer source, final int defaultValue) {
        if (null == source)
            return defaultValue;
        return source.intValue();
    }

    /**
     * Long转long, null返回0l
     *
     * @param source
     * @return
     */
    public static long longValue(Long source) {
        return longValue(source, 0l);
    }

    /**
     * Long转long, null返回默认值
     *
     * @param source
     * @param defaultValue 默认值
     * @return
     */
    public static long longValue(Long source, final long defaultValue) {
        if (null == source)
            return defaultValue;
        return source.longValue();
    }

    /**
     * Float转float, null返回0f
     *
     * @param source
     * @return
     */
    public static float floatValue(Float source) {
        return floatValue(source, 0f);
    }

    /**
     * Float转float, null返回默认值
     *
     * @param source
     * @param defaultValue 默认值
     * @return
     */
    public static float floatValue(Float source, final float defaultValue) {
        if (null == source)
            return defaultValue;
        return source.floatValue();
    }

    /**
     * Double转double, null返回0d
     *
     * @param source
     * @return
     */
    public static double doubleValue(Double source) {
        return doubleValue(source, 0d);
    }

    /**
     * Double转double, null返回默认值
     *
     * @param source
     * @param defaultValue 默认值
     * @return
     */
    public static double doubleValue(Double source, final double defaultValue) {
        if (null == source)
            return defaultValue;
        return source.doubleValue();
    }

    /**
     * 是否相等，注意当s/t为null时取默认值
     *
     * @param s
     * @param t
     * @return
     */
    public static boolean equals(Short s, Short t) {
        return shortValue(s) == shortValue(t);
    }

    /**
     * 是否相等，注意当s/t为null时取默认值
     *
     * @param s
     * @param t
     * @return
     */
    public static boolean equals(Integer s, Integer t) {
        return intValue(s) == intValue(t);
    }

    /**
     * 是否相等，注意当s/t为null时取默认值
     *
     * @param s
     * @param t
     * @return
     */
    public static boolean equals(Long s, Long t) {
        return longValue(s) == longValue(t);
    }

    /**
     * 是否相等，注意当s/t为null时取默认值
     *
     * @param s
     * @param t
     * @return
     */
    public static boolean equals(Float s, Float t) {
        return floatValue(s) == floatValue(t);
    }

    /**
     * 是否相等，注意当s/t为null时取默认值
     *
     * @param s
     * @param t
     * @return
     */
    public static boolean equals(Double s, Double t) {
        return doubleValue(s) == doubleValue(t);
    }

    /**
     * BigDecimal相加, null作为BigDecimal.ZERO
     *
     * @param args
     * @return
     */
    public static BigDecimal add(BigDecimal... args) {
        BigDecimal target = BigDecimal.ZERO;
        if (args == null) {
            return target;
        }
        for (BigDecimal item : args) {
            if (null != item)
                target = target.add(item);
        }
        return target;
    }
}
