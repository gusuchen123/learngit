package com.imooc.repository;

import com.imooc.entity.SupportAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:35
 * Description:
 * Modified by:
 */
public interface SupportAddressRepository extends CrudRepository<SupportAddress, Long> {
    /**
     * @author gusuchen
     * Created in 2018/1/15 19:45
     * Description: 根据行政级别获取地址信息列表
     * Modified by: 
     * @param level 行政级别
     */
    List<SupportAddress> findAllByLevel(String level);
    /**
     * @author gusuchen
     * Created in 2018/1/15 20:15
     * Description: 根据 上一级行政单位名 行政级别 获取对应城市支持区域列表
     * Modified by: 
     * @param
     */
    List<SupportAddress> findAllByBelongToAndLevel(String belongTo, String level);

    /**
     * @author gusuchen
     * Created in 2018/1/16 16:19
     * Description: 获取地址信息
     * Modified by: 
     * @param cityEnName
     * @param level
     */
    SupportAddress findByEnNameAndLevel(String cityEnName, String level);

    /**
     * @author gusuchen
     * Created in 2018/1/16 16:22
     * Description: 
     * Modified by: 
     * @param cityEnName
     * @param belongTo
     */
    SupportAddress findByEnNameAndBelongTo(String cityEnName, String belongTo);
}
