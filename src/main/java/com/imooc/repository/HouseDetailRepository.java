package com.imooc.repository;

import com.imooc.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:04
 * Description: 房屋详情DAO
 * Modified by:
 */
public interface HouseDetailRepository extends CrudRepository<HouseDetail, Long> {
    /**
     * 查询房源详细信息
     * @param houseId
     * @return
     */
    HouseDetail findByHouseId(Long houseId);

    /**
     * 查询房源详细信息集
     * @param houseIds
     * @return
     */
    List<HouseDetail> findAllByHouseIdIsIn(List<Long> houseIds);

}
