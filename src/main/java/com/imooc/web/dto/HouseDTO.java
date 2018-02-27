package com.imooc.web.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-16 15:16
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
public class HouseDTO implements Serializable {
    /**
     * house唯一标识
     */
    private Long id;
    /**
     * 房屋标题
     */
    private String title;
    /**
     * 城市英文缩写，如 北京bj
     */
    private String cityEnName;
    /**
     * 区域英文缩写，如昌平区 cpq
     */
    private String regionEnName;
    /**
     * 所在小区
     */
    private String district;
    /**
     *街道
     */
    private String street;
    /**
     * 房屋朝向: 1-东，2-南，3-西，4-北，5-东南，6-东北，7-西南，8-西北
     */
    private int direction;
    /**
     * 房间数量
     */
    private int room;
    /**
     * 客厅数量
     */
    private int parlour;
    /**
     * 楼层
     */
    private int floor;
    /**
     * 总楼层
     */
    private int totalFloor;
    /**
     * 价格
     */
    private int price;
    /**
     * 面积
     */
    private int area;
    /**
     * 建立年限
     */
    private int buildYear;
    /**
     * 被看次数，默认是0
     */
    private int watchTimes;
    /**
     * 房屋状态 0-未审核 1-审核通过 2-已出租 3-逻辑删除
     */
    private int status;
    /**
     * 房屋封面
     */
    private String cover;
    /**
     * 距地铁距离 默认-1 附近无地铁
     */
    private int distanceToSubway;
    /**
     * 所属管理员id
     */
    private Long adminId;
    /**
     * 卫生间数量
     */
    private int bathroom;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最近更新时间
     */
    private Date lastUpdateTime;
    /**
     * 房源详细信息
     */
    private HouseDetailDTO houseDetail;
    /**
     * 房源标签
     */
    private List<String> tags;
    /**
     * 房源图片
     */
    private List<HousePictureDTO> pictures;
    /**
     * 预约看房状态 1-加入待看清单 2-已预约看房时间 3-看房完成
     */
    private int subscribeStatus;

    public List<String> getTags() {
        if (CollectionUtils.isEmpty(this.tags)) {
            tags = Lists.newArrayList();
        }
        return tags;
    }
}
