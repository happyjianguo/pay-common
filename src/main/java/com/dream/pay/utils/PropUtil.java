package com.dream.pay.utils;

import com.dream.pay.other.PropertiesLoader;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

/**
 * 属性获取工具<br/>
 *
 * @author chenjianchunjs
 */
public class PropUtil {
    public static final Properties properties = PropertiesLoader.getProperties();

    /**
     * 获取所有属性
     *
     * @return
     */
    public static Properties getProperties() {
        return properties;
    }

    /**
     * 检查属性值是否存在
     *
     * @param key
     * @return
     */
    public static boolean exists(String key) {
        return properties.containsKey(key);
    }

    /**
     * 检查属性值是否空白值<br/>
     * 与StringUtils.isBlank规则一致
     *
     * @param key
     * @return
     */
    public static boolean isBlank(String key) {
        return StringUtils.isBlank(properties.getProperty(key));
    }

    /**
     * 检查属性值是否非空白值<br/>
     * 与StringUtils.isNotBlank规则一致
     *
     * @param key
     * @return
     */
    public static boolean isNotBlank(String key) {
        return StringUtils.isNotBlank(properties.getProperty(key));
    }

    /**
     * 获取属性字符串值
     *
     * @param key 属性key
     * @return
     */
    public static String get(String key) {
        return properties.getProperty(key, "").trim();
    }

    /**
     * 获取属性字符串值
     *
     * @param key          属性key
     * @param defaultValue 默认值
     * @return
     */
    public static String get(String key, String defaultValue) {
        return StringUtils.trim(properties.getProperty(key, defaultValue));
    }

    /**
     * 获取属性值并解析为int返回<br/>
     * 属性不存在或解析int失败返回0
     *
     * @param key 属性key
     * @return
     */
    public static int getInt(String key) {
        String property = properties.getProperty(key);
        return NumberUtils.toInt(property);
    }

    /**
     * 获取属性值并解析为int返回<br/>
     * 属性不存在或解析int失败返回默认值
     *
     * @param key          属性key
     * @param defaultValue 默认值
     * @return
     */
    public static int getInt(String key, int defaultValue) {
        String property = properties.getProperty(key);
        return NumberUtils.toInt(property, defaultValue);
    }

    /**
     * 获取属性值并解析为boolean返回<br/>
     * 属性不存在或解析boolean失败返回false
     *
     * @param key 属性key
     * @return
     */
    public static boolean getBoolean(String key) {
        String property = properties.getProperty(key);
        return Boolean.valueOf(property);
    }

    /**
     * 获取属性值并解析为boolean返回<br/>
     * 属性存在并equalsIgnoreCase("true")时返回真，否则返回false
     *
     * @param key          属性key
     * @param defaultValue 默认值
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String property = properties.getProperty(key);
        if (property == null) {
            return defaultValue;
        }
        return Boolean.valueOf(property);
    }

    /**
     * 获取属性值并用分隔符分隔返回List&lt;String&gt;
     *
     * @param key       属性key
     * @param separator 分隔符
     * @return
     */
    public static List<String> getList(String key, String separator) {
        if (!properties.containsKey(key)) {
            return Collections.emptyList();
        }
        return Splitter.on(separator).trimResults().splitToList(properties.getProperty(key));
    }

    /**
     * 获取属性值并用分隔符分隔返回List&lt;T&gt;
     *
     * @param key       属性key
     * @param separator 分隔符
     * @param valueType 值类型
     * @return
     */
    public static <T> List<T> getList(String key, String separator, Class<T> valueType) {
        if (!properties.containsKey(key)) {
            return Collections.emptyList();
        }
        return BeanUtil.convertList(Splitter.on(separator).trimResults().splitToList(properties.getProperty(key)),
                valueType);
    }

    /**
     * 获取属性值并用分隔符分隔返回Set
     *
     * @param key       属性key
     * @param separator 分隔符
     * @return
     */
    public static Set<String> getSet(String key, String separator) {
        if (!properties.containsKey(key)) {
            return Collections.emptySet();
        }
        return new HashSet<String>(Splitter.on(separator).trimResults().splitToList(properties.getProperty(key)));
    }

    /**
     * 获取属性值并用分隔符分隔返回Set
     *
     * @param key       属性key
     * @param separator 分隔符
     * @param valueType 值类型
     * @return
     */
    public static <T> Set<T> getSet(String key, String separator, Class<T> valueType) {
        if (!properties.containsKey(key)) {
            return Collections.emptySet();
        }
        return new HashSet<T>(BeanUtil
                .convertList(Splitter.on(separator).trimResults().splitToList(properties.getProperty(key)), valueType));
    }

    /**
     * 获取枚举
     *
     * @param key
     * @param valueType
     * @return
     */
    public static <T extends Enum<T>> T getEnum(String key, Class<T> valueType) {
        return Enum.valueOf(valueType, get(key));
    }

    /**
     * 获取枚举
     *
     * @param key
     * @param valueType
     * @param defaultValue
     * @return
     */
    public static <T extends Enum<T>> T getEnum(String key, Class<T> valueType, T defaultValue) {
        if (!properties.containsKey(key)) {
            return defaultValue;
        }
        return Enum.valueOf(valueType, get(key));
    }

    /**
     * 获取属性值并用分隔符分隔返回Set
     *
     * @param key       属性key
     * @param separator 分隔符
     * @param valueType 值类型
     * @return
     */
    public static <T extends Enum<T>> Set<T> getEnumSet(String key, String separator, Class<T> valueType) {
        List<String> sourceList = getList(key, separator);
        Set<T> targetSet = Sets.newHashSet();
        for (String item : sourceList) {
            targetSet.add(Enum.valueOf(valueType, item));
        }
        return targetSet;
    }

    /**
     * 获取属性值并用分隔符分隔返回List
     *
     * @param key       属性key
     * @param separator 分隔符
     * @param valueType 值类型
     * @return
     */
    public static <T extends Enum<T>> List<T> getEnumList(String key, String separator, Class<T> valueType) {
        List<String> sourceList = getList(key, separator);
        List<T> targetList = Lists.newArrayList();
        for (String item : sourceList) {
            targetList.add(Enum.valueOf(valueType, item));
        }
        return targetList;
    }

}
