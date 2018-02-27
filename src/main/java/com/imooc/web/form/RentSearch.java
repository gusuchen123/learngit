package com.imooc.web.form;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-21 13:08
 * Description: 租房请求参数结构体
 * Modified by:
 */
@Accessors(chain = true)
@Data
@ToString
public class RentSearch {
    private String cityEnName;

    private String regionEnName;

    private String priceBlock;

    private String areaBlock;

    private int room;

    private int direction;

    private String keywords;

    private int rentWay = -1;

    private String orderBy = "lastUpdateTime";

    private String orderDirection = "desc";

    private int start = 0;

    private int size =5;

    public int getSize() {
        if (this.size < 1) {
            return 5;
        }
        if (this.size > 100) {
            return 100;
        }
        return this.size;
    }

    public int getRentWay() {
        if (rentWay > -2 && rentWay < 2) {
            return rentWay;
        }
        return -1;
    }
}
