package com.imooc.web.form;

import lombok.Data;
import lombok.ToString;

/**
 * @author gusuchen
 * Created in 2018-01-16 15:07
 * Description:
 * Modified by:
 */
@Data
@ToString
public class PhotoForm {

    private String path;

    private int width;

    private int height;
}
