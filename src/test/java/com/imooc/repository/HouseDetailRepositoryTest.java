package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.entity.HouseDetail;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-13 22:54
 * Description:
 * Modified by:
 */
@Slf4j
public class HouseDetailRepositoryTest extends ApplicationTests {

    @Autowired
    private HouseDetailRepository detailRepository;

    @Test
    public void testFindOne() {
        HouseDetail houseDetail = detailRepository.findOne(21L);
        log.warn(JSON.toJSONString(houseDetail));
    }
}
