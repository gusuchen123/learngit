package com.imooc.web.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-15 20:47
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@ToString
public class SubwayStationDTO {
    private Long id;

    private Long subwayId;

    private String name;
}
