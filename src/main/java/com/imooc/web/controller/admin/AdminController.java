package com.imooc.web.controller.admin;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.imooc.base.*;
import com.imooc.entity.SupportAddress;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.house.IHouseService;
import com.imooc.service.house.IQiNiuService;
import com.imooc.service.user.IUserService;
import com.imooc.web.dto.*;
import com.imooc.web.form.DataTableSearchForm;
import com.imooc.web.form.HouseForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author gusuchen
 * Created in 2018-01-11 20:09
 * Description: 管理员控制中心
 * Modified by:
 */
@Controller
public class AdminController {

    private final String uploadPath = "E:\\tmp\\imooc\\xunwu-project\\tmp\\";

    @Autowired
    private IQiNiuService qiNiuService;

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private IUserService userService;

    @Autowired
    private Gson gson;

    /**
     * 后台管理中心
     * @return
     */
    @GetMapping("/admin/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    /**
     * 欢迎页面
     * @return
     */
    @GetMapping("/admin/welcome")
    public String adminWelcomePage() {
        return "admin/welcome";
    }

    /**
     * 管理员登录页面
     * @return
     */
    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    /**
     * 房源列表页
     * @return
     */
    @GetMapping("admin/house/list")
    public String listHousePage() {
        return "admin/house-list";
    }

    /**
     * 房源列表分页显示接口
     * @param searchBody
     * @return
     */
    @PostMapping("admin/houses")
    @ResponseBody
    public ApiDataTableResponse listHouse(@ModelAttribute DataTableSearchForm searchBody) {
        ServiceMultiResult<HouseDTO> result = houseService.adminQuery(searchBody);
        ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);;
        response.setData(result.getResult());
        response.setRecordsTotal(result.getTotal());
        response.setRecordsFiltered(result.getTotal());

        // Datatables 要求的回显字段
        response.setDraw(searchBody.getDraw());
        return response;
    }

    /**
     * 新增房源功能页
     * @return
     */
    @GetMapping("admin/add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    /**
     * 新增房源功能接口
     * @param houseForm
     * @param bindingResult
     * @return
     */
    @PostMapping("admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute(name = "form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        // controller 完成数据的校验，service 具体的业务逻辑，dao 数据的生成 查询 修改 转换
        ApiResponse response = checkFormParams(bindingResult);
        if (response.getCode() == HttpStatus.BAD_REQUEST.value()) {
            return response;
        }

        // 校验图片
        if (CollectionUtils.isEmpty(houseForm.getPhotos())) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须上传房屋图片");
        }

        // 校验城市和区域
        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService
                .findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult<HouseDTO> result = houseService.saveHouse(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(result.getResult());
        }
        return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
    }

