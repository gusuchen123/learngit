package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.entity.SubwayLine;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-13 23:13
 * Description:
 * Modified by:
 */
@Slf4j
public class SubwayRepositoryTest extends ApplicationTests {

    @Autowired
    private SubwayLineRepository subwayLineRepository;

    @Test
    public void testFindOne() {
        SubwayLine subwayLine = subwayLineRepository.findOne(1L);
        log.warn(JSON.toJSONString(subwayLine));
    }
}
