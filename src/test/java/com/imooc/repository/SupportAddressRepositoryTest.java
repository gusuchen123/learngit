package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.imooc.ApplicationTests;
import com.imooc.entity.SupportAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-13 23:23
 * Description:
 * Modified by:
 */
@Slf4j
public class SupportAddressRepositoryTest extends ApplicationTests {

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private Gson gson;

    @Test
    public void testFindOne() {
        SupportAddress supportAddress = supportAddressRepository.findOne(1L);
        log.warn(JSON.toJSONString(supportAddress));
    }

    @Test
    public void testFindByEnNameAndLevel() {
        SupportAddress city = supportAddressRepository.findByEnNameAndLevel("bj", SupportAddress.Level.CITY.getValue());
        log.warn(gson.toJson(city));
    }

    @Test
    public void testFindByEnNameAndBelongTo() {
        SupportAddress region = supportAddressRepository.findByEnNameAndBelongTo("dcq", "bj");
        log.warn(gson.toJson(region));
    }
}
