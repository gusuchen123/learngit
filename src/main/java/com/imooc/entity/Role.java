package com.imooc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author gusuchen
 * Created in 2018-01-12 13:30
 * Description: 用户角色表
 * Modified by:
 */
@Accessors(chain = true)
@Data
@Entity
@Table(name = "role")
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 用户id
    @Column(name = "user_id")
    private Long userId;

    // 用户角色名
    private String name;
}
