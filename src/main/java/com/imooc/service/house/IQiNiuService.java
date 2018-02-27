package com.imooc.service.house;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;
import java.io.InputStream;

/**
 * @author gusuchen
 * Created in 2018-01-20 13:50
 * Description: 七牛云服务
 * Modified by:
 */
public interface IQiNiuService {
    /**
     * 上传文件
     * @param file
     * @return
     * @throws QiniuException
     */
    Response uploadFile(File file) throws QiniuException;

    /**
     * 上传文件字节流
     * @param inputStream
     * @return
     * @throws QiniuException
     */
    Response uploadFile(InputStream inputStream) throws QiniuException;

    /**
     * 删除文件
     * @param key
     * @return
     * @throws QiniuException
     */
    Response deleteFile(String key) throws QiniuException;
}
