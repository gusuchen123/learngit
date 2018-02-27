package com.imooc.web.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-16 15:21
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@ToString
public class UserDTO {
    private Long id;

    private String name;
    
    private String email;

    private String phoneNumber;

    private String avatar;
    
    private String lastLoginTime;
}
