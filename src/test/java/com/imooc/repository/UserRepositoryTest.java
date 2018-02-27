package com.imooc.repository;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-11 13:59
 * Description:
 * Modified by:
 */
@Slf4j
public class UserRepositoryTest extends ApplicationTests{

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindOne() {
        User user = userRepository.findOne(1L);
        Assert.assertEquals("wali", user.getName());
        log.warn(JSON.toJSONString(user));
    }

    @Test
    public void testFindByName() {
        User user = userRepository.findByName("wali");
        log.warn(JSON.toJSONString(user));
    }
}
