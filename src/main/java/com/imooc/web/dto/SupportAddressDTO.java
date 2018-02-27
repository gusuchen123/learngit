package com.imooc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-15 17:39
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
public class SupportAddressDTO {
    // 地址唯一标识id
    private Long id;

    // 上一级的行政单位名称
    @JsonProperty(value = "belong_to")
    private String belongTo;

    // 行政单位英文名称
    @JsonProperty(value = "en_name")
    private String enName;

    // 行政单位中文名称
    @JsonProperty(value = "cn_name")
    private String cnName;

    // 行政级别 市-city 地区-region
    private String level;

    // 百度地图经度
    private double baiduMapLng;

    // 百度地图纬度
    private double baiduMapLat;
}
