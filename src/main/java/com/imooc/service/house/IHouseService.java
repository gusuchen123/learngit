package com.imooc.service.house;

import com.imooc.base.HouseSubscribeStatus;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.HouseSubscribeDTO;
import com.imooc.web.form.DataTableSearchForm;
import com.imooc.web.form.HouseForm;
import com.imooc.web.form.MapSearch;
import com.imooc.web.form.RentSearch;
import org.springframework.data.util.Pair;

import java.util.Date;

/**
 * @author gusuchen
 * Created in 2018-01-16 10:13
 * Description: 房屋管理服务接口
 * Modified by:
 */
public interface IHouseService {
    /**
     * 新增房源接口
     * @param houseForm
     * @return
     */
    ServiceResult<HouseDTO> saveHouse(HouseForm houseForm);

    /**
     * 房源列表分页显示
     * @param searchBody
     * @return
     */
    ServiceMultiResult<HouseDTO> adminQuery(DataTableSearchForm searchBody);

    /**
     * 查询完整房源信息
     * @param houseId
     * @return
     */
    ServiceResult<HouseDTO> findCompleteOne(Long houseId);

    /**
     * 更新房源
     * @param houseForm
     * @return
     */
    ServiceResult updateCompleteHouse(HouseForm houseForm);

    /**
     * 删除房源图片
     * @param id
     * @return
     */
    ServiceResult removePhoto(Long id);

    /**
     * 更新封面
     * @param coverId
     * @param targetId
     * @return
     */
    ServiceResult updateCover(Long coverId, Long targetId);

    /**
     * 增加标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult addTag(Long houseId, String tag);

    /**
     * 移除标签
     * @param houseId
     * @param tag
     * @return
     */
    ServiceResult removeTag(Long houseId, String tag);

    /**
     * 更新房源状态
     * @param id
     * @param status
     * @return
     */
    ServiceResult updateStatus(Long id, int status);

    /**
     * 查询房源信息集
     * @param rentSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> query(RentSearch rentSearch);

    /**
     * 全地图查询
     * @param mapSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch);

    /**
     * 精确范围数据查询
     * @param mapSearch
     * @return
     */
    ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch);

    /**
     * 加入带看房清单
     * @param houseId
     * @return
     */
    ServiceResult<HouseSubscribeDTO> addSubscribeOrder(Long houseId);

    /**
     * 获取对应状态的预约列表
     * @param status
     * @param start
     * @param size
     * @return
     */
    ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatus status,
                                                                          int start, int size);

    /**
     * 预约看房时间
     * @param houseId
     * @param orderTime
     * @param desc
     * @param telephone
     * @return
     */
    ServiceResult subscribe(Long houseId, Date orderTime, String desc, String telephone);

    /**
     * 取消看房预约
     * @param houseId
     * @return
     */
    ServiceResult cancelSubscribe(Long houseId);

    /**
     * 管理员查询预约信息接口
     * @param start
     * @param size
     * @return
     */
    ServiceMultiResult<Pair<HouseDTO,HouseSubscribeDTO>> findSubscribeList(int start, int size);

    /**
     * 管理员完成预约接口
     * @param houseId
     * @return
     */
    ServiceResult finishSubscribe(Long houseId);
}
