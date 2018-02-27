package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:36
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "support_address")
@NoArgsConstructor
public class SupportAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 上一级行政单位名
    @Column(name = "belong_to")
    private String belongTo;

    // 行政单位英文名缩写
    @Column(name = "en_name")
    private String enName;

    // 行政单位中文名
    @Column(name = "cn_name")
    private String cnName;

    // 行政级别 市-city 地区-region
    private String level;

    // 百度地图经度
    @Column(name = "baidu_map_lng")
    private double baiduMapLng;

    // 百度地图纬度
    @Column(name = "baidu_map_lat")
    private double baiduMapLat;

    // 行政级别枚举类
    public enum Level {
        CITY("city"),
        REGION("region");

        private String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Level of(String value) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(value)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("非法的行政级别");
        }
    }
}
