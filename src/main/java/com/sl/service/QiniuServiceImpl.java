package com.sl.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
@Service
public class QiniuServiceImpl implements IQiniuService, InitializingBean {


    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private Auth auth;

    @Value("${qiniu.privateBucket}")
    private String privateBucket;

    @Value("${qiniu.privateDomain}")
    private String privateDomain;

    @Value("${qiniu.publicBucket}")
    private String publicBucket;

    @Value("${qiniu.publicDomain}")
    private String publicDomain;

    @Value("${qiniu.expireInSeconds}")
    private Long expireInSeconds;


    private StringMap putPolicy;

    @Override
    public String uploadFile(File file, String fileName) throws QiniuException {
        Response response = this.uploadManager.put(file, fileName, getPrivateUploadToken());
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, fileName, getPrivateUploadToken());
            retry++;
        }
        if (response.statusCode == 200) {
            return "http://" + privateDomain + "/" + fileName;
        }
        return "上传失败!";
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileName) throws QiniuException {
        Response response = this.uploadManager.put(inputStream, fileName, getPrivateUploadToken(), null, null);
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(inputStream, fileName, getPrivateUploadToken(), null, null);
            retry++;
        }
        if (response.statusCode == 200) {
            return "http://" + privateDomain + "/" + fileName;
        }
        return "上传失败!";
    }


    @Override
    public String delete(String key) throws QiniuException {
        Response response = bucketManager.delete(this.privateBucket, key);
        int retry = 0;
        while (response.needRetry() && retry++ < 3) {
            response = bucketManager.delete(privateBucket, key);
        }
        return response.statusCode == 200 ? "删除成功!" : "删除失败!";
    }

    @Override
    public String downloadPrivateFileUrl(String url) {
        String finalUrl = auth.privateDownloadUrl(url, expireInSeconds);
        return finalUrl;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }

    /**
     * 获取公开库上传凭证
     */
    private String getPrivateUploadToken() {
        return this.auth.uploadToken(privateBucket, null, 3600, putPolicy);
    }

    /**
     * 获取私有库上传凭证
     */
    private String getOpenUploadToken() {
        return this.auth.uploadToken(publicBucket, null, 3600, putPolicy);
    }

}
