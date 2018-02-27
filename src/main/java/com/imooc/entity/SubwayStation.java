package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:31
 * Description: 地铁站信息表
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "subway_station")
@NoArgsConstructor
public class SubwayStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 所属地铁线id
    @Column(name = "subway_id")
    private Long subwayId;

    // 站点名称
    private String name;
}
