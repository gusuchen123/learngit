package com.imooc.service.user;

import com.imooc.entity.User;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.UserDTO;

/**
 * @author gusuchen
 * Created in 2018-01-12 11:41
 * Description: 用户服务
 * Modified by:
 */
public interface IUserService {
    /**
     * 根据用户名查询用户信息
     * @param userName
     * @return
     */
    User findUserByName(String userName);

    /**
     * 根据adminId查询用户
     * @param adminId
     * @return
     */
    ServiceResult<UserDTO> findById(Long adminId);

    /**
     * 根据电话号码查询用户
     * @param telephone
     * @return
     */
    User findUserByTelephone(String telephone);

    /**
     * 根据电话号码注册用户
     * @param telephone
     * @return
     */
    User addUserByTelephone(String telephone);

    /**
     * 修改指定属性值
     * @param profile
     * @param value
     * @return
     */
    ServiceResult modifyUserProfile(String profile, String value);
}
