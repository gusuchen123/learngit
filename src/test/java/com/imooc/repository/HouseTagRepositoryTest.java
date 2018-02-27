package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.entity.HouseTag;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-13 23:09
 * Description:
 * Modified by:
 */
@Slf4j
public class HouseTagRepositoryTest extends ApplicationTests {

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Test
    public void testFindOne() {
        HouseTag houseTag = houseTagRepository.findOne(15L);
        log.warn(JSON.toJSONString(houseTag));
    }
}
