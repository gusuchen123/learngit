package com.imooc.repository;

import com.imooc.entity.House;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author gusuchen
 * Created in 2018-01-13 17:37
 * Description: 房屋数据DAO
 * Modified by:
 */
public interface HouseRepository extends PagingAndSortingRepository<House, Long>, JpaSpecificationExecutor<House> {
    /**
     * 修改封面
     * @param id
     * @param cover
     */
    @Modifying
    @Query("update House as house set house.cover = :cover where house.id = :id")
    void updateCover(@Param(value = "id") Long id, @Param(value = "cover") String cover);

    /**
     * 更新房源状态
     * @param id
     * @param status
     */
    @Modifying
    @Query("update House as house set house.status = :status where house.id = :id")
    void updateStatus(@Param(value = "id") Long id, @Param(value = "status") int status);

    /**
     *
     * @param houseId
     */
    @Modifying
    @Query(value = "update House as house set house.watchTimes = house.watchTimes + 1 where house.id = :houseId")
    void updateWatchTime(@Param("houseId") Long houseId);
}
