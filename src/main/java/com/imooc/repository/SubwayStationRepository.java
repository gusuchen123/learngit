package com.imooc.repository;

import com.imooc.entity.SubwayStation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:34
 * Description: 地铁站信息数据DAO
 * Modified by:
 */
public interface SubwayStationRepository extends CrudRepository<SubwayStation, Long> {
    /**
     * @author gusuchen
     * Created in 2018/1/15 20:53
     * Description: 根据具体地体线获取地铁站点名称
     * Modified by: 
     * @param subwayId
     */
    List<SubwayStation> findAllBySubwayId(Long subwayId);
}
