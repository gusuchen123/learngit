package com.imooc.config;

import com.imooc.security.AuthFilter;
import com.imooc.security.AuthProvider;
import com.imooc.security.LoginAuthFailHandler;
import com.imooc.security.LoginUrlEntryAuthPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author gusuchen
 * Created in 2018-01-12 10:28
 * Description: 后台登录功能，角色权限控制
 * Modified by:
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * @author gusuchen
     * Created in 2018/1/12 10:30
     * Description: HTTP权限控制
     * Modified by: 
     * @param http
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class);

        // 资源访问权限
        http.authorizeRequests()
                .antMatchers("/admin/login").permitAll() // 管理员登录入口
                .antMatchers("/static/**").permitAll()   // 静态资源, 允许任何用户都可以访问
                .antMatchers("/user/login").permitAll()  // 普通用户登录入口
                .antMatchers("/admin/**").hasRole("ADMIN")  // 权限控制
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")     // 权限控制
                .antMatchers("/api/user/**").hasAnyRole("ADMIN", "USER") // 权限控制
                .and()
                .formLogin()
                .loginProcessingUrl("/login")  //配置角色登录处理入口
                .loginPage("/user/login")
                .failureHandler(loginAuthFailHandler()) // 登入验证失败处理器
                .and()
                .logout() // 登出处理入口
                .logoutUrl("/logout") // 登出处理url
                .logoutSuccessUrl("/logout/page") // 登出成功url页面
                .deleteCookies("JSESSIONID") // 删除sessionId
                .invalidateHttpSession(true) // 使会话失效
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(loginUrlEntryAuthPoint()) // 基于角色权限的登录入口控制器
                .accessDeniedPage("/403"); // 无权访问的提示页面
        // 关闭防御配置
        http.csrf().disable();
        http.headers().frameOptions().sameOrigin(); // 开启同源策略
    }

    /**
     * 自定义认证策略
     * @param auth
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider())
                .eraseCredentials(true); // 权限认证，擦出密码

        // 内存验证密码
//        auth.inMemoryAuthentication().withUser("admin").password("admin")
//                .roles("ADMIN").and();
    }

    @Bean
    public AuthProvider authProvider() {
        return new AuthProvider();
    }

    @Bean
    public LoginUrlEntryAuthPoint loginUrlEntryAuthPoint() {
        // 默认普通用户的登录页面
        return new LoginUrlEntryAuthPoint("/user/login");
    }

    @Bean
    public LoginAuthFailHandler loginAuthFailHandler() {
        // 注入LoginUrlEntryAuthPoint
        return new LoginAuthFailHandler(loginUrlEntryAuthPoint());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        AuthenticationManager authenticationManager = null;
        try {
            authenticationManager = super.authenticationManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return authenticationManager;
    }

    @Bean
    public AuthFilter authFilter() {
        AuthFilter authFilter = new AuthFilter();
        authFilter.setAuthenticationManager(authenticationManager());
        authFilter.setAuthenticationFailureHandler(loginAuthFailHandler());

        return authFilter;
    }

}
