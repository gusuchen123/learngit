package com.imooc.service.user;

import com.imooc.service.ServiceResult;

/**
 * @author gusuchen
 * Created in 2018-01-24 11:56
 * Description: 验证码服务
 * Modified by:
 */
public interface ISmsService {
    /**
     * 发送验证码到指定手机 缓存验证码 10分钟 及 请求间隔时间1分钟
     * @param telephone
     * @return
     */
    ServiceResult<String> sendSms(String telephone);

    /**
     * 获取缓存中的验证码
     * @param telephone
     * @return
     */
    String getSmsCode(String telephone);

    /**
     * 移除指定手机号的验证码缓存
     * @param telephone
     */
    void removeSmsCode(String telephone);
}
