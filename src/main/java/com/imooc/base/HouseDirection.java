package com.imooc.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gusuchen
 * Created in 2018-01-20 18:50
 * Description: 房屋朝向枚举类
 * Modified by:
 */
@AllArgsConstructor
public enum HouseDirection {
    EAST(1, "朝东"),
    SOUTH(2, "朝南"),
    WEST(3, "朝西"),
    NORTH(4, "朝北"),
    EAST_NORTH(5, "东南"),
    EAST_SOUTH(6, "东北"),
    WEST_NORTH(7, "西南"),
    WEST_SOUTH(8, "西北");

    @Getter private int key;
    @Getter private String value;
}
