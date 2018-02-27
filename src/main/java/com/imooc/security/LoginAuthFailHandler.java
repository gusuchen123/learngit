package com.imooc.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author gusuchen
 * Created in 2018-01-12 14:47
 * Description: 登入验证失败处理器
 * Modified by:
 */
public class LoginAuthFailHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginUrlEntryAuthPoint urlEntryAuthPoint;

    public LoginAuthFailHandler(LoginUrlEntryAuthPoint urlEntryAuthPoint) {
        this.urlEntryAuthPoint = urlEntryAuthPoint;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 获取登录失败的url路径
        String targetUrl = this.urlEntryAuthPoint.determineUrlToUseForThisRequest(request, response, exception);

        targetUrl += "?" + exception.getMessage();
        super.setDefaultFailureUrl(targetUrl);
        super.onAuthenticationFailure(request, response, exception);
    }
}
