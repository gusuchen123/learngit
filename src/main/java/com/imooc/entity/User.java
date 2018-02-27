package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-11 13:46
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User implements UserDetails {
    // 用户唯一id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用户名
    private String name;

    // 电子邮箱
    private String email;

    // 电话号码
    @Column(name = "phone_number")
    private String phoneNumber;

    // 密码
    private String password;

    // 用户状态 0-正常 1-封禁
    private int status;

    // 用户账号创建时间
    @Column(name = "create_time")
    private Date createTime;

    // 上次登入时间
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    // 上次更新记录时间
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    // 用户头像
    private String avatar;

    // 透明，不被jpa验证，表中没有该字段
    @Transient
    private List<GrantedAuthority> grantedAuthorityList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.grantedAuthorityList;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
