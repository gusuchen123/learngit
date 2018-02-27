package com.imooc.base;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author gusuchen
 * Created in 2018-01-21 13:27
 * Description: 带区间的常用数值定义
 * Modified by:
 */
@AllArgsConstructor
public class RentValueBlock {
    /**
     * 价格区间定义
     */
    public static final Map<String, RentValueBlock> PRICE_BLOCK;

    /**
     * 面积区间定义
     */
    public static final Map<String, RentValueBlock> AREA_BLOCK;

    /**
     * 无限制区间
     */
    public static final RentValueBlock ALL = new RentValueBlock("*", -1, -1);

    static {
        PRICE_BLOCK = ImmutableMap.<String, RentValueBlock>builder()
                .put("*-1000", new RentValueBlock("*-1000", -1, 1000))
                .put("1000-3000", new RentValueBlock("1000-3000", 1000, 3000))
                .put("3000-*", new RentValueBlock("3000-*", 3000, -1))
                .build();

        AREA_BLOCK = ImmutableMap.<String, RentValueBlock>builder()
                .put("*-30", new RentValueBlock("*-30", -1, 30))
                .put("30-50", new RentValueBlock("30-50", 30, 50))
                .put("50-*", new RentValueBlock("50-*", 50, -1))
                .build();
    }

    @Getter @Setter public String key;
    @Getter @Setter public int min;
    @Getter @Setter public int max;

    public static RentValueBlock matchPrice(String key) {
        RentValueBlock priceBlock = PRICE_BLOCK.get(key);
        if (priceBlock == null) {
            return ALL;
        }

        return priceBlock;
    }

    public static RentValueBlock matchArea(String key) {
        RentValueBlock areaBlock = AREA_BLOCK.get(key);
        if (areaBlock == null) {
            return ALL;
        }

        return areaBlock;
    }

}
