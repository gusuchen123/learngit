package com.imooc.repository;

import com.imooc.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * @author gusuchen
 * Created in 2018-01-11 13:58
 * Description: 用户信息数据DAO
 * Modified by:
 */
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * 查询用户
     * @param userName
     * @return
     */
    User findByName(String userName);

    /**
     * 根据 phoneNumber 查询用户
     * @param phoneNumber
     * @return
     */
    @Query(value = "select user from User as user where user.phoneNumber = :phoneNumber")
    User findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    /**
     * 更新用户名
     * @param id
     * @param username
     * @param lastUpdateTime
     */
    @Modifying
    @Query(value = "update User as user set user.name = :username, user.lastUpdateTime = :lastUpdateTime " +
            "where user.id = :id")
    void updateUsername(@Param("id") Long id, @Param("username") String username,
                        @Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 更新用户邮箱
     * @param id
     * @param email
     * @param lastUpdateTime
     */
    @Modifying
    @Query(value = "update User as user set user.email = :email, user.lastUpdateTime = :lastUpdateTime " +
            "where user.id = :id")
    void updateEmail(@Param("id") Long id, @Param("email") String email,
                     @Param("lastUpdateTime") Date lastUpdateTime);

    /**
     * 更新用户密码
     * @param id
     * @param password
     * @param lastUpdateTime
     */
    @Modifying
    @Query(value = "update User as user set user.password = :password, user.lastUpdateTime = :lastUpdateTime " +
            "where user.id = :id")
    void updatePassword(@Param("id") Long id, @Param("password") String password,
                        @Param("lastUpdateTime") Date lastUpdateTime);

}
