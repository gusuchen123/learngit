package com.imooc.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author gusuchen
 * Created in 2018-01-17 13:52
 * Description: DataTables 响应结构, Datatables是一款jquery表格插件，http://www.datatables.club/
 * Modified by:
 */
@Accessors(chain = true)
@Data
@AllArgsConstructor
public class ApiDataTableResponse extends ApiResponse {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;

    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);
    }

    public ApiDataTableResponse(Status status) {
        this(status.getCode(), status.getStandardMessage(), null);
    }
}
