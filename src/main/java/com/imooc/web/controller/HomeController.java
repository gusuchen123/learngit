package com.imooc.web.controller;

import com.google.common.base.Strings;
import com.imooc.base.ApiResponse;
import com.imooc.base.LoginUserUtil;
import com.imooc.service.ServiceResult;
import com.imooc.service.user.ISmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gusuchen
 * Created in 2018-01-11 14:56
 * Description:
 * Modified by:
 */
@Controller
public class HomeController {

    @Autowired
    private ISmsService smsService;

    /**
     * index
     * @return
     */
    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index";
    }

    /**
     * NOT FOUND
     * @return
     */
    @GetMapping("/404")
    public String notFoundPage() {
        return "404";
    }

    /**
     * Access Error 权限异常访问页面
     * @return
     */
    @GetMapping("/403")
    public String accessError() {
        return "403";
    }

    /**
     * INTERNAL SERVER ERROR
     * @return
     */
    @GetMapping("/500")
    public String internalError() {
        return "500";
    }

    /**
     * 通用登出页面
     * @return
     */
    @GetMapping("/logout/page")
    public String logout() {
        return "logout";
    }

    @GetMapping("sms/code")
    @ResponseBody
    public ApiResponse smsCode(@RequestParam(name = "telephone") String telephone) {
        if (Strings.isNullOrEmpty(telephone) || !LoginUserUtil.checkTelephone(telephone)) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "请输入正确的手机号");
        }

        ServiceResult<String> result = smsService.sendSms(telephone);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess("");
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }
}
