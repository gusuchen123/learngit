package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:28
 * Description: 地铁线路表
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "subway")
@NoArgsConstructor
public class SubwayLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 线路名
    private String name;

    // 所属城市英文名缩写
    @Column(name = "city_en_name")
    private String cityEnName;
}
