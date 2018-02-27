package com.imooc.repository;

import com.imooc.entity.HousePicture;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:11
 * Description: 房屋图片信息数据DAO
 * Modified by:
 */
public interface HousePictureRepository extends CrudRepository<HousePicture, Long> {
    /**
     * 查询房源信息照片
     * @param houseId
     * @return
     */
    List<HousePicture> findAllByHouseId(Long houseId);

}
