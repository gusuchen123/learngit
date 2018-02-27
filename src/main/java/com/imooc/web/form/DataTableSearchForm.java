package com.imooc.web.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author gusuchen
 * Created in 2018-01-17 14:02
 * Description:
 * Modified by:
 */
@Data
public class DataTableSearchForm {
    /**
     * Datatables要求回显字段，判断是否是同一个请求
     */
    private int draw;

    /**
     * Datatables规定分页字段
     * start: 从第几条记录开始分页
     * length: 每页的记录数
     */
    private int start;
    private int length;

    /**
     * 房源的状态:
     */
    private Integer status;

    /**
     * createTimeMin createTimeMax 表示创建时间的范围
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeMin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeMax;

    /**
     * 城市中文名称
     */
    private String city;
    /**
     * 房源标题
     */
    private String title;
    /**
     * 排序字段
     */
    private String orderBy;
    /**
     * 升序 or 降序
     */
    private String direction;
}
