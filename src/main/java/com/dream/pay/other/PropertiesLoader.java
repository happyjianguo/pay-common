package com.dream.pay.other;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption <br/>
 * 加载配置属性文件<br/>
 * 属性文件编码: UTF-8<br/>
 * System.开头的属性及system.properties文件里的属性写入系统属性
 */
public class PropertiesLoader {
    private static final String CLASSPATH_PROPS_FILE = "classpath*:conf/*.properties";
    private static final String SYSTEM_PROPS_FILE = "classpath:conf/system.properties";
    private static final String SYSTEM_PROPS_PREFIX = "system.";
    private static final Charset PROPS_FILE_CHARSET = Charset.forName("UTF-8");
    private static final Properties properties = new Properties();

    static {
        load();
    }

    public static Properties getProperties() {
        return properties;
    }

    private static void load() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(CLASSPATH_PROPS_FILE);
            for (Resource resource : resources) {
                Properties props = PropertiesLoaderUtils
                        .loadProperties(new EncodedResource(resource, PROPS_FILE_CHARSET));
                properties.putAll(props);
            }
        } catch (IOException e) {
            throw new RuntimeException("配置文件读取失败", e);
        }

        setSystemProperties(properties);
    }

    /**
     * 把SYSTEM_PROPS_FILE文件的属性文件写进系统属性<br/>
     * 把SYSTEM_PROPS_PREFIX开头的配置写进系统属性
     *
     * @param properties
     */
    private static void setSystemProperties(Properties properties) {
        Resource resource = new ClassPathResource(SYSTEM_PROPS_FILE);
        if (resource.exists()) {
            try {
                Properties props = PropertiesLoaderUtils
                        .loadProperties(new EncodedResource(resource, PROPS_FILE_CHARSET));
                for (Entry<Object, Object> entry : props.entrySet()) {
                    System.setProperty(entry.getKey().toString(), entry.getValue().toString());
                }
            } catch (IOException e) {
                throw new RuntimeException("配置文件读取失败", e);
            }
        }

        for (Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(SYSTEM_PROPS_PREFIX)) {
                System.setProperty(key, entry.getValue().toString());
            }
        }
    }
}
