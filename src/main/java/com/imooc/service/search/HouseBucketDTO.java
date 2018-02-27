package com.imooc.service.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-23 15:22
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
public class HouseBucketDTO {
    /**
     * 聚合bucket的key
     */
    private String key;

    /**
     * 聚合的结果值
     */
    private long count;
}
