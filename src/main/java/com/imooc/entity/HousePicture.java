package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:06
 * Description: 房屋图片信息
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "house_picture")
@NoArgsConstructor
public class HousePicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 所属房屋id
    @Column(name = "house_id")
    private Long houseId;

    // 图片路径
    @Column(name = "cdn_prefix")
    private String cdnPrefix;

    // 宽
    private int width;

    // 高
    private int height;

    // 所属房屋位置
    private String location;

    // 文件名
    private String path;
}
