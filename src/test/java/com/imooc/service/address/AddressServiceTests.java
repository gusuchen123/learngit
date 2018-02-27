package com.imooc.service.address;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.search.BaiduMapLocation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-23 19:18
 * Description:
 * Modified by:
 */
@Slf4j
public class AddressServiceTests extends ApplicationTests {

    @Autowired
    private IAddressService addressService;

    @Test
    public void testGetBaiduMapLocation() {
        String city = "北京";
        String address = "北京市昌平区巩华家园1号楼2单元";
        ServiceResult<BaiduMapLocation> serviceResult = addressService.getBaiduMapLocation(city, address);

        Assert.assertTrue(serviceResult.isSuccess());

        Assert.assertTrue(serviceResult.getResult().getLongitude() > 0 );
        Assert.assertTrue(serviceResult.getResult().getLatitude() > 0 );

        log.warn(JSON.toJSONString(serviceResult.getResult()));
    }
}
