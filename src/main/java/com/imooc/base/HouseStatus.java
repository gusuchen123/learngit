package com.imooc.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gusuchen
 * Created in 2018-01-17 14:06
 * Description: 房源状态枚举类
 * Modified by:
 */
@AllArgsConstructor
public enum  HouseStatus {
    NOT_AUDITED(0), // 未审核
    PASSES(1),      // 审核通过
    RENTED(2),      // 已出租
    DELETED(3);     // 逻辑删除

    @Getter private int value;

    public static HouseStatus of(int value) {
        for (HouseStatus status : HouseStatus.values()) {
            if (status.getValue() == value) {
                return status;
            }
        }
        return NOT_AUDITED;
    }

}
