package com.imooc.web.form;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-16 10:58
 * Description: 新增房源表单验证
 * Modified by:
 */
@Data
@ToString
public class HouseForm {
    private Long id;

    @NotNull(message = "必须填写大标题")
    @Size(min = 1, max = 30, message = "标题长度必须在1~30之间")
    private String title;

    @NotNull(message = "必须选中一个城市")
    @Size(min = 1, max = 10, message = "非法的城市")
    private String cityEnName;

    @NotNull(message = "必须选中一个地区")
    @Size(min = 1, message = "非法的地区")
    private String regionEnName;

    @NotNull(message = "必须填写街道")
    @Size(min = 1, message = "非法的街道")
    private String street;

    @NotNull(message = "必须填写小区")
    @Size(min = 1, message = "非法的小区")
    private String district;

    @NotNull(message = "必须填写详细地址")
    @Size(min = 1, max = 30, message = "详细地址长度必须在1~30之间")
    private String detailAddress;

    @NotNull(message = "必须填写卧室的数量")
    @Min(value = 0, message = "非法的卧室数量")
    private Integer room;

    @NotNull(message = "必须填写客厅数量")
    @Min(value = 0, message = "非法的客厅数量")
    private Integer parlour;

    @NotNull(message = "必须选择一个房屋朝向")
    @Min(value = 1, message = "非法的房屋朝向")
    @Max(value = 8, message = "非法的房屋朝向")
    private Integer direction;

    @NotNull(message = "必须填写建筑起始时间")
    @Min(value = 1900, message = "非法的建筑起始时间")
    private Integer buildYear;

    @NotNull(message = "必须填写所属楼层")
    private Integer floor;

    @NotNull(message = "必须填写总楼层")
    private Integer totalFloor;

    @NotNull(message = "必须填写房屋面积")
    @Min(value = 1, message = "非法的房屋面积")
    private Integer area;

    @NotNull(message = "必须填写租赁价格")
    @Min(value = 1, message = "非法的租赁价格")
    private Integer price;

    @NotNull(message = "必须选择一个租赁方式")
    @Min(value = 0, message = "非法的租赁方式")
    @Max(value = 1, message = "非法的租赁方式")
    private Integer rentWay;

    // 地铁线路
    private Long subwayLineId;

    // 地铁站
    private Long subwayStationId;

    // 房屋到地铁站的距离
    private int distanceToSubway = -1;

    @NotNull(message = "必须填写户型介绍")
    private String layoutDesc;

    @NotNull(message = "必须填写周边配套服务")
    private String roundService;

    @NotNull(message = "必须填写交通出行")
    private String traffic;

    @NotNull(message = "必须填写房屋描述")
    private String description;

    // 房屋封面
    private String cover;

    // 房屋标签
    private List<String> tags;

    // 房源照片
    private List<PhotoForm> photos;
}
