package com.imooc.service.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-23 16:39
 * Description: 百度地图位置信息
 * Modified by:
 */
@Accessors(chain = true)
@Data
public class BaiduMapLocation {
    /**
     * 经度, es中定义为lon
     */
    @JsonProperty("lon")
    private double longitude;

    /**
     * 纬度, es中定义为lat
     */
    @JsonProperty("lat")
    private double latitude;
}
