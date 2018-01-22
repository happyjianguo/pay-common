package com.dream.pay.other;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author mengzhenbin
 * @version V1.0
 * @date 2017年3月1日
 * @descrption Map过滤器，键值对：根据类型和值集合判断Map的Entry元素是否有效
 */
public class MapFilter<T> {
    private Set<T> valueSet;

    @SafeVarargs
    public MapFilter(T... values) {
        Set<T> valueSet = new HashSet<T>(Arrays.asList(values));
        this.valueSet = valueSet;
    }

    public MapFilter(Set<T> valueSet) {
        this.valueSet = valueSet;
    }

    public boolean isKeyValid(Entry<T, ?> entry) {
        if (entry == null)
            throw new RuntimeException("entry is null");
        if (this.valueSet == null || this.valueSet.size() == 0)
            return true;

        return !this.valueSet.contains(entry.getKey());
    }

    public boolean isValueValid(Entry<?, T> entry) {
        if (entry == null)
            throw new RuntimeException("entry is null");
        if (this.valueSet == null || this.valueSet.size() == 0)
            return true;
        return !this.valueSet.contains(entry.getValue());
    }
}