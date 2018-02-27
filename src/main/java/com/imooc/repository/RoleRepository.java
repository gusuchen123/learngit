package com.imooc.repository;

import com.imooc.entity.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-12 13:32
 * Description: 用户角色数据DAO
 * Modified by:
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    List<Role> findByUserId(Long userId);
}
