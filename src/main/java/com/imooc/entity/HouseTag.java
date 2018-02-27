package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:20
 * Description: 房屋标签映射关系表
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "house_tag")
@NoArgsConstructor
public class HouseTag {
    // 房屋标签唯一标识id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 房屋id
    @Column(name = "house_id")
    private Long houseId;

    // 房屋标签名
    private String name;

    public HouseTag(Long houseId, String name) {
        this.houseId = houseId;
        this.name = name;
    }
}
