package com.imooc.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author gusuchen
 * Created in 2018-01-11 16:50
 * Description: API结构设计_异常拦截器: 页面拦截器、api异常拦截信息
 *              Web错误，全局处理
 * Modified by:
 */
@Controller
public class AppErrorController implements ErrorController {
    // 全局的错误路径
    public static final String ERROR_PATH = "/error";

    private ErrorAttributes errorAttributes;

    // 保存 ErrorAttributes, 构造器注入
    @Autowired
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * @author gusuchen
     * Created in 2018/1/11 17:06
     * Description: Web页面错误处理，produces--访问类型
     * Modified by: 
     * @param request
     * @param response
     * @return String
     */
    @RequestMapping(value = ERROR_PATH, produces = {"text/html"})
    public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
        // 获取状态信息
        int status = response.getStatus();
        switch (status) {
            case 403:
                // 权限异常
                return "403";
            case 404:
                return "404";
            case 500:
                return "500";
        }
        return "index";
    }

    /**
     * @author gusuchen
     * Created in 2018/1/11 17:11
     * Description: 除web页面外的错误处理，例如JSON/XML等
     * Modified by: 
     * @param request
     * @return ApiResponse
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ApiResponse errorApiHandler(HttpServletRequest request) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);

        // 获取错误信息
        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(requestAttributes, false);

        // 获取request状态码
        int status = this.getStatus(request);

        return ApiResponse.ofMessage(status, String.valueOf(attr.getOrDefault("message", "error")));
    }

    /**
     * @author gusuchen
     * Created in 2018/1/11 17:23
     * Description: 获取 request 状态码
     * Modified by: 
     * @param request
     * @return int
     */
    private int getStatus(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (status != null) {
            return status;
        }

        return 500;
    }
}
