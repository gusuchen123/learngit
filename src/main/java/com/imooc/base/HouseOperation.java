package com.imooc.base;

/**
 * @author gusuchen
 * Created in 2018-01-18 21:07
 * Description: 房屋操作状态常量定义
 * Modified by:
 */
public class HouseOperation {
    /**
     * 审核通过
     */
    public static final int PASS = 1;

    /**
     * 下架, 重新审核
     */
    public static final int PULL_OUT = 2;

    /**
     * 逻辑删除
     */
    public static final int DELETE = 3;

    /**
     * 出租
     */
    public static final int RENT = 4;
}
