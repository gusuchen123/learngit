package com.imooc.service.search;

import com.alibaba.fastjson.JSON;
import com.imooc.ApplicationTests;
import com.imooc.service.ServiceMultiResult;
import com.imooc.web.form.RentSearch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gusuchen
 * Created in 2018-01-21 23:39
 * Description:
 * Modified by:
 */
@Slf4j
public class SearchServiceTests extends ApplicationTests {

    @Autowired
    private ISearchService searchService;

    @Test
    public void testIndex() {
        searchService.index(15L);
    }

    @Test
    public void testRemove() {
        searchService.remove(15L);
    }

    @Test
    public void testQuery() {
        RentSearch rentSearch = new RentSearch()
                .setCityEnName("bj")
                .setSize(0)
                .setSize(10)
                .setKeywords("房屋描述");
        ServiceMultiResult<Long> houseIds = searchService.query(rentSearch);

        log.warn(JSON.toJSONString(houseIds));
    }
}
