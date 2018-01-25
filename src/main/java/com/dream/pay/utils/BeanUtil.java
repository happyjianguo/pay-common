package com.dream.pay.utils;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author mengzhenbin
 * @version V1.0
 * @sine 2017年3月1日
 * @descrption 与org.apache.commons.beanutils的BeanUtils功能类似<br/>
 * 通过BeanUtilsBean处理
 */
public class BeanUtil {
    private static final BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
    private static final ConvertUtilsBean convertUtilsBean = beanUtilsBean.getConvertUtils();

    static {
        DateConverter dateConverter = new DateConverter(null);
        String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss.SSS", "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd"};
        dateConverter.setPatterns(patterns);
        convertUtilsBean.register(dateConverter, Date.class);
    }

    /**
     * Map转换为业务bean
     *
     * @param map
     * @param valueType
     * @return
     */
    public static <T> T fromMap(Map<String, ?> map, Class<T> valueType) {
        if (map == null) {
            return null;
        }
        try {
            T bean = valueType.newInstance();
            beanUtilsBean.populate(bean, map);
            return bean;
        } catch (Exception e) {
            throw new RuntimeException("BeanUtil.fromMap error", e);
        }
    }

    /**
     * 业务bean转map
     *
     * @param t
     * @return
     */
    public static <T> Map<String, Object> toMap(T t) {
        Map<String, Object> map = Maps.newHashMap();
        PropertyDescriptor[] propertyArray = PropertyUtils.getPropertyDescriptors(t);

        try {
            for (PropertyDescriptor property : propertyArray) {
                String propertyName = property.getName();
                // 过滤class属性
                if (!propertyName.equals("class")) {
                    Object value = property.getReadMethod().invoke(t);
                    map.put(propertyName, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("BeanUtil.toMap error", e);
        }

        return map;
    }

    /**
     * 转换成valueType类型对象
     *
     * @param obj
     * @param valueType
     * @return
     * @throws ClassCastException
     */
    public static <T> T convert(Object obj, Class<T> valueType) {
        if (obj == null) {
            return null;
        }
        Object converted = convertUtilsBean.convert(obj, valueType);
        return valueType.cast(converted);
    }

    /**
     * 转换{@code Map<K, ?>}为{@code Map<K, V>}
     *
     * @param map
     * @param valueType
     * @return
     * @throws ClassCastException
     */
    public static <K, V> Map<K, V> convertMap(Map<K, ?> map, Class<V> valueType) {
        if (map == null) {
            return null;
        }
        Map<K, V> convertedMap = new HashMap<K, V>();
        for (Entry<K, ?> entry : map.entrySet()) {
            convertedMap.put(entry.getKey(), valueType.cast(convertUtilsBean.convert(entry.getValue(), valueType)));
        }

        return convertedMap;
    }

    /**
     * 转换{@code List<?> list}为{@code List<T>}
     *
     * @param list
     * @param valueType
     * @return
     * @throws ClassCastException
     */
    public static <T> List<T> convertList(List<?> list, Class<T> valueType) {
        if (list == null) {
            return null;
        }

        List<T> convertedList = new ArrayList<T>();
        for (Object obj : list) {
            convertedList.add(valueType.cast(convertUtilsBean.convert(obj, valueType)));
        }

        return convertedList;
    }

    /**
     * 复制bean属性<br/>
     * 与org.apache.commons.beanutils的BeanUtils.copyProperties功能一致<br/>
     * 但把异常包装成RuntimeException
     *
     * @param dest
     * @param orig
     */
    public static void copyProperties(Object dest, Object orig) {
        try {
            beanUtilsBean.copyProperties(dest, orig);
        } catch (Exception e) {
            throw new RuntimeException("BeanUtil.copyProperties error", e);
        }
    }

    /**
     * 把字符串分隔并转成{@code List<T>}
     *
     * @param string    字符串
     * @param valueType 值类型
     * @return
     */
    public static <T> List<T> splitToList(String string, Class<T> valueType) {
        return splitToList(string, valueType, ",");
    }

    /**
     * 把字符串分隔并转成{@code List<T>}
     *
     * @param string     字符串
     * @param valueType  值类型
     * @param splitRegex 分隔符
     * @return {@code List<T>}
     * @throws ClassCastException
     */
    public static <T> List<T> splitToList(String string, Class<T> valueType, String splitRegex) {

        List<T> valueList = new ArrayList<T>();

        if (StringUtils.isBlank(string)) {
            return valueList;
        }

        String[] stringArray = string.split(splitRegex);
        if (valueType == String.class) {
            for (String str : stringArray) {
                valueList.add(valueType.cast(str));
            }
            return valueList;
        }

        for (String str : stringArray) {
            valueList.add(valueType.cast(convertUtilsBean.convert(str, valueType)));
        }

        return valueList;
    }
}
