package com.imooc.repository;

import com.imooc.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:25
 * Description: 房屋标签映射关系数据DAO
 * Modified by:
 */
public interface HouseTagRepository extends CrudRepository<HouseTag, Long> {
    /**
     * 查询房源标签
     * @param houseId
     * @return
     */
    List<HouseTag> findAllByHouseId(Long houseId);

    /**
     * 获取房源标签
     * @param name
     * @param houseId
     * @return
     */
    HouseTag findByHouseIdAndName(Long houseId, String name);

    /**
     * 查询房源标签集
     * @param houseIds
     * @return
     */
    List<HouseTag> findAllByHouseIdIsIn(List<Long> houseIds);
}
