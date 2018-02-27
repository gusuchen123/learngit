package com.imooc.service.user;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.imooc.base.LoginUserUtil;
import com.imooc.entity.Role;
import com.imooc.entity.User;
import com.imooc.repository.RoleRepository;
import com.imooc.repository.UserRepository;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-12 11:42
 * Description: 用户
 * Modified by:
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    public static final String USER_PASSWORD = "password";

    public static final String USER_USERNAME = "name";

    public static final String USER_EMAIL = "email";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public User findUserByName(String userName) {
        User user = userRepository.findByName(userName);
        if (user == null) {
            return null;
        }

        List<Role> roles = roleRepository.findByUserId(user.getId());
        if (CollectionUtils.isEmpty(roles)) {
            throw new DisabledException("权限非法");
        }

        List<GrantedAuthority> authorityList = Lists.newArrayList();
        roles.forEach(role ->
                authorityList.add(
                        new SimpleGrantedAuthority("ROLE_" + role.getName())
                )
        );
        user.setGrantedAuthorityList(authorityList);
        return user;
    }

    @Override
    public ServiceResult<UserDTO> findById(Long adminId) {
        if (adminId == null || adminId < 1) {
            return ServiceResult.notFound();
        }

        User user = userRepository.findOne(adminId);
        if (user == null) {
            return ServiceResult.notFound();
        }
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return ServiceResult.ofResult(userDTO);
    }

    @Override
    public User findUserByTelephone(String telephone) {
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
            log.warn("Wrong telephone number");
            return null;
        }

        User user = userRepository.findByPhoneNumber(telephone);
        if (user == null) {
            return null;
        }

        List<Role> roles = roleRepository.findByUserId(user.getId());
        if (CollectionUtils.isEmpty(roles)) {
            throw new DisabledException("用户权限非法");
        }

        List<GrantedAuthority> authorities = Lists.newArrayList();
        roles.forEach(role -> {
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + user.getName())
            );
        });
        user.setGrantedAuthorityList(authorities);

        return user;
    }

    @Override
    @Transactional
    public User addUserByTelephone(String telephone) {
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
            log.warn("Wrong telephone number");
            return null;
        }

        User user = new User()
                .setPhoneNumber(telephone)
                .setName(telephone.substring(0, 3) + "****" + telephone.substring(7, telephone.length()))
                .setCreateTime(new Date())
                .setLastLoginTime(new Date())
                .setLastUpdateTime(new Date());
        user = userRepository.save(user);

        Role role = new Role()
                .setName("USER")
                .setUserId(user.getId());
        role = roleRepository.save(role);

        user.setGrantedAuthorityList(
                Lists.newArrayList(new SimpleGrantedAuthority("ROLE" + role.getName()))
        );

        return user;
    }

    @Override
    @Transactional
    public ServiceResult modifyUserProfile(String profile, String value) {
        if (Strings.isNullOrEmpty(profile) || Strings.isNullOrEmpty(value)) {
            return ServiceResult.ofMessage(false, "指定属性值为空");
        }

        if ("email".equals(profile) && !LoginUserUtil.checkEmail(value)) {
            return ServiceResult.ofMessage(false, "邮件格式不支持");
        }

        Long userId = LoginUserUtil.getLoginUserId();
        switch (profile) {
            case USER_USERNAME:
                userRepository.updateUsername(userId, value, new Date());
                break;
            case USER_EMAIL:
                userRepository.updateEmail(userId, value, new Date());
                break;
            case USER_PASSWORD:
                userRepository.updatePassword(userId, this.passwordEncoder.encodePassword(value, userId), new Date());
                break;
            default:
                return ServiceResult.ofMessage(false, "不支持的属性");
        }
        return ServiceResult.ofSuccess();
    }
}
