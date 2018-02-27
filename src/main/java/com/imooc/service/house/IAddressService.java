package com.imooc.service.house;

import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.entity.SupportAddress;
import com.imooc.service.search.BaiduMapLocation;
import com.imooc.web.dto.SubwayLineDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;

import java.util.Map;

/**
 * @author gusuchen
 * Created in 2018-01-15 17:39
 * Description: 地址服务接口
 * Modified by:
 */
public interface IAddressService {
    /**
     * @author gusuchen
     * Created in 2018/1/15 19:41
     * Description: 获取支持的城市列表
     * Modified by:
     */
    ServiceMultiResult<SupportAddressDTO> findAllCities();

    /**
     * @param
     * @author gusuchen
     * Created in 2018/1/15 20:10
     * Description: 获取对应城市支持区域列表
     * Modified by:
     */
    ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityEnName);

    /**
     * @param
     * @author gusuchen
     * Created in 2018/1/15 20:34
     * Description: 获取具体城市所支持的地铁线路
     * Modified by:
     */
    ServiceMultiResult<SubwayLineDTO> findAllSubwayLineByCityName(String cityEnName);

    /**
     * @param
     * @author gusuchen
     * Created in 2018/1/15 20:50
     * Description: 获取具体地铁线路所支持的地铁站点名字
     * Modified by:
     */
    ServiceMultiResult<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subwayId);

    /**
     * @param cityEnName
     * @param regionEnName
     * @author gusuchen
     * Created in 2018/1/16 16:11
     * Description: 根据英文简写获取具体区域的信息
     * Modified by:
     */
    Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    /**
     * @param subwayLineId
     * @author gusuchen
     * Created in 2018/1/18 16:53
     * Description: 获取地铁线路信息
     * Modified by:
     */
    ServiceResult<SubwayLineDTO> findSubwayLine(Long subwayLineId);

    /**
     * @param subwayStationId
     * @author gusuchen
     * Created in 2018/1/18 16:54
     * Description: 获取地铁站信息
     * Modified by:
     */
    ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId);

    /**
     * 根据城市英文简写获取城市详细信息
     *
     * @param cityEnName
     * @return
     */
    ServiceResult<SupportAddressDTO> findCity(String cityEnName);

    /** 根据城市 及 具体的地理位置 获取百度地图经纬度
     * @param cityCnName 城市中文名称
     * @param address 具体地理位置
     * @return
     */
    ServiceResult<BaiduMapLocation> getBaiduMapLocation(String cityCnName, String address);

    /**
     * 上传百度LBS数据
     * @param location
     * @param title
     * @param address
     * @param houseId
     * @param price
     * @param area
     * @return
     */
    ServiceResult uploadLbs(BaiduMapLocation location, String title, String address,
                            long houseId, int price, int area);

    /**
     * 移除百度LBS数据
     * @param houseId
     * @return
     */
    ServiceResult removeLbs(Long houseId);
}
