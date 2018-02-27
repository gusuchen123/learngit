package com.imooc.service.search;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;
import com.imooc.base.HouseRoom;
import com.imooc.base.HouseSort;
import com.imooc.base.RentValueBlock;
import com.imooc.entity.House;
import com.imooc.entity.HouseDetail;
import com.imooc.entity.HouseTag;
import com.imooc.entity.SupportAddress;
import com.imooc.repository.HouseDetailRepository;
import com.imooc.repository.HouseRepository;
import com.imooc.repository.HouseTagRepository;
import com.imooc.repository.SupportAddressRepository;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.web.form.MapSearch;
import com.imooc.web.form.RentSearch;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author gusuchen
 * Created in 2018-01-21 18:33
 * Description:
 * Modified by:
 */
@Slf4j
@Service
public class SearchServiceImpl implements ISearchService {

    public static final String INDEX_NAME = "xunwu";

    public static final String INDEX_TYPE = "house";

    public static final String IK_SMART = "ik_smart";

    public static final String IK_MAX_WORD = "ik_max_word";

    public static final String SUGGEST = "suggest";

    @Autowired
    private IAddressService addressService;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TransportClient esClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void index(Long houseId) {
        if (houseId == null || houseId < 1) {
            log.error("index houseId {} does not exists", houseId);
            return;
        }

        House house = houseRepository.findOne(houseId);
        if (house == null) {
            // TODO 异常情况
            log.error("Index house {} does not exists", houseId);
            return;
        }

        HouseIndexTemplate indexTemplate = modelMapper.map(house, HouseIndexTemplate.class);

        HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
        if (detail == null) {
            // TODO 异常情况
            log.error("House Detail {} does not exists", detail);
            return;
        }

        modelMapper.map(detail, indexTemplate);

        // 获取百度地图具体位置的经纬度
        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(indexTemplate.getCityEnName(),
                SupportAddress.Level.CITY.getValue());

        SupportAddress region = supportAddressRepository.findByEnNameAndLevel(indexTemplate.getRegionEnName(),
                SupportAddress.Level.REGION.getValue());

        String detailAddress = new StringBuilder()
                .append(city.getCnName())
                .append(region.getCnName())
                .append(house.getStreet())
                .append(house.getDistrict())
                .append(detail.getAddress()).toString();

        ServiceResult<BaiduMapLocation> location = addressService.getBaiduMapLocation(city.getCnName(), detailAddress);
        if (!location.isSuccess()) {
            log.warn("Do not get baidu map location");
            return;
        }
        indexTemplate.setLocation(location.getResult());

        List<HouseTag> houseTags = houseTagRepository.findAllByHouseId(houseId);
        List<String> stringTags = Lists.newArrayList();
        if (CollectionUtils.isEmpty(houseTags)) {
            houseTags.forEach(houseTag ->
                    stringTags.add(houseTag.getName())
            );
            indexTemplate.setTags(stringTags);
        }

        SearchRequestBuilder requestBuilder = esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));

        log.debug(requestBuilder.toString());

        SearchResponse searchResponse = requestBuilder.get();

        boolean success;
        long totalHits = searchResponse.getHits().getTotalHits();
        if (totalHits == 0L) {
            // 创建索引
            success = create(indexTemplate);
        } else if (totalHits == 1L) {
            // 更新索引
            String esId = searchResponse.getHits().getAt(0).getId();
            success = update(esId, indexTemplate);
        } else  {
            // 删除并且更新索引
            success = deleteAndCreate(totalHits, indexTemplate);
        }

        if (success) {
            log.debug("Index success with house " + houseId);
        }
    }

    @Override
    public void remove(Long houseId) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))
                .source(INDEX_NAME);

        log.debug(builder.toString());

        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();

        log.debug("Index deleted " + deleted);

    }

    @Override
    public ServiceMultiResult<Long> query(RentSearch rentSearch) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 城市 cityEnName keyword 不需要分词
        if (rentSearch.getCityEnName() != null) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName())
            );
        }

        // 区域 regionEnName keyword 不需要分词
        if (rentSearch.getRegionEnName() != null && !("*").equals(rentSearch.getRegionEnName())) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName())
            );
        }

        // 租金 priceBlock 价格区间 范围查询
        RentValueBlock priceBlock = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
        if (!RentValueBlock.ALL.equals(priceBlock)) {
            RangeQueryBuilder rangePriceBuilder = QueryBuilders.rangeQuery(HouseIndexKey.PRICE);
            if (priceBlock.getMax() > 0) {
                rangePriceBuilder.lte(priceBlock.getMax());
            }

            if (priceBlock.getMin() > 0) {
                rangePriceBuilder.gte(priceBlock.getMin());
            }

            boolQuery.filter(rangePriceBuilder);
        }


        // 面积 areaBlock 面积区间  范围查询
        RentValueBlock areaBlock = RentValueBlock.matchArea(rentSearch.getAreaBlock());
        if (!RentValueBlock.ALL.equals(areaBlock)) {
            RangeQueryBuilder rangeAreaBuilder = QueryBuilders.rangeQuery(HouseIndexKey.AREA);
            if (areaBlock.getMax() > 0) {
                rangeAreaBuilder.lte(areaBlock.getMax());
            }

            if (areaBlock.getMin() > 0) {
                rangeAreaBuilder.gte(areaBlock.getMin());
            }

            boolQuery.filter(rangeAreaBuilder);
        }

        // 房型 room 房间数量
        if (rentSearch.getRoom() > HouseRoom.ROOM_ALL) {

            if (rentSearch.getRoom() == HouseRoom.ROOM_MAX) {
                RangeQueryBuilder rangeRoomBuilder = QueryBuilders.rangeQuery(HouseIndexKey.ROOM);
                rangeRoomBuilder.gte(HouseRoom.ROOM_MAX);
                boolQuery.filter(rangeRoomBuilder);

            } else {
                boolQuery.filter(
                        QueryBuilders.termQuery(HouseIndexKey.ROOM, rentSearch.getRoom())
                );
            }

        }

        // direction 房屋朝向 keyword 不需要分词
        if (rentSearch.getDirection() > 0) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.DIRECTION, rentSearch.getDirection())
            );
        }

        // rentWay 租住方式 keyword 不需要分词
        if (rentSearch.getRentWay() > -1) {
            boolQuery.filter(
                    QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearch.getRentWay())
            );
        }

        // keywords 多字段匹配
        if (!Strings.isNullOrEmpty(rentSearch.getKeywords())) {
            /**
             * 1. 提高重要关键字搜索的权重, 权重默认是1.0
             * 2. 根据业务场景使用must should
             */
            boolQuery.must(
                    QueryBuilders.termQuery(HouseIndexKey.TITLE, rentSearch.getKeywords())
                    .boost(2.0f)
            );

            boolQuery.must(
                    QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
//                            HouseIndexKey.TITLE,
                            HouseIndexKey.DISTRICT,
                            HouseIndexKey.STREET,
                            HouseIndexKey.ROUND_SERVICE,
                            HouseIndexKey.LAYOUT_DESC,
                            HouseIndexKey.DESCRIPTION,
                            HouseIndexKey.TRAFFIC,
                            HouseIndexKey.SUBWAY_LINE_NAME,
                            HouseIndexKey.SUBWAY_STATION_NAME
                    )
            );
        }

        /**
         * 2. setFetchSource--解决返回的数据集过大的问题，当前只是返回HouseIndexKey.HOUSE_ID
         * 2. 提高搜索的权重
         */
        SearchRequestBuilder searchBuilder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(
                        rentSearch.getOrderBy(),
                        SortOrder.fromString(rentSearch.getOrderDirection())
                )
                .setFrom(rentSearch.getStart())
                .setSize(rentSearch.getSize())
                .setFetchSource(HouseIndexKey.HOUSE_ID, null);

        log.debug(searchBuilder.toString());

        List<Long> houseIds = Lists.newArrayList();
        SearchResponse searchResponse = searchBuilder.get();
        if (searchResponse.status() != RestStatus.OK) {
            log.warn("Search status is not ok " + searchBuilder);
            return ServiceMultiResult.of(0, houseIds);
        }

        searchResponse.getHits().forEach(searchHit -> {
            log.debug(JSON.toJSONString(searchHit.getSource()));

            houseIds.add(Longs.tryParse(
                    String.valueOf(searchHit.getSource().get(HouseIndexKey.HOUSE_ID))
            ));
        });

        return ServiceMultiResult.of(searchResponse.getHits().totalHits, houseIds);
    }

    @Override
    public ServiceResult<List<String>> suggest(String prefix) {
        CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion(SUGGEST)
                .prefix(prefix)
                .size(5);

        SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion("autocomplete", suggestion);

        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .suggest(suggestBuilder);
        log.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        if (response.status() != RestStatus.OK) {
            log.warn("searchResponse is not ok " + requestBuilder.toString());
            return ServiceResult.notFound();
        }

        Suggest suggest = response.getSuggest();
        if (suggest == null) {
            return ServiceResult.ofResult(Lists.newArrayList());
        }
        Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

        // 去除重复的记录
        int maxSuggest = 0;
        Set<String> suggestSet = Sets.newHashSet();
        for (Object term : result) {
            if (term instanceof CompletionSuggestion.Entry) {
                CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;

                if (item.getOptions().isEmpty()) {
                    continue;
                }

                for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
                    String tip = option.getText().string();
                    if (suggestSet.contains(tip)) {
                        continue;
                    }
                    suggestSet.add(tip);
                    maxSuggest++;
                }

            }
            if (maxSuggest > 5) {
                break;
            }
        }

        List<String> suggests = Lists.newArrayList(
                suggestSet.toArray(new String[]{})
        );
        return ServiceResult.ofResult(suggests);
    }

    @Override
    public ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName))
                .filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, regionEnName))
                .filter(QueryBuilders.termQuery(HouseIndexKey.DISTRICT, district));

        SearchRequestBuilder searchRequest = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addAggregation(
                        AggregationBuilders.terms(HouseIndexKey.AGG_DISTRICT)
                        .field(HouseIndexKey.DISTRICT)
                ).setSize(0);
        log.debug(searchRequest.toString());

        SearchResponse response = searchRequest.get();
        if (response.status() != RestStatus.OK) {
            log.warn("Failed to aggregate for " + HouseIndexKey.AGG_DISTRICT);
            return ServiceResult.ofResult(0L);
        }

        Terms terms = response.getAggregations().get(HouseIndexKey.AGG_DISTRICT);

        if (!CollectionUtils.isEmpty(terms.getBuckets())) {
            return ServiceResult.ofResult(
                    terms.getBucketByKey(district).getDocCount()
            );
        }
        return ServiceResult.ofResult(0L);
    }

    @Override
    public ServiceMultiResult<HouseBucketDTO> mapAggregateByCityEnName(String cityEnName) {
        if (Strings.isNullOrEmpty(cityEnName)) {
            return ServiceMultiResult.of(0, Lists.newArrayList());
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(
                        QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName)
                );
        AggregationBuilder aggBuilder = AggregationBuilders.terms(HouseIndexKey.AGG_REGION)
                .field(HouseIndexKey.REGION_EN_NAME);

        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addAggregation(aggBuilder);
        log.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        List<HouseBucketDTO> buckets = Lists.newArrayList();
        if (response.status() != RestStatus.OK) {
            log.warn("Aggregate status is not ok for " + requestBuilder);
            return ServiceMultiResult.of(0, buckets);
        }

        Terms terms = response.getAggregations().get(HouseIndexKey.AGG_REGION);
        terms.getBuckets().forEach(bucket -> {
            buckets.add(
                    new HouseBucketDTO(bucket.getKeyAsString(), bucket.getDocCount())
            );
        });

        return ServiceMultiResult.of(response.getHits().totalHits, buckets);
    }

    @Override
    public ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy,
                                             String orderDirection, int start, int size) {
        if (Strings.isNullOrEmpty(cityEnName)) {
            log.warn("MapSearch cityEnName is empty or null ");
            return ServiceMultiResult.of(0, Lists.newArrayList());
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(
                        QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName)
                );

        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(HouseSort.getSortKey(orderBy), SortOrder.fromString(orderDirection))
                .setFrom(start)
                .setSize(size)
                .setFetchSource(HouseIndexKey.HOUSE_ID, null);
        log.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        List<Long> houseIds = Lists.newArrayList();
        if (response.status() != RestStatus.OK) {
            log.warn("Search response status is not ok for " + requestBuilder);
            return ServiceMultiResult.of(0, houseIds);
        }

        for (SearchHit hit : response.getHits().getHits()) {
            houseIds.add(
                    Longs.tryParse(
                            String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))
                    )
            );
        }

        return ServiceMultiResult.of(response.getHits().getTotalHits(), houseIds);
    }

    @Override
    public ServiceMultiResult<Long> mapQuery(MapSearch mapSearch) {
        if (Strings.isNullOrEmpty(mapSearch.getCityEnName())) {
            log.warn("MapSearch cityEnName is empty or null for " + mapSearch);
            return ServiceMultiResult.of(0, Lists.newArrayList());
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(
                        QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, mapSearch.getCityEnName())
                );

        // 地图范围经纬度精确查询
        boolQuery.filter(
                QueryBuilders.geoBoundingBoxQuery(HouseIndexKey.GEO_LOCATION)
                        .setCorners(
                                new GeoPoint(mapSearch.getLeftLatitude(), mapSearch.getLeftLongitude()),
                                new GeoPoint(mapSearch.getRightLatitude(), mapSearch.getRightLongitude())
                        )
        );

        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(
                        HouseSort.getSortKey(mapSearch.getOrderBy()),
                        SortOrder.fromString(mapSearch.getOrderDirection())
                )
                .setFrom(mapSearch.getStart())
                .setSize(mapSearch.getSize())
                .setFetchSource(HouseIndexKey.HOUSE_ID, null);
        log.debug(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        List<Long> houseIds = Lists.newArrayList();
        if (response.status() != RestStatus.OK) {
            log.warn("Search response status is not ok for " + requestBuilder);
            return ServiceMultiResult.of(0, houseIds);
        }

        for (SearchHit hit : response.getHits().getHits()) {
            houseIds.add(
                    Longs.tryParse(
                            String.valueOf(hit.getSource().get(HouseIndexKey.HOUSE_ID))
                    )
            );
        }

        return ServiceMultiResult.of(response.getHits().getTotalHits(), houseIds);
    }

    private boolean updateSuggest(HouseIndexTemplate indexTemplate) {
        // 中文分词
        AnalyzeRequestBuilder analyzeBuilder = new AnalyzeRequestBuilder(
                this.esClient, AnalyzeAction.INSTANCE, INDEX_NAME,
                indexTemplate.getTitle(),
                indexTemplate.getLayoutDesc(),
                indexTemplate.getDescription(),
                indexTemplate.getTraffic(),
                indexTemplate.getRoundService()
        );

        analyzeBuilder.setAnalyzer(IK_SMART);

        log.debug(analyzeBuilder.toString());

        AnalyzeResponse response = analyzeBuilder.get();
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        if (tokens == null) {
            log.warn("Can not analyze tokens for house " + indexTemplate.getHouseId());
            return false;
        }

        List<HouseSuggest> suggests = Lists.newArrayList();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            // 排除数字类型 && 小于2个字符的字符集
            if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
                continue;
            }

            HouseSuggest suggest = new HouseSuggest().setInput(token.getTerm());
            suggests.add(suggest);
        }

        // 定制化小区自动补全
        if (indexTemplate.getDistrict() != null) {
            suggests.add(
                    new HouseSuggest().setInput(indexTemplate.getDistrict())
            );
        }

        // 定制化街道自动补全
        if (indexTemplate.getStreet() != null) {
            suggests.add(
                    new HouseSuggest().setInput(indexTemplate.getStreet())
            );
        }

        // 定制化地铁线自动补全
        if (indexTemplate.getSubwayLineName() != null) {
            suggests.add(
                    new HouseSuggest().setInput(indexTemplate.getSubwayLineName())
            );
        }

        // 定制化地铁站自动补全
        if (indexTemplate.getSubwayStationName() != null) {
            suggests.add(
                    new HouseSuggest().setInput(indexTemplate.getSubwayStationName())
            );
        }

        indexTemplate.setSuggest(suggests);

        return true;
    }

    /**
     * 根据房源索引结构模板创建索引
     * @param indexTemplate
     * @return
     */
    private boolean create(HouseIndexTemplate indexTemplate) {
        if (!updateSuggest(indexTemplate)) {
            return false;
        }

        try {
            IndexRequestBuilder requestBuilder = esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON);

            log.debug(requestBuilder.toString());

            IndexResponse response = requestBuilder.get();
            if (response.status() == RestStatus.CREATED) {
                return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            log.error("Error to index house " + indexTemplate.getHouseId(), e);
            return false;
        }
    }

    /**
     * 根据 索引id 房源索引结构模板 更新索引
     * @param esId
     * @param indexTemplate
     * @return
     */
    private boolean update(String esId, HouseIndexTemplate indexTemplate) {
        if (!updateSuggest(indexTemplate)) {
            return false;
        }

        try {
            UpdateRequestBuilder requestBuilder = esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
                    .setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON);

            log.debug(requestBuilder.toString());

            UpdateResponse response = requestBuilder.get();
            if (response.status() == RestStatus.OK) {
                return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            log.error("Error to index house " + indexTemplate.getHouseId(), e);
            return false;
        }
    }

    /**
     * 当出现异常情况时，先删除索引，再创建索引
     * @param indexTemplate
     * @return
     */
    private boolean deleteAndCreate(long totalHits, HouseIndexTemplate indexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId()))
                .source(INDEX_NAME);

        log.debug(builder.toString());

        BulkByScrollResponse response = builder.get();

        long deleted = response.getDeleted();
        if (totalHits != deleted) {
            log.warn("Need delete {}, but {} was deleted ", totalHits, deleted);
            return false;
        }
        return create(indexTemplate);
    }
}
