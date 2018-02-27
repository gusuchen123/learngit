package com.imooc.service.house;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.imooc.base.*;
import com.imooc.entity.*;
import com.imooc.repository.*;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.search.ISearchService;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.HouseDetailDTO;
import com.imooc.web.dto.HousePictureDTO;
import com.imooc.web.dto.HouseSubscribeDTO;
import com.imooc.web.form.*;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gusuchen
 * Created in 2018-01-16 10:14
 * Description:
 * Modified by:
 */
@Service
public class HouseServiceImpl implements IHouseService {
    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private SubwayLineRepository subwayLineRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private HouseSubscribeRepository houseSubscribeRepository;

    @Autowired
    private IQiNiuService qiNiuService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ISearchService searchService;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;

    @Override
    @Transactional
    public ServiceResult<HouseDTO> saveHouse(HouseForm houseForm) {
        // 校验地铁信息
        HouseDetail detail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidationResult = wrapperDetailInfo(detail, houseForm);
        if (!subwayValidationResult.isSuccess()) {
            return subwayValidationResult;
        }

        House house = new House();
        modelMapper.map(houseForm, house);

        Date date = new Date();
        house.setCreateTime(date);
        house.setLastUpdateTime(date);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        house = houseRepository.save(house);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);

        detail.setHouseId(house.getId());
        detail = houseDetailRepository.save(detail);

        HouseDetailDTO houseDetailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        houseDTO.setHouseDetail(houseDetailDTO);

        List<HousePicture> pictures = generatePictures(houseForm, house.getId());
        if (CollectionUtils.isEmpty(pictures)) {
            return new ServiceResult<>(false, "必须上传图片");
        }
        List<HousePictureDTO> pictureDTOS = Lists.newArrayList();
        pictures.forEach(picture -> pictureDTOS.add(modelMapper.map(picture, HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix + houseDTO.getCover());

        List<String> tags = houseForm.getTags();
        if (!CollectionUtils.isEmpty(tags)) {
            List<HouseTag> houseTags = Lists.newArrayList();
            for (String tag : tags) {
                houseTags.add(new HouseTag(house.getId(), tag));
            }
            houseTagRepository.save(houseTags);
            houseDTO.setTags(tags);
        }

        return ServiceResult.ofResult(houseDTO);
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DataTableSearchForm searchBody) {
        List<HouseDTO> houseDTOS = Lists.newArrayList();

        // 排序方式
        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());

        // 页码
        int page = searchBody.getStart() / searchBody.getLength();

        Specification<House> specification = (root, query, cb) -> {
            // 只允许查看本用户的房源
            Predicate predicate = cb.equal(root.get("adminId"), LoginUserUtil.getLoginUserId());

            // 不允许查看已被逻辑删除的房源
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));

