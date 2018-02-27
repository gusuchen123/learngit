package com.imooc.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gusuchen
 * Created in 2018-01-25 13:22
 * Description: 预约房源状态码
 * Modified by:
 */
@AllArgsConstructor
public enum HouseSubscribeStatus {
    NO_SUBSCRIBE(0),  // 未预约
    IN_ORDER_LIST(1), // 已加入待看房清单
    IN_ORDER_TIME(2), // 已预约看房时间
    FINISH(3);        // 已完成预约

    @Getter private int value;

    public static HouseSubscribeStatus of(int value) {
        for (HouseSubscribeStatus subscribeStatus : HouseSubscribeStatus.values()) {
            if (subscribeStatus.getValue() == value) {
                return subscribeStatus;
            }
        }
        return NO_SUBSCRIBE;
    }

}
