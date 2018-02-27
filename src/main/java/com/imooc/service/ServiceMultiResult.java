package com.imooc.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author gusuchen
 * Created in 2018-01-15 19:23
 * Description: 多个结果集 服务接口通用结构
 * Modified by:
 */
@Data
@AllArgsConstructor
public class ServiceMultiResult<T> {

    private long total;

    // 数据结果集
    private List<T> result;

    // 返回数据集的数据量
    public int getResultSize() {
        if (CollectionUtils.isEmpty(this.result)) {
            return 0;
        }
        return this.result.size();
    }

    public static <T> ServiceMultiResult<T> of(long total, List<T> result) {
        return new ServiceMultiResult<>(total, result);
    }
}
