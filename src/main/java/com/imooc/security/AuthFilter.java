package com.imooc.security;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.imooc.base.LoginUserUtil;
import com.imooc.entity.User;
import com.imooc.service.user.ISmsService;
import com.imooc.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gusuchen
 * Created in 2018-01-24 13:15
 * Description: 登入验证
 * Modified by:
 */
public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private IUserService userService;

    @Autowired
    private ISmsService smsService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = obtainUsername(request);
        if (!Strings.isNullOrEmpty(username)) {
            request.setAttribute("username", username);
            return super.attemptAuthentication(request, response);
        }

        String telephone = request.getParameter("telephone");
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
            throw new BadCredentialsException("Wrong telephone number");
        }

        User user = userService.findUserByTelephone(telephone);
        String inputCode = request.getParameter("smsCode");
        String sessionCode = smsService.getSmsCode(telephone);
        if (Objects.equal(inputCode, sessionCode)) {
            if (user == null) { // 如果用户第一次使用手机登入，则自动为该用户注册
                user = userService.addUserByTelephone(telephone);
            }
            return new UsernamePasswordAuthenticationToken(user, null, user.getGrantedAuthorityList());
        } else {
            throw new BadCredentialsException("smsCodeError");
        }

    }
}
