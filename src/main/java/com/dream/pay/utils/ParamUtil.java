package com.dream.pay.utils;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dream.pay.other.MapFilter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author mengzhenbin
 * @date 2017年3月1日
 * @version V1.0
 * @descrption 参数处理工具<br/>
 */
public class ParamUtil {

	/**
	 * 转换Map<String, String[]>为Map<String, String><br/>
	 * 一般用于转换requet的参数：Map<String, String[]> map = request.getParameterMap();
	 * <br/>
	 * 把多个值用,连接
	 * 
	 * @param isKeepOrder
	 *            是否需要按照从map里取出的元素的顺序存放元素到paramMap
	 * @param requestParamMap
	 * @return
	 */
	public static Map<String, String> convertParamMap(Map<String, String[]> requestParamMap, boolean isKeepOrder) {
		Map<String, String> paramMap;
		if (isKeepOrder) {
			paramMap = Maps.newLinkedHashMap();
		} else {
			paramMap = Maps.newHashMap();
		}
		for (Entry<String, String[]> entry : requestParamMap.entrySet()) {
			paramMap.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
		}

		return paramMap;
	}

	/**
	 * 转换Map<String, String[]>为Map<String, String>并转换参数值编码<br/>
	 * 
	 * @param map
	 *            参数map
	 * @param fromCharset
	 *            参数值原编码
	 * @param toCharset
	 *            参数值目标编码
	 * @return
	 */
	public static Map<String, String> convertParamMap(Map<String, String[]> map, Charset fromCharset,
			Charset toCharset) {

		Map<String, String> paramMap = Maps.newHashMap();

		for (Entry<String, String[]> entry : map.entrySet()) {
			String string = StringUtils.join(entry.getValue(), ",");
			if (StringUtils.isNotEmpty(string)) {
				string = new String(string.getBytes(fromCharset), toCharset);
			}

			paramMap.put(entry.getKey(), string);
		}

		return paramMap;
	}

	/**
	 * 生成key1=value1&key3=value2&...的字符串
	 * 
	 * @param map
	 * @return
	 */
	public static String createParamString(Map<String, String> map) {
		if (MapUtils.isEmpty(map))
			return null;
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}

		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	/**
	 * 生成按照key的ascii码升序排列的key1=value1&key3=value2&...的字符串
	 * 
	 * @param map
	 * @return
	 */
	public static String createSortParamString(Map<String, String> map) {
		List<String> keyList = Lists.newArrayList(map.keySet());
		StringBuilder sb = new StringBuilder();
		Collections.sort(keyList);

		for (String key : keyList) {
			sb.append(key).append("=").append(map.get(key)).append("&");
		}

		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	/**
	 * map过滤，根据传入的过滤器对key和value进行过滤
	 * 
	 * @param map
	 * @param keyFilter
	 * @param valueFilter
	 * @return
	 */
	public static <K, V> Map<K, V> mapFilter(final Map<K, V> map, MapFilter<K> keyFilter, MapFilter<V> valueFilter) {
		Map<K, V> filterMap = Maps.newHashMap();

		if (keyFilter == null) {
			return mapValueFilter(map, valueFilter);
		}
		if (valueFilter == null) {
			return mapKeyFilter(map, keyFilter);
		}
		for (Entry<K, V> entry : map.entrySet()) {
			if (keyFilter.isKeyValid(entry) && valueFilter.isValueValid(entry)) {
				filterMap.put(entry.getKey(), entry.getValue());
			}
		}

		return filterMap;
	}

	/**
	 * map过滤，根据传入的过滤器对key进行过滤
	 * 
	 * @param map
	 * @param filter
	 * @return
	 */
	public static <K, V> Map<K, V> mapKeyFilter(final Map<K, V> map, MapFilter<K> filter) {
		Map<K, V> filterMap = Maps.newHashMap();

		if (filter == null) {
			return map;
		}

		for (Entry<K, V> entry : map.entrySet()) {
			if (filter.isKeyValid(entry)) {
				filterMap.put(entry.getKey(), entry.getValue());
			}
		}

		return filterMap;
	}

	/**
	 * map过滤，根据传入的过滤器对map的值进行过滤
	 * 
	 * @param map
	 * @param filter
	 * @return
	 */
	public static <K, V> Map<K, V> mapValueFilter(final Map<K, V> map, MapFilter<V> filter) {
		Map<K, V> filterMap = Maps.newHashMap();

		if (filter == null) {
			return map;
		}

		for (Entry<K, V> entry : map.entrySet()) {
			if (filter.isValueValid(entry)) {
				filterMap.put(entry.getKey(), entry.getValue());
			}
		}

		return filterMap;
	}

	/**
	 * map过滤，根据传入的filterValues参数对map的key进行过滤
	 * 
	 * @param map
	 * @param filterValues
	 * @return
	 */
	@SafeVarargs
	public static <K, V> Map<K, V> mapKeyFilter(final Map<K, V> map, K... filterValues) {
		MapFilter<K> filter = new MapFilter<K>(filterValues);

		return mapKeyFilter(map, filter);
	}

	/**
	 * map过滤，根据传入的filterValues参数对map的值进行过滤
	 * 
	 * @param map
	 * @param filterValues
	 * @return
	 */
	@SafeVarargs
	public static <K, V> Map<K, V> mapValueFilter(final Map<K, V> map, V... filterValues) {
		MapFilter<V> filter = new MapFilter<V>(filterValues);

		return mapValueFilter(map, filter);
	}
}
