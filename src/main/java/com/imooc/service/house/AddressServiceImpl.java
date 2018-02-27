package com.imooc.service.house;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.imooc.entity.SubwayLine;
import com.imooc.entity.SubwayStation;
import com.imooc.entity.SupportAddress;
import com.imooc.repository.SubwayLineRepository;
import com.imooc.repository.SubwayStationRepository;
import com.imooc.repository.SupportAddressRepository;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.search.BaiduMapLocation;
import com.imooc.web.dto.SubwayLineDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author gusuchen
 * Created in 2018-01-15 17:40
 * Description:
 * Modified by:
 */
@Slf4j
@Service
public class AddressServiceImpl implements IAddressService {
    /**
     * baidu api access key
     */
    private static final String BAIDU_MAP_KEY = "6QtSF673D1pYl3eQkEXfwp8ZgsQpB77U";

    private static final String BAIDU_MAP_GEOCONV_API = "http://api.map.baidu.com/geocoder/v2/?";

    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayLineRepository subwayLineRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {
        List<SupportAddress> addressDOS = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDTO> addressDTOS = Lists.newArrayList();
        for (SupportAddress addressDO : addressDOS) {
            SupportAddressDTO addressDTO = modelMapper.map(addressDO, SupportAddressDTO.class);
            addressDTOS.add(addressDTO);
        }
        return new ServiceMultiResult<>(addressDTOS.size(), addressDTOS);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCityName(String cityEnName) {
        List<SupportAddress> addressDOS = supportAddressRepository.findAllByBelongToAndLevel(cityEnName, SupportAddress.Level.REGION.getValue());
        List<SupportAddressDTO> addressDTOS = Lists.newArrayList();
        for (SupportAddress addressDO : addressDOS) {
            SupportAddressDTO addressDTO = modelMapper.map(addressDO, SupportAddressDTO.class);
            addressDTOS.add(addressDTO);
        }
        return new ServiceMultiResult<>(addressDTOS.size(), addressDTOS);
    }

    @Override
    public ServiceMultiResult<SubwayLineDTO> findAllSubwayLineByCityName(String cityEnName) {
        List<SubwayLine> subwayLineList = subwayLineRepository.findSubwayLineDOSByCityEnName(cityEnName);
        List<SubwayLineDTO> subwayLineDTOList = Lists.newArrayList();
        for (SubwayLine subwayLine : subwayLineList) {
            SubwayLineDTO subwayLineDTO = modelMapper.map(subwayLine, SubwayLineDTO.class);
            subwayLineDTOList.add(subwayLineDTO);
        }
        return new ServiceMultiResult<>(subwayLineDTOList.size(), subwayLineDTOList);
    }

    @Override
    public ServiceMultiResult<SubwayStationDTO> findAllSubwayStationBySubwayId(Long subwayId) {
        List<SubwayStation> subwayStationList = subwayStationRepository.findAllBySubwayId(subwayId);
        List<SubwayStationDTO> subwayStationDTOList = Lists.newArrayList();
        for (SubwayStation subwayStation : subwayStationList) {
            SubwayStationDTO subwayStationDTO = modelMapper.map(subwayStation, SubwayStationDTO.class);
            subwayStationDTOList.add(subwayStationDTO);
        }
        return new ServiceMultiResult<>(subwayStationDTOList.size(), subwayStationDTOList);
    }

    @Override
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddressDTO> result = Maps.newHashMap();
        SupportAddress city = supportAddressRepository.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        SupportAddress region = supportAddressRepository.findByEnNameAndBelongTo(regionEnName, city.getEnName());

        result.put(SupportAddress.Level.CITY, modelMapper.map(city, SupportAddressDTO.class));
        result.put(SupportAddress.Level.REGION, modelMapper.map(region, SupportAddressDTO.class));
        return result;
    }

    @Override
    public ServiceResult<SubwayLineDTO> findSubwayLine(Long subwayLineId) {
        if (subwayLineId == null) {
            return ServiceResult.notFound();
        }

        SubwayLine subwayLine = subwayLineRepository.findOne(subwayLineId);
        if (subwayLine == null) {
            return ServiceResult.notFound();
        }

        return ServiceResult.ofResult(modelMapper.map(subwayLine, SubwayLineDTO.class));
    }

    @Override
    public ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId) {
        if (subwayStationId == null) {
            return ServiceResult.notFound();
        }

        SubwayStation subwayStation = subwayStationRepository.findOne(subwayStationId);
        if (subwayStation == null) {
            return ServiceResult.notFound();
        }

        return ServiceResult.ofResult(modelMapper.map(subwayStation, SubwayStationDTO.class));
    }

    @Override
    public ServiceResult<SupportAddressDTO> findCity(String cityEnName) {
        if (cityEnName == null) {
            return ServiceResult.notFound();
        }

        SupportAddress city = supportAddressRepository
                .findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        if (city == null) {
            return ServiceResult.notFound();
        }

        SupportAddressDTO addressDTO = modelMapper.map(city, SupportAddressDTO.class);

        return ServiceResult.ofResult(addressDTO);
    }

    @Override
    public ServiceResult<BaiduMapLocation> getBaiduMapLocation(String cityCnName, String address) {
        String encodeAdress;
        String encodeCity;

        try {
            encodeAdress = URLEncoder.encode(address, "UTF-8");
            encodeCity = URLEncoder.encode(cityCnName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("Error to encode house address", e);
            return ServiceResult.ofMessage(false, "Error to encode house address");
        }

        HttpClient httpClient = HttpClients.createDefault();
        StringBuilder sb = new StringBuilder(BAIDU_MAP_GEOCONV_API)
                .append("address=").append(encodeAdress).append("&")
                .append("city=").append(encodeCity).append("&")
                .append("output=json&")
                .append("ak=").append(BAIDU_MAP_KEY);

        HttpGet httpGet = new HttpGet(sb.toString());

        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return ServiceResult.ofMessage(false, "Can not get baidu map location");
            }

            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonNode jsonNode = objectMapper.readTree(result);

            int status = jsonNode.get("status").asInt();
            if (status != 0) {
                return ServiceResult.ofMessage(false, "Error to get map location for status: " + status);
            }

            JsonNode jsonNodeLocation = jsonNode.get("result").get("location");
            BaiduMapLocation location = new BaiduMapLocation()
                    .setLongitude(jsonNodeLocation.get("lng").asDouble())
                    .setLatitude(jsonNodeLocation.get("lat").asDouble());

            return ServiceResult.ofResult(location);
        } catch (IOException e) {
            log.error("Error to fetch baidumap api", e);
            return ServiceResult.ofMessage(false, "Error to fetch baidumap api");
        }
    }

    @Override
    public ServiceResult uploadLbs(BaiduMapLocation location, String title, String address,
                                   long houseId, int price, int area) {
        return null;
    }

    @Override
    public ServiceResult removeLbs(Long houseId) {
        return null;
    }
}
