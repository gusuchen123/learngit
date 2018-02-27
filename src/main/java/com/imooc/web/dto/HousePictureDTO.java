package com.imooc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author gusuchen
 * Created in 2018-01-16 15:19
 * Description:
 * Modified by:
 */
@Accessors(chain = true)
@Data
@ToString
public class HousePictureDTO {
    private Long id;

    // 所属房屋id
    @JsonProperty(value = "house_id")
    private Long houseId;

    // 图片路径
    @JsonProperty(value = "cdn_prefix")
    private String cdnPrefix;

    // 宽
    private int width;

    // 高
    private int height;

    // 文件名
    private String path;
}
