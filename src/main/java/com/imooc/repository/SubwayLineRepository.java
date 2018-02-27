package com.imooc.repository;

import com.imooc.base.HouseSubscribeStatus;
import com.imooc.entity.SubwayLine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:30
 * Description: 地铁线路数据DAO
 * Modified by:
 */
public interface SubwayLineRepository extends CrudRepository<SubwayLine, Long> {
    /**
     * @author gusuchen
     * Created in 2018/1/15 20:39
     * Description: 获取对应城市的地铁线路
     * Modified by: 
     * @param cityEnName
     */
    List<SubwayLine> findSubwayLineDOSByCityEnName(String cityEnName);

}
