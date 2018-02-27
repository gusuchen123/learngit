package com.imooc.web.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-15 20:33
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@ToString
public class SubwayLineDTO {
    private Long id;

    private String name;

    private String cityEnName;
}
