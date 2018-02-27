package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.entity.HousePicture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-13 23:04
 * Description:
 * Modified by:
 */
@Slf4j
public class HousePictureRepositoryTest extends ApplicationTests {

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Test
    public void testFindOne() {
        HousePicture housePicture = housePictureRepository.findOne(68L);
        log.warn(JSON.toJSONString(housePicture));
    }
}
