package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.entity.SubwayStation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-13 23:16
 * Description:
 * Modified by:
 */
@Slf4j
public class SubwayStationRepositoryTest extends ApplicationTests {

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Test
    public void testFindOne() {
        SubwayStation subwayStation = subwayStationRepository.findOne(1L);
        log.warn(JSON.toJSONString(subwayStation));
    }
}
