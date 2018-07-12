package com.galileoai.service;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.galileoai.config.BaiduAipFace;
import com.galileoai.dao.BaiduApiConfigDao;
import com.galileoai.entity.BaiduApiConfig;
import com.galileoai.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by baymin on 18-3-15.
 */
@Service
public class BaiduService {

    private final static Logger logger = LoggerFactory.getLogger(BaiduService.class);
    @Autowired
    private BaiduApiConfigDao baiduApiConfigDao;


    public int updateBaiduConfig(){
        int num=baiduApiConfigDao.updateConfig(Utils.getNowDate());
        logger.info("百度配置已经更新,"+num);
        return num;
    }

    public AipBodyAnalysis AipBodyAnalysis(){

        BaiduApiConfig baiduApiConfig= baiduApiConfigDao.findByDo_numLessThanAndMyself(500);

        if(baiduApiConfig==null)return null;

        baiduApiConfigDao.findByDo_numLessThanAndMyself(500);

        // 初始化一个AipBodyAnalysis
        AipBodyAnalysis client = new AipBodyAnalysis(baiduApiConfig.getApp_id(), baiduApiConfig.getApi_key(), baiduApiConfig.getSecret_key());

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        baiduApiConfig.setDo_num(baiduApiConfig.getDo_num()+1);
        baiduApiConfigDao.save(baiduApiConfig);
        //System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        return client;
    }


}
