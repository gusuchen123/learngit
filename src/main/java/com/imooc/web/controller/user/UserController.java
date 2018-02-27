package com.imooc.web.controller.user;

import com.google.common.base.Strings;
import com.imooc.base.ApiResponse;
import com.imooc.base.HouseSubscribeStatus;
import com.imooc.base.LoginUserUtil;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IHouseService;
import com.imooc.service.user.IUserService;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.HouseSubscribeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author gusuchen
 * Created in 2018-01-12 14:12
 * Description: 用户控制中心
 * Modified by:
 */
@Controller
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IHouseService houseService;

    /**
     * 用户欢迎页面
     * @return
     */
    @GetMapping("/user/welcome")
    public String userWelcomePage() {
        return "user/welcome";
    }

    /**
     * 用户登录页面
     * @return
     */
    @GetMapping(value = "/user/login")
    public String userLoginPage() {
        return "user/login";
    }

    /**
     * 用户中心页面
     * @return
     */
    @GetMapping("/user/center")
    public String userCenterPage() {
        return "user/center";
    }

    /**
     * 修改用户信息
     * @param profile
     * @param value
     * @return
     */
    @PostMapping(value = "api/user/info")
    @ResponseBody
    public ApiResponse updateUserInfo(@RequestParam(name = "profile") String profile,
                                      @RequestParam(name = "value") String value) {
        if (Strings.isNullOrEmpty(profile) || Strings.isNullOrEmpty(value)) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "用户属性值不能为空");
        }

        if ("email".equals(profile) && !LoginUserUtil.checkEmail(value)) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "不支持的邮件格式");
        }

        ServiceResult result = userService.modifyUserProfile(profile, value);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }

    /**
     * 加入带看房清单
     * @param houseId
     * @return
     */
    @PostMapping("api/user/house/subscribe")
    @ResponseBody
    public ApiResponse subscribeHouse(@RequestParam(name = "house_id") Long houseId) {
        if (houseId == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "传递的参数不能为空");
        }

        ServiceResult<HouseSubscribeDTO> result = houseService.addSubscribeOrder(houseId);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofSuccess(ApiResponse.Status.SUCCESS);
    }

    /**
     * 根据待看房源状态显示
     * @param start
     * @param size
     * @param status
     * @return
     */
    @GetMapping("api/user/house/subscribe/list")
    @ResponseBody
    public ApiResponse subscribeList(@RequestParam(name = "start", defaultValue = "0") int start,
                                     @RequestParam(name = "size", defaultValue = "3") int size,
                                     @RequestParam(name = "status") int status) {
        ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> result = houseService.querySubscribeList(HouseSubscribeStatus.of(status), start, size);
        if (result.getTotal() == 0) {
            return ApiResponse.ofSuccess(result.getResult());
        }

        ApiResponse response = ApiResponse.ofSuccess(result.getResult());
        response.setMore(result.getTotal() > (start + size));
        return response;
    }

    /**
     * 预约看房时间
     * @param houseId
     * @param orderTime
     * @param desc
     * @param telephone
     * @return
     */
    @PostMapping("api/user/house/subscribe/date")
    @ResponseBody
    public ApiResponse subscribeDate(
            @RequestParam(name = "houseId") Long houseId,
            @RequestParam(name = "orderTime") @DateTimeFormat(pattern = "yyyy-MM-dd")Date orderTime,
            @RequestParam(name = "desc", required = false) String desc,
            @RequestParam(name = "telephone") String telephone) {
        if (houseId == null) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        if (orderTime == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "请选择预约时间");
        }

        if (Strings.isNullOrEmpty(telephone)) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "请填写预约电话");
        }

        if (!LoginUserUtil.checkTelephone(telephone)) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "电话号码格式不正确");
        }

        ServiceResult result = houseService.subscribe(houseId, orderTime, desc, telephone);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }

    /**
     * 取消看房预约
     * @param houseId
     * @return
     */
    @DeleteMapping("api/user/house/subscribe")
    @ResponseBody
    public ApiResponse cancelSubscribe(@RequestParam(name = "houseId") Long houseId) {
        if (houseId == null || houseId < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = houseService.cancelSubscribe(houseId);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }
}
