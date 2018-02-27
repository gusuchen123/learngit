package com.imooc.service.search;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-21 18:01
 * Description: 索引结构模板
 * Modified by:
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
public class HouseIndexTemplate {
    private Long houseId;

    private String title;

    private int price;

    private int area;

    private int direction;

    private int room;

    private Date createTime;

    private Date lastUpdateTime;

    private String cityEnName;
    
    private String regionEnName;

    private int distanceToSubway;

    private String subwayLineName;
    
    private String subwayStationName;

    private String street;

    private String district;

    private String description;
    
    private String layoutDesc;
    
    private String traffic;
    
    private String roundService;

    private int rentWay;

    private List<String> tags;

    private List<HouseSuggest> suggest;

    private BaiduMapLocation location;

}
