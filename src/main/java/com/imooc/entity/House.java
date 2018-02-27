package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author gusuchen
 * Created in 2018-01-13 17:13
 * Description: 房屋信息实体类
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "house")
@NoArgsConstructor // 默认构造器 防止 jackson 序列化失败
public class House {
    // house唯一标识
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 房屋标题
    private String title;

    // 价格
    private int price;

    // 面积
    private int area;

    // 房间数量
    private int room;

    // 楼层
    private int floor;

    // 总楼层
    @Column(name = "total_floor")
    private int totalFloor;

    // 被看次数，默认是0
    @Column(name = "watch_times")
    private int watchTimes;

    // 建立年限
    @Column(name = "build_year")
    private int buildYear;

    // 房屋状态 0-未审核 1-审核通过 2-已出租 3-逻辑删除
    private int status;

    // 城市英文缩写，如 北京bj
    @Column(name = "city_en_name")
    private String cityEnName;

    // 区域英文缩写，如昌平区 cpq
    @Column(name = "region_en_name")
    private String regionEnName;

    // 房屋封面
    private String cover;

    // 房屋朝向
    private int direction;

    // 距地铁距离 默认-1 附近无地铁
    @Column(name = "distance_to_subway")
    private int distanceToSubway;

    // 客厅数量
    private int parlour;

    // 所在小区
    private String district;

    // 所属管理员id
    @Column(name = "admin_id")
    private Long adminId;

    // 卫生间数量
    private int bathroom;

    // 街道
    private String street;

    // 创建时间
    @Column(name = "create_time")
    private Date createTime;

    // 最近更新时间
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

}
