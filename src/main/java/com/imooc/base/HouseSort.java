package com.imooc.base;

import com.google.common.collect.Sets;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * @author gusuchen
 * Created in 2018-01-21 14:56
 * Description: 排序生成器
 * Modified by:
 */
public class HouseSort {

    public static final String DEFAULT_SORT_KEY = "lastUpdateTime";

    public static final String DISTANCE_TO_SUBWAY = "distanceToSubway";

    public static final String CREATE_TIME = "createTime";

    public static final String PRICE = "price";

    public static final String AREA = "area";

    private static final Set<String> SORT_KEYS = Sets.newHashSet(
            DEFAULT_SORT_KEY,
            CREATE_TIME,
            PRICE,
            AREA,
            DISTANCE_TO_SUBWAY
    );

    public static Sort generateSort(String orderByKey, String directionKey) {
        orderByKey = getSortKey(orderByKey);

        Sort.Direction direction = Sort.Direction.fromStringOrNull(directionKey);
        if (direction == null) {
            direction = Sort.Direction.DESC;
        }
        return new Sort(direction, orderByKey);
    }

    public static String getSortKey(String key) {
        if (!SORT_KEYS.contains(key)) {
            return DEFAULT_SORT_KEY;
        }
        return key;
    }
}
