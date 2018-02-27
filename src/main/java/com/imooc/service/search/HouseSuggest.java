package com.imooc.service.search;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-22 22:45
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
public class HouseSuggest {
    /**
     * 输入
     */
    private String input;

    /**
     * 默认权重
     */
    private int weight = 10;
}
