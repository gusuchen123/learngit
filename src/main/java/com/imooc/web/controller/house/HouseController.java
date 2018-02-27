package com.imooc.web.controller.house;

import com.google.common.base.Strings;
import com.imooc.base.ApiResponse;
import com.imooc.base.RentValueBlock;
import com.imooc.entity.SupportAddress;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.house.IHouseService;
import com.imooc.service.search.HouseBucketDTO;
import com.imooc.service.search.ISearchService;
import com.imooc.service.user.IUserService;
import com.imooc.web.dto.*;
import com.imooc.web.form.MapSearch;
import com.imooc.web.form.RentSearch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author gusuchen
 * Created in 2018-01-13 23:35
 * Description: 房屋控制类
 * Modified by:
 */
@Controller
public class HouseController {
    @Autowired
    private IAddressService addressService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ISearchService searchService;

    /**
     * 自动补全接口
     * @param prefix
     * @return
     */
    @GetMapping("rent/house/autocomplete")
    @ResponseBody
    public ApiResponse autoComplete(@RequestParam(name = "prefix") String prefix) {
        if (Strings.isNullOrEmpty(prefix)) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }

        ServiceResult<List<String>> result = this.searchService.suggest(prefix);
        if (!result.isSuccess()) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }

        return ApiResponse.ofSuccess(result.getResult());
    }

    /**
     * 获取支持的城市列表
     * @return
     */
    @GetMapping("/address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities() {
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if (CollectionUtils.isEmpty(result.getResult())) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    /**
     * 获取对应城市支持区域列表
     * @param cityEnName 城市英文名称
     * @return
     */
    @GetMapping("/address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(name = "city_name") String cityEnName) {
        ServiceMultiResult<SupportAddressDTO> addressResult = addressService.findAllRegionsByCityName(cityEnName);
        if (addressResult.getResult() == null || addressResult.getTotal() < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(addressResult.getResult());
    }

    /**
     * 获取具体城市所支持的地铁线路
     * @param cityEnName
     * @return
     */
    @GetMapping("/address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubwayLine(@RequestParam(name = "city_name") String cityEnName) {
        ServiceMultiResult<SubwayLineDTO> subwayLineDTOResult = addressService.findAllSubwayLineByCityName(cityEnName);
        if (subwayLineDTOResult.getResult() == null || subwayLineDTOResult.getResultSize() < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(subwayLineDTOResult.getResult());
    }

    /**
     * 获取对应地铁线路所支持的地铁站点
     * @param subwayId
     * @return
     */
    @GetMapping("/address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportSubwayStation(@RequestParam(name = "subway_id") Long subwayId) {
        ServiceMultiResult<SubwayStationDTO> stationDTOResult = addressService.findAllSubwayStationBySubwayId(subwayId);
        if (stationDTOResult.getResult() == null || stationDTOResult.getResultSize() < 1) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }
        return ApiResponse.ofSuccess(stationDTOResult.getResult());
    }

    /**
     * 房源信息浏览
     * @param rentSearch
     * @param model
     * @param session
     * @param attributes
     * @return
     */
    @GetMapping("rent/house")
    public String rentHousePage(@ModelAttribute RentSearch rentSearch,
                                Model model, HttpSession session,
                                RedirectAttributes attributes) {
        // 校验城市
        if (Strings.isNullOrEmpty(rentSearch.getCityEnName())) {
            String cityEnNameInSession = (String) session.getAttribute("cityEnName");
            if (cityEnNameInSession == null) {
                attributes.addAttribute("msg", "must_choose_city");
                return "redirect:/index";
            }
            rentSearch.setCityEnName(cityEnNameInSession);
        }
        session.setAttribute("cityEnName", rentSearch.getCityEnName());

        // 校验城市
        ServiceResult<SupportAddressDTO> cityResult = addressService.findCity(rentSearch.getCityEnName());
        if (!cityResult.isSuccess()) {
            attributes.addAttribute("msg", "must_choose_city");
            return "redirect:/index";
        }

        model.addAttribute("currentCity", cityResult.getResult());


        // 校验区域
        ServiceMultiResult<SupportAddressDTO> addressResults = addressService
                .findAllRegionsByCityName(rentSearch.getCityEnName());
        if (addressResults == null || addressResults.getTotal() < 1) {
            attributes.addAttribute("msg", "must_choose_city");
            return "redirect:/index";
        }

        ServiceMultiResult<HouseDTO> houseResults = houseService.query(rentSearch);

        model.addAttribute("total", houseResults.getTotal());
        model.addAttribute("houses", houseResults.getResult());

        if (rentSearch.getRegionEnName() == null) {
            rentSearch.setRegionEnName("*");
        }

        model.addAttribute("searchBody", rentSearch);
        model.addAttribute("regions", addressResults.getResult());

        model.addAttribute("priceBlocks", RentValueBlock.PRICE_BLOCK);
        model.addAttribute("areaBlocks", RentValueBlock.AREA_BLOCK);

        model.addAttribute("currentPriceBlock", RentValueBlock.matchPrice(rentSearch.getPriceBlock()));
        model.addAttribute("currentAreaBlock", RentValueBlock.matchArea(rentSearch.getAreaBlock()));

        return "rent-list";
    }

    /**
     * 房源信息详情页
     * @param houseId
     * @param model
     * @return
     */
    @GetMapping("rent/house/show/{id}")
    public String showHouse(@PathVariable(name = "id") Long houseId, Model model) {
        if (houseId == null || houseId < 1) {
            return "404";
        }

        ServiceResult<HouseDTO> houseResult = houseService.findCompleteOne(houseId);
        if (!houseResult.isSuccess()) {
            return "404";
        }
        HouseDTO houseDTO = houseResult.getResult();

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService
                .findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());

        SupportAddressDTO city = addressMap.get(SupportAddress.Level.CITY);
        SupportAddressDTO region = addressMap.get(SupportAddress.Level.REGION);

        model.addAttribute("city", city);
        model.addAttribute("region", region);

        ServiceResult<UserDTO> userResult = userService.findById(houseDTO.getAdminId());
        if (!userResult.isSuccess()) {
            return "404";
        }

        model.addAttribute("agent", userResult.getResult());
        model.addAttribute("house", houseDTO);

        ServiceResult<Long> aggResult = searchService.aggregateDistrictHouse(houseDTO.getCityEnName(), houseDTO.getRegionEnName(), houseDTO.getDistrict());
        model.addAttribute("houseCountInDistrict", aggResult.getResult());

        return "house-detail";
    }

    /**
     * 地图找房页面
     * @param cityEnName
     * @param session
     * @param attributes
     * @param model
     * @return
     */
    @GetMapping("rent/house/map")
    public String rentHouseMapPage(@RequestParam(name = "cityEnName") String cityEnName,
                                   HttpSession session,
                                   RedirectAttributes attributes,
                                   Model model) {
        // 校验城市
        if (Strings.isNullOrEmpty(cityEnName)) {
            String cityEnNameInSession = (String) session.getAttribute("ciyEnName");
            if (Strings.isNullOrEmpty(cityEnNameInSession)) {
                attributes.addAttribute("msg", "must_choose_city");
                return "redirect:/index";
            }
            cityEnName = cityEnNameInSession;
        }

        ServiceResult<SupportAddressDTO> city = addressService.findCity(cityEnName);
        if (!city.isSuccess()) {
            attributes.addAttribute("msg", "must_choose_city");
            return "redirect:/index";
        }
        session.setAttribute("cityEnName", cityEnName);
        model.addAttribute("city", city.getResult());

        ServiceMultiResult<SupportAddressDTO> regions = addressService.findAllRegionsByCityName(cityEnName);
        if (CollectionUtils.isEmpty(regions.getResult())) {
            model.addAttribute("regions", 0);
        }
        model.addAttribute("regions", regions.getResult());

        ServiceMultiResult<HouseBucketDTO> result = searchService.mapAggregateByCityEnName(cityEnName);
        model.addAttribute("aggData", result.getResult());
        model.addAttribute("total", result.getTotal());

        return "rent-map";
    }

    @GetMapping("rent/house/map/houses")
    @ResponseBody
    public ApiResponse rentMapHouses(@ModelAttribute MapSearch mapSearch) {
        if (mapSearch.getCityEnName() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须选择城市");
        }

        ServiceMultiResult<HouseDTO> results;
        if (mapSearch.getLevel() < 13) {
            // 全地图查询
            results = houseService.wholeMapQuery(mapSearch);
        } else {
            // 小地图查询必须要传递地图边界参数
            results = houseService.boundMapQuery(mapSearch);
        }

        if (CollectionUtils.isEmpty(results.getResult())) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_FOUND);
        }

        ApiResponse response = ApiResponse.ofSuccess(results.getResult());
        response.setMore(results.getTotal() > (mapSearch.getStart() + mapSearch.getStart()));

        return response;
    }

}
