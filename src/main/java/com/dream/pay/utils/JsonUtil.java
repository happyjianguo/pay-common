package com.dream.pay.utils;

import com.dream.pay.constants.Constant;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption Json序列化与反序列化工具类
 * <p>
 * 使用jackson 1.9.x版本<br />
 * 由于目前Dubbo还是使用jackson 1.9.x做序列化，保持一致
 * </p>
 */
public class JsonUtil {
    @Getter//获取定制化的ObjectMapper
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateFormat df = new SimpleDateFormat(Constant.DATE_DEFAULT_FORMAT);

    static {
        // 序列化配置
        objectMapper.setSerializationConfig(objectMapper.copySerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL).without(Feature.FAIL_ON_EMPTY_BEANS));

        // 反序列化配置
        objectMapper.setDeserializationConfig(objectMapper.copyDeserializationConfig()
                .without(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES));

        // 日期序列化与反序列化
        objectMapper.setDateFormat(df);
    }

    /**
     * 对象转JSON
     *
     * @param t 对象
     * @return
     */
    public static <T> String toJson(T t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (IOException e) {
            throw new RuntimeException("JsonUtil.toJson error", e);
        }
    }

    /**
     * 对象转JSON
     *
     * @param t 对象
     * @return
     */
    public static <T> String toJsonPrettyPrint(T t) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(t);
        } catch (IOException e) {
            throw new RuntimeException("JsonUtil.toJson error", e);
        }
    }

    /**
     * JSON转对象
     *
     * @param jsonString json字符串
     * @param valueType  对象类型
     * @return
     */
    public static <T> T fromJson(String jsonString, Class<T> valueType) {
        if (StringUtils.isBlank(jsonString))
            return null;

        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (IOException e) {
            throw new RuntimeException("JsonUtil.fromJson error", e);
        }

    }

    /**
     * @param jsonString    json字符串
     * @param typeReference 泛型信息
     * @return
     */
    public static <T> T fromJson(String jsonString, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(jsonString))
            return null;

        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("JsonUtil.fromJson error", e);
        }
    }

}
