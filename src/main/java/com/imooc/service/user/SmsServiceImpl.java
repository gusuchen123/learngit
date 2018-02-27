package com.imooc.service.user;

import com.imooc.service.ServiceResult;
import org.springframework.stereotype.Service;

/**
 * @author gusuchen
 * Created in 2018-01-24 11:56
 * Description:
 * Modified by:
 */
@Service
public class SmsServiceImpl implements ISmsService {

    @Override
    public ServiceResult<String> sendSms(String telephone) {
        return ServiceResult.ofResult("123456");
    }

    @Override
    public String getSmsCode(String telephone) {
        return "123456";
    }

    @Override
    public void removeSmsCode(String telephone) {

    }
}
