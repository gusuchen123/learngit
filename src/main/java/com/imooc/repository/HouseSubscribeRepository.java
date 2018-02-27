package com.imooc.repository;

import com.imooc.entity.HouseSubscribe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * @author gusuchen
 * Created in 2018-01-25 10:46
 * Description: 房源描述 数据DAO
 * Modified by:
 */
public interface HouseSubscribeRepository extends PagingAndSortingRepository<HouseSubscribe, Long>,
        JpaSpecificationExecutor<HouseSubscribe> {
    /**
     * 根据 houseId loginUserId 查询房源预约
     * @param houseId
     * @param loginUserId
     */
    HouseSubscribe findByHouseIdAndUserId(Long houseId, Long loginUserId);

    /**
     * 根据 userId status 分页显示
     * @param userId
     * @param status
     * @param pageable
     */
    Page<HouseSubscribe> findAllByUserIdAndStatus(Long userId, int status, Pageable pageable);

    /**
     * 根据 userId status 分页显示
     * @param adminId
     * @param status
     * @param pageable
     * @return
     */
    Page<HouseSubscribe> findAllByAdminIdAndStatus(Long adminId, int status, Pageable pageable);

    /**
     * 根据 adminId houseId 查询预约信息
     * @param adminId
     * @param houseId
     * @return
     */
    HouseSubscribe findAllByAdminIdAndHouseId(Long adminId, Long houseId);

    /**
     * 更新预约状态
     * @param id
     * @param status
     * @param lastUpdateDate
     */
    @Modifying
    @Query(value = "update HouseSubscribe as subscribe set subscribe.status = :status, " +
            "subscribe.lastUpdateTime = :lastUpdateTime where subscribe.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") int status, @Param("lastUpdateTime") Date lastUpdateDate);
}
