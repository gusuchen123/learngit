package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:13
 * Description: 预约看房信息表
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "house_subscribe")
@NoArgsConstructor
public class HouseSubscribe {
    /**
     * 唯一标识id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 房源id
     */
    @Column(name = "house_id")
    private Long houseId;

    /**
     * 房源发布者id
     */
    @Column(name = "admin_id")
    private Long adminId;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 预约描述
     * 踩坑: `desc` 为MySQL的保留字段 需要加转义
     */
    @Column(name = "`desc`")
    private String desc;

    /**
     * 预约状态 1-加入待看清单 2-已预约看房时间 3-看房完成
     */
    private int status;

    /**
     * 预约时间
     */
    @Column(name = "order_time")
    private Date orderTime;

    /**
     * 联系电话
     */
    private String telephone;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 最近更新时间
     */
    @Column(name = "last_update_time")
    private Date lastUpdateTime;
}
