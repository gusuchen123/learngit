package com.imooc.web.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-20 15:01
 * Description: 七牛云返回的结果集
 * Modified by:
 */
@Accessors(chain = true)
@Data
@ToString
public final class QiNiuPutRet {
    public String key;
    public String hash;
    public String bucket;
    public int width;
    public int height;
}
