package com.imooc.web.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-16 15:18
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@ToString
public class HouseDetailDTO {
    /**
     * 描述
     */
    private String description;
    /**
     * 户型介绍
     */
    private String layoutDesc;
    /**
     * 交通出行
     */
    private String traffic;
    /**
     * 周边配套
     */
    private String roundService;
    /**
     * 租赁方式
     */
    private int rentWay;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 附近地铁线id
     */
    private Long subwayLineId;
    /**
     * 附近地铁线名称
     */
    private String subwayLineName;
    /**
     * 地铁站id
     */
    private Long subwayStationId;
    /**
     * 地铁站名
     */
    private String subwayStationName;
}
