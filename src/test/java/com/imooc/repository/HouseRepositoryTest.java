package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.entity.House;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:43
 * Description:
 * Modified by:
 */
@Slf4j
public class HouseRepositoryTest extends ApplicationTests {

    @Autowired
    private HouseRepository houseRepository;

    @Test
    public void testFindOne() {
        House house = houseRepository.findOne(15L);
        log.warn(JSON.toJSONString(house));
    }
}
