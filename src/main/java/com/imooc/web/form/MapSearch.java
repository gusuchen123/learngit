package com.imooc.web.form;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-23 14:05
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
public class MapSearch {
    private String cityEnName;

    /**
     * 地图缩放级别
     */
    private int level = 12;

    private String orderBy = "lastUpdateTime";
    private String orderDirection = "desc";

    /**
     * 左上角
     */
    private Double leftLongitude;
    private Double leftLatitude;

    /**
     * 右下角
     */
    private Double rightLongitude;
    private Double rightLatitude;

    private int start = 0;
    private int size = 5;

    public int getStart() {
        return start < 0 ? 0 : start;
    }

    public int getSize() {
        return size > 100 ? 100 : size;
    }
}