    /**
     * 本地上传图片，consumes(消费类型)--MediaType.MULTIPART_FORM_DATA_VALUE
     * @param file
     * @return
     */
    @PostMapping(value = "/admin/local/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse localUploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        String fileName = file.getOriginalFilename();
        File target = new File(uploadPath + fileName);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            e.printStackTrace();
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }
        return ApiResponse.ofSuccess(null);
    }

    /**
     * 文件上传至七牛云, consumes = "multipart/form-data"
     * @param file
     * @return
     */
    @PostMapping(value = "admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam(name = "file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        try {
            InputStream stream = file.getInputStream();
            Response response = qiNiuService.uploadFile(stream);
            if (response.isOK()) {
                QiNiuPutRet ret = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ApiResponse.ofSuccess(ret);
            }
            return ApiResponse.ofMessage(response.statusCode, response.getInfo());

        } catch (QiniuException e) {
            Response response = e.response;
            try {
                return ApiResponse.ofMessage(response.statusCode, response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
                return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
            }

        } catch (IOException e) {
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * 房源信息编辑页面
     * @param houseId
     * @param model
     * @return
     */
    @GetMapping("admin/edit/house")
    public String editHousePage(@RequestParam(name = "id") Long houseId, Model model) {

        if (houseId == null || houseId < 1) {
            return "404";
        }

        ServiceResult<HouseDTO> result = houseService.findCompleteOne(houseId);
        if (!result.isSuccess()) {
            return "404";
        }

        HouseDTO houseDTO = result.getResult();
        model.addAttribute("house", houseDTO);

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService
                .findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());
        model.addAttribute("city", addressMap.get(SupportAddress.Level.CITY));
        model.addAttribute("region", addressMap.get(SupportAddress.Level.REGION));

        HouseDetailDTO detailDTO = houseDTO.getHouseDetail();
        ServiceResult<SubwayLineDTO> subwayLineResult = addressService.findSubwayLine(detailDTO.getSubwayLineId());
        if (subwayLineResult.isSuccess()) {
            model.addAttribute("subway", subwayLineResult.getResult());
        }

        ServiceResult<SubwayStationDTO> subwayStationResult = addressService
                .findSubwayStation(detailDTO.getSubwayStationId());
        if (subwayStationResult.isSuccess()) {
            model.addAttribute("station", subwayStationResult.getResult());
        }

        return "admin/house-edit";
    }

    /**
     * 房源信息编辑接口
     * @param houseForm
     * @param bindingResult
     * @return
     */
    @PostMapping("admin/edit/house")
    @ResponseBody
    public ApiResponse editHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm,
                                 BindingResult bindingResult) {
        // 校验表单参数
        ApiResponse response = checkFormParams(bindingResult);
        if (response.getCode() == HttpStatus.BAD_REQUEST.value()) {
            return response;
        }

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService
                .findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult result = houseService.updateCompleteHouse(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }

        ApiResponse apiResponse = ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        apiResponse.setMessage(result.getMessage());
        return apiResponse;
    }

    /**
     * 移除图片接口
     * @param id
     * @return
     */
    @DeleteMapping("admin/house/photo")
    @ResponseBody
    public ApiResponse removeHousePhoto(@RequestParam(name = "id") Long id) {
        if (id == null || id < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = houseService.removePhoto(id);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }

    /**
     * 修改封面接口
     * @param coverId 图片id
     * @param targetId 房源id
     * @return
     */
    @PostMapping("admin/house/cover")
    @ResponseBody
    public ApiResponse updateCover(@RequestParam(name = "cover_id") Long coverId,
                                   @RequestParam(name = "target_id") Long targetId) {
        if (coverId == null || coverId < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        if (targetId == null || targetId < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = this.houseService.updateCover(coverId, targetId);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }

    /**
     * 增加标签接口
     * @param houseId
     * @param tag
     * @return
     */
    @PostMapping("admin/house/tag")
    @ResponseBody
    public ApiResponse addHouseTag(@RequestParam(name = "house_id") Long houseId,
                                   @RequestParam(name = "tag") String tag) {
        if (houseId == null || houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = houseService.addTag(houseId, tag);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }

    /**
     * 移除标签接口
     * @param houseId
     * @param tag
     * @return
     */
    @DeleteMapping("admin/house/tag")
    @ResponseBody
    public ApiResponse removeHouseTag(@RequestParam(name = "house_id") Long houseId,
                                      @RequestParam(name = "tag") String tag) {
        if (houseId == null || houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = this.houseService.removeTag(houseId, tag);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }

    /**
     * 审核接口
     * @param id
     * @param operation
     * @return
     */
    @PutMapping("admin/house/operate/{id}/{operation}")
    @ResponseBody
    public ApiResponse operateHouse(@PathVariable(name = "id") Long id,
                                    @PathVariable(name = "operation") int operation) {
        if (id == null || id <= 0) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult result;

        switch (operation) {
            case HouseOperation.PASS:
                result = this.houseService.updateStatus(id, HouseStatus.PASSES.getValue());
                break;
            case HouseOperation.PULL_OUT:
                result = this.houseService.updateStatus(id, HouseStatus.NOT_AUDITED.getValue());
                break;
            case HouseOperation.DELETE:
                result = this.houseService.updateStatus(id, HouseStatus.DELETED.getValue());
                break;
            case HouseOperation.RENT:
                result = this.houseService.updateStatus(id, HouseStatus.RENTED.getValue());
                break;
            default:
                return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        if (result.isSuccess()) {
            return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
        }

        return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
    }

    /**
     * 跳转到预约看房管理页面
     * @return
     */
    @GetMapping("admin/house/subscribe")
    public String houseSubscribePage() {
        return "admin/subscribe";
    }

    /**
     * 管理员查询预约信息接口
     * @param draw
     * @param start
     * @param size
     * @return
     */
    @GetMapping("admin/house/subscribe/list")
    @ResponseBody
    public ApiResponse subscribeList(@RequestParam(name = "draw") int draw,
                                     @RequestParam(name = "start", defaultValue = "0") int start,
                                     @RequestParam(name = "length", defaultValue = "5") int size) {
        ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> result = houseService.findSubscribeList(start, size);

        ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
        response.setDraw(draw)
                .setRecordsFiltered(result.getTotal())
                .setRecordsTotal(result.getTotal());

        return response;
    }

    /**
     * 管理员查看预约看房用户信息
     * @param userId
     * @return
     */
    @GetMapping("admin/user/{userId}")
    @ResponseBody
    public ApiResponse getUserInfo(@PathVariable(name = "userId") Long userId) {
        if (userId == null || userId < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult<UserDTO> result = userService.findById(userId);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }

    /**
     * 管理员完成预约接口
     * @param houseId
     * @return
     */
    @PostMapping("admin/finish/subscribe")
    @ResponseBody
    public ApiResponse finishSubscribe(@RequestParam(name = "houseId") Long houseId) {
        if (houseId == null || houseId < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult result = houseService.finishSubscribe(houseId);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
    }

    /**
     * 校验表单参数
     * @param bindingResult
     * @return
     */
    private ApiResponse checkFormParams(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return ApiResponse.ofSuccess(null);
    }

}
