package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author gusuchen
 * Created in 2018-01-13 17:39
 * Description: 房屋详情实体类
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "house_detail")
@NoArgsConstructor
public class HouseDetail {
    // 房屋详情唯一标识id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 对应house的id
    @Column(name = "house_id")
    private Long houseId;

    // 描述
    private String description;

    // 户型介绍
    @Column(name = "layout_desc")
    private String layoutDesc;

    // 交通出行
    private String traffic;

    // 周边配套
    @Column(name = "round_service")
    private String roundService;

    // 租赁方式: 0-整租，1-合租
    @Column(name = "rent_way")
    private int rentWay;

    // 详细地址
    @Column(name = "address")
    private String address;

    // 附近地铁线id
    @Column(name = "subway_line_id")
    private Long subwayLineId;

    // 附近地铁线名称
    @Column(name = "subway_line_name")
    private String subwayLineName;

    // 地铁站id
    @Column(name = "subway_station_id")
    private Long subwayStationId;

    // 地铁站名
    @Column(name = "subway_station_name")
    private String subwayStationName;
}
