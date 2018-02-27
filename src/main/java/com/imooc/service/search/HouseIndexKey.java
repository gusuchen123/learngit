package com.imooc.service.search;

/**
 * @author gusuchen
 * Created in 2018-01-21 18:01
 * Description: 索引关键词统一定义
 * Modified by:
 */
public class HouseIndexKey {
    public static final String HOUSE_ID = "houseId";

    public static final String TITLE = "title";

    public static final String PRICE = "price";

    public static final String AREA = "area";

    public static final String DIRECTION = "direction";

    public static final String ROOM = "room";

    public static final String CREATE_TIME = "createTime";

    public static final String LAST_UPDATE_TIME = "lastUpdateTime";

    public static final String CITY_EN_NAME = "cityEnName";

    public static final String REGION_EN_NAME = "regionEnName";

    public static final String DISTANCE_TO_SUBWAY = "distanceToSubway";

    public static final String SUBWAY_LINE_NAME = "subwayLineName";

    public static final String SUBWAY_STATION_NAME = "subwayStationName";

    public static final String STREET = "street";

    public static final String DISTRICT = "district";

    public static final String DESCRIPTION = "description";

    public static final String LAYOUT_DESC = "layoutDesc";

    public static final String TRAFFIC = "traffic";

    public static final String ROUND_SERVICE = "roundService";

    public static final String RENT_WAY = "rentWay";

    public static final String TAGS = "tags";

    /**
     * 聚合特定小区的房源数量
     */
    public static final String AGG_DISTRICT = "agg_district";

    /**
     * 聚合特定城市区域数据
     */
    public static final String AGG_REGION = "agg_region";

    /**
     * 百度地图精确范围查询
     */
    public static final String GEO_LOCATION = "location";
}