            if (searchBody.getCity() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), searchBody.getCity()));
            }

            if (searchBody.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), searchBody.getStatus()));
            }

            if (searchBody.getCreateTimeMin() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMin()));
            }

            if (searchBody.getCreateTimeMax() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMax()));
            }

            if (searchBody.getTitle() != null) {
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + searchBody.getTitle() + "%"));
            }

            return predicate;
        };
        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);

        Page<House> houses = houseRepository.findAll(specification, pageable);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);
        });

        return new ServiceMultiResult<>(houses.getTotalElements(), houseDTOS);
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Long houseId) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
        List<HousePicture> pictures = housePictureRepository.findAllByHouseId(houseId);

        HouseDetailDTO detailDTO = modelMapper.map(detail, HouseDetailDTO.class);
        List<HousePictureDTO> pictureDTOS = Lists.newArrayList();
        pictures.forEach(picture ->
                pictureDTOS.add(modelMapper.map(picture, HousePictureDTO.class))
        );

        List<HouseTag> tagList = houseTagRepository.findAllByHouseId(houseId);
        List<String> tags = Lists.newArrayList();
        tagList.forEach(tag ->
                tags.add(tag.getName())
        );

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        houseDTO.setHouseDetail(detailDTO);
        houseDTO.setTags(tags);
        houseDTO.setPictures(pictureDTOS);

        if (LoginUserUtil.getLoginUserId() > 0) { // 已登入用户
            HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndUserId(house.getId(),
                    LoginUserUtil.getLoginUserId());
            if (subscribe != null) {
                houseDTO.setSubscribeStatus(subscribe.getStatus());
            }
        }

        return ServiceResult.ofResult(houseDTO);
    }

    @Override
    @Transactional
    public ServiceResult updateCompleteHouse(HouseForm houseForm) {
        House house = this.houseRepository.findOne(houseForm.getId());
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = this.houseDetailRepository.findByHouseId(house.getId());
        if (detail == null) {
            return ServiceResult.notFound();
        }

        ServiceResult wrapperResult = wrapperDetailInfo(detail, houseForm);
        if (!wrapperResult.isSuccess()) {
            return wrapperResult;
        }

        houseDetailRepository.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        housePictureRepository.save(pictures);

        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }

        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(new Date());
        houseRepository.save(house);

        // TODO: 更新房源时，更新 es
        if (house.getStatus() == HouseStatus.PASSES.getValue()) {
            searchService.index(house.getId());
        }

        return ServiceResult.ofSuccess();
    }

    @Override
    @Transactional
    public ServiceResult removePhoto(Long id) {
        HousePicture picture = housePictureRepository.findOne(id);
        if (picture == null) {
            return ServiceResult.notFound();
        }

        try {
            Response response = this.qiNiuService.deleteFile(picture.getPath());
            if (response.isOK()) {
                housePictureRepository.delete(id);
                return ServiceResult.ofSuccess();
            }
            return ServiceResult.ofMessage(false, response.error);

        } catch (QiniuException e) {
            e.printStackTrace();
            return ServiceResult.ofMessage(false, e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResult updateCover(Long coverId, Long targetId) {
        // 校验图片是否存在
        HousePicture cover = housePictureRepository.findOne(coverId);
        if (cover == null) {
            return ServiceResult.notFound();
        }

        this.houseRepository.updateCover(targetId, cover.getPath());

        return ServiceResult.ofSuccess();
    }

    @Override
    @Transactional
    public ServiceResult addTag(Long houseId, String tag) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseTag houseTag = houseTagRepository.findByHouseIdAndName(houseId, tag);
        if (houseTag != null) {
            return ServiceResult.ofMessage(false, "标签已存在");
        }

        houseTagRepository.save(new HouseTag(houseId, tag));

        return ServiceResult.ofSuccess();
    }

    @Override
    @Transactional
    public ServiceResult removeTag(Long houseId, String tag) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseTag houseTag = houseTagRepository.findByHouseIdAndName(houseId, tag);
        if (houseTag == null) {
            return ServiceResult.ofMessage(false, "标签不存在");
        }

        houseTagRepository.delete(houseTag.getId());

        return ServiceResult.ofSuccess();
    }

    @Override
    @Transactional
    public ServiceResult updateStatus(Long id, int status) {
        House house = houseRepository.findOne(id);
        if (house == null) {
            return ServiceResult.notFound();
        }

        if (house.getStatus() == status) {
            return ServiceResult.ofMessage(false, "状态没有发生变化");
        }

        if (house.getStatus() == HouseStatus.RENTED.getValue()) {
            return ServiceResult.ofMessage(false, "已出租的房源不允许发生变化");
        }

        if (house.getStatus() == HouseStatus.DELETED.getValue()) {
            return ServiceResult.ofMessage(false, "已删除的房源不允许操作");
        }

        this.houseRepository.updateStatus(id, status);

        // todo: 上架更新索引  其它情况删除索引
        if (status == HouseStatus.PASSES.getValue()) {
            searchService.index(id);
        } else {
            searchService.remove(id);
        }

        return ServiceResult.ofSuccess();
    }

    @Override
    public ServiceMultiResult<HouseDTO> query(RentSearch rentSearch) {
        if (!Strings.isNullOrEmpty(rentSearch.getKeywords())) {
            ServiceMultiResult<Long> searchResult = searchService.query(rentSearch);
            if (searchResult.getTotal() == 0) {
                return ServiceMultiResult.of(0, Lists.newArrayList());
            }

            return ServiceMultiResult.of(searchResult.getTotal(), wrapperHouseResult(searchResult.getResult()));
        }

        return simpleQuery(rentSearch);
    }

    @Override
    public ServiceMultiResult<HouseDTO> wholeMapQuery(MapSearch mapSearch) {
        ServiceMultiResult<Long> multiResult = searchService
                .mapQuery(
                        mapSearch.getCityEnName(),
                        mapSearch.getOrderBy(),
                        mapSearch.getOrderDirection(),
                        mapSearch.getStart(),
                        mapSearch.getSize()
                );
        if (multiResult.getTotal() == 0) {
            return ServiceMultiResult.of(0, Lists.newArrayList());
        }

        List<HouseDTO> houses = wrapperHouseResult(multiResult.getResult());
        return ServiceMultiResult.of(multiResult.getTotal(), houses);
    }

    @Override
    public ServiceMultiResult<HouseDTO> boundMapQuery(MapSearch mapSearch) {
        ServiceMultiResult<Long> multiResult = searchService.mapQuery(mapSearch);
        if (multiResult.getTotal() == 0) {
            return ServiceMultiResult.of(0, Lists.newArrayList());
        }

        List<HouseDTO> houses = wrapperHouseResult(multiResult.getResult());
        return ServiceMultiResult.of(multiResult.getTotal(), houses);
    }

    @Override
    @Transactional
    public ServiceResult<HouseSubscribeDTO> addSubscribeOrder(Long houseId) {
        if (houseId == null) {
            return ServiceResult.ofMessage(false, "传递的参数为空");
        }

        Long userId = LoginUserUtil.getLoginUserId();
        if (userId == null || userId < 1) {
            return ServiceResult.ofMessage(false, "该用户不存在");
        }

        HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe != null) {
            return ServiceResult.ofMessage(false, "已加入预约看房清单");
        }

        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.ofMessage(false, "查无此房");
        }

        subscribe = new HouseSubscribe()
                .setCreateTime(new Date())
                .setLastUpdateTime(new Date())
                .setHouseId(houseId)
                .setUserId(userId)
                .setAdminId(house.getAdminId())
                .setStatus(HouseSubscribeStatus.IN_ORDER_LIST.getValue());
        houseSubscribeRepository.save(subscribe);

        return ServiceResult.ofSuccess();
    }

    @Override
    public ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> querySubscribeList(HouseSubscribeStatus status,
                                                                                 int start, int size) {
        if (status == null) {
            return ServiceMultiResult.of(0, Lists.newArrayList());
        }

        Pageable pageable = new PageRequest(start / size, size, Sort.Direction.DESC, "createTime");

        Long userId = LoginUserUtil.getLoginUserId();
        if (userId == null || userId < 1) {
            return ServiceMultiResult.of(0, Lists.newArrayList());
        }

        Page<HouseSubscribe> page = houseSubscribeRepository.findAllByUserIdAndStatus(userId,
                status.getValue(), pageable);

        return wrapper(page);
    }

    @Override
    @Transactional
    public ServiceResult subscribe(Long houseId, Date orderTime, String desc, String telephone) {
        Long userId = LoginUserUtil.getLoginUserId();
        if (userId == null) {
            return ServiceResult.ofMessage(false, "无法预约");
        }

        HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe == null) {
            return ServiceResult.ofMessage(false, "无预约记录");
        }

        if (HouseSubscribeStatus.IN_ORDER_LIST.getValue() != subscribe.getStatus()) {
            return ServiceResult.ofMessage(false, "无法预约");
        }

        subscribe.setLastUpdateTime(new Date())
                .setDesc(desc)
                .setOrderTime(orderTime)
                .setTelephone(telephone);
        houseSubscribeRepository.save(subscribe);

        return ServiceResult.ofSuccess();
    }

    @Override
    @Transactional
    public ServiceResult cancelSubscribe(Long houseId) {
        if (houseId == null || houseId < 1) {
            return ServiceResult.notFound();
        }

        Long userId = LoginUserUtil.getLoginUserId();
        if (userId == null || userId < 1) {
            return ServiceResult.notFound();
        }

        HouseSubscribe subscribe = houseSubscribeRepository.findByHouseIdAndUserId(houseId, userId);
        if (subscribe == null) {
            return ServiceResult.ofMessage(false, "无此预约记录");
        }

        // 当心：物理删除，非逻辑删除
        houseSubscribeRepository.delete(subscribe.getId());

        return ServiceResult.ofSuccess();
    }

    @Override
    public ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> findSubscribeList(int start, int size) {
        Long adminId = LoginUserUtil.getLoginUserId();
        if (adminId == null || adminId < 1) {
            return ServiceMultiResult.of(0, Lists.newArrayList());
        }

        Pageable pageable = new PageRequest(start / size, size, Sort.Direction.DESC, "createTime");

        Page<HouseSubscribe> page = houseSubscribeRepository.findAllByAdminIdAndStatus(adminId, HouseSubscribeStatus.IN_ORDER_TIME.getValue(), pageable);

        return wrapper(page);
    }

    @Override
    @Transactional
    public ServiceResult finishSubscribe(Long houseId) {
        Long adminId = LoginUserUtil.getLoginUserId();
        if (adminId == null || adminId < 1) {
            return ServiceResult.notFound();
        }

        HouseSubscribe subscribe = houseSubscribeRepository.findAllByAdminIdAndHouseId(adminId, houseId);
        if (subscribe == null) {
            return ServiceResult.ofMessage(false, "无此预约记录");
        }

        if (HouseSubscribeStatus.IN_ORDER_TIME.getValue() != subscribe.getStatus()) {
            return ServiceResult.ofMessage(false, "无法结束预约");
        }

        houseSubscribeRepository.updateStatus(subscribe.getId(), HouseSubscribeStatus.FINISH.getValue(), new Date());

        houseRepository.updateWatchTime(subscribe.getHouseId());

        return ServiceResult.ofSuccess();
    }

    private ServiceMultiResult<Pair<HouseDTO, HouseSubscribeDTO>> wrapper(Page<HouseSubscribe> page) {
        List<Pair<HouseDTO, HouseSubscribeDTO>> result = Lists.newArrayList();

        // 无预约看房记录
        if (page.getSize() < 1) {
            return ServiceMultiResult.of(page.getTotalElements(), result);
        }

        List<HouseSubscribeDTO> subscribeDTOS = Lists.newArrayList();
        List<Long> houseIds = Lists.newArrayList();
        page.forEach(houseSubscribe -> {
            subscribeDTOS.add(modelMapper.map(houseSubscribe, HouseSubscribeDTO.class));
            houseIds.add(houseSubscribe.getHouseId());
        });

        Map<Long, HouseDTO> idToHouseMap = Maps.newHashMap();
        Iterable<House> houses = houseRepository.findAll(houseIds);
        houses.forEach(house ->
            idToHouseMap.put(house.getId(), modelMapper.map(house, HouseDTO.class))
        );

        subscribeDTOS.forEach(houseSubscribeDTO -> {
            Pair<HouseDTO, HouseSubscribeDTO> pair = Pair.of(idToHouseMap.get(houseSubscribeDTO.getHouseId()), houseSubscribeDTO);
            result.add(pair);
        });

        return ServiceMultiResult.of(page.getTotalElements(), result);
    }

    private ServiceMultiResult<HouseDTO> simpleQuery(RentSearch rentSearch) {
        Sort sort = HouseSort.generateSort(rentSearch.getOrderBy(), rentSearch.getOrderDirection());
        int page = rentSearch.getStart() / rentSearch.getSize();

        Pageable pageable = new PageRequest(page, rentSearch.getSize(), sort);

        Specification<House> specification = ((root, query, cb) -> {
            Predicate predicate = cb.equal(root.get("status"), HouseStatus.PASSES.getValue());

            predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), rentSearch.getCityEnName()));

            if (HouseSort.DISTANCE_TO_SUBWAY.equals(rentSearch.getOrderBy())) {
                predicate = cb.and(predicate, cb.gt(root.get(HouseSort.DISTANCE_TO_SUBWAY), -1));
            }
            return predicate;
        });

        Page<House> houses = houseRepository.findAll(specification, pageable);
        List<HouseDTO> houseDTOS = Lists.newArrayList();

        List<Long> houseIds = Lists.newArrayList();
        Map<Long, HouseDTO> idToHouseDTO = Maps.newHashMap();
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            houseDTOS.add(houseDTO);

            houseIds.add(house.getId());
            idToHouseDTO.put(house.getId(), houseDTO);
        });

        wrapperHouseList(houseIds, idToHouseDTO);

        return ServiceMultiResult.of(houses.getTotalElements(), houseDTOS);
    }

    /**
     *
     * @param houseIds
     * @return
     */
    private List<HouseDTO> wrapperHouseResult(List<Long> houseIds) {
        List<HouseDTO> result = Lists.newArrayList();
        Map<Long, HouseDTO> idToHouseDTO = Maps.newHashMap();

        Iterable<House> houses = houseRepository.findAll(houseIds);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + house.getCover());
            idToHouseDTO.put(houseDTO.getId(), houseDTO);
        });

        this.wrapperHouseList(houseIds, idToHouseDTO);

        // 矫正顺序
        houseIds.forEach(houseId -> {
            result.add(idToHouseDTO.get(houseId));
        });

        return result;
    }

    /**
     * 渲染详细信息 及 标签
     * @param houseIds
     * @param idToHouseDTO
     */
    private void wrapperHouseList(List<Long> houseIds, Map<Long, HouseDTO> idToHouseDTO) {
        List<HouseDetail> detailList = houseDetailRepository.findAllByHouseIdIsIn(houseIds);
        detailList.forEach(houseDetail -> {
            HouseDTO houseDTO = idToHouseDTO.get(houseDetail.getHouseId());
            HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);
            houseDTO.setHouseDetail(houseDetailDTO);
        });

        List<HouseTag> tagList = houseTagRepository.findAllByHouseIdIsIn(houseIds);
        tagList.forEach(houseTag -> {
            HouseDTO houseDTO = idToHouseDTO.get(houseTag.getHouseId());
            houseDTO.getTags().add(houseTag.getName());
        });
    }

    /**
     * @author gusuchen
     * Created in 2018/1/16 17:35
     * Description: 图片详细信息对象填充
     * Modified by: 
     * @param
     */
    private List<HousePicture> generatePictures(HouseForm houseForm, Long houseId) {
        List<HousePicture> pictures = Lists.newArrayList();
        // 校验图片信息
        if (CollectionUtils.isEmpty(houseForm.getPhotos())) {
            return pictures;
        }

        for (PhotoForm photo : houseForm.getPhotos()) {
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setHeight(photo.getHeight());
            picture.setWidth(photo.getWidth());
            picture.setPath(photo.getPath());
            picture.setCdnPrefix(this.cdnPrefix);
            pictures.add(picture);
        }
        return pictures;
    }
    /**
     * @author gusuchen
     * Created in 2018/1/16 17:16
     * Description: 房源详细信息对象填充
     * Modified by: 
     * @param houseDetail
     * @param houseForm
     */
    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {
        // 校验地铁线路信息
        SubwayLine subwayLine = subwayLineRepository.findOne(houseForm.getSubwayLineId());
        if (subwayLine == null) {
            return new ServiceResult<>(false, "Not Valid Subway Line !");
        }

        // 验证地铁站名称
        SubwayStation subwayStation = subwayStationRepository.findOne(houseForm.getSubwayStationId());
        if (subwayStation == null) {
            return new ServiceResult<>(false, "Not Valid Subway Station !");
        }

        houseDetail.setSubwayLineId(subwayLine.getId());
        houseDetail.setSubwayLineName(subwayLine.getName());

        houseDetail.setSubwayStationId(subwayStation.getId());
        houseDetail.setSubwayStationName(subwayStation.getName());

        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setAddress(houseForm.getDetailAddress());
        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());

        return ServiceResult.ofSuccess();
    }
}
