package com.imooc.web.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author gusuchen
 * Created in 2018-01-16 15:19
 * Description: 预约看房实体类
 * Modified by:
 */
@Accessors(chain = true)
@Data
@ToString
public class HouseSubscribeDTO {
    private Long id;

    // 房源id
    private Long houseId;

    // 房源发布者id
    private Long adminId;

    // 用户id
    private Long userId;

    // 用户描述
    private String desc;

    // 预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成
    private int status;

    // 预约时间
    private Date orderTime;

    // 联系电话
    private String telephone;

    // 创建时间
    private Date createTime;

    // 最近更新时间
    private Date lastUpdateTime;
}
