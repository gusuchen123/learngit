package com.imooc.security;

import com.imooc.entity.User;
import com.imooc.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author gusuchen
 * Created in 2018-01-12 11:38
 * Description: 自定义认证实现
 * Modified by:
 */
public class AuthProvider implements AuthenticationProvider {
    @Autowired
    private IUserService userService;

    // md5解密器
    private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取输入用户名
        String username = authentication.getName();
        // 获取输入密码
        String inputPassword = (String) authentication.getCredentials();

        // dao层查找用户
        User user = userService.findUserByName(username);
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("authError");
        }

        // 用户密码使用MD5加密
        if (passwordEncoder.isPasswordValid(user.getPassword(), inputPassword, user.getId())) {
            return new UsernamePasswordAuthenticationToken(user, null, user.getGrantedAuthorityList());
        }

        throw new BadCredentialsException("authError");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        // 支持所有的认证类
        return true;
    }
}
