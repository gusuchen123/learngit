package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-13 23:19
 * Description:
 * Modified by:
 */
@Slf4j
public class RoleRepositoryTest extends ApplicationTests {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindOne() {
        Role role = roleRepository.findOne(1L);
        log.warn(JSON.toJSONString(role));
    }
}
