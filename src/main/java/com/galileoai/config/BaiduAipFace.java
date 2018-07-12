package com.galileoai.config;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.baidu.aip.face.AipFace;
import com.galileoai.DemoApplication;
import com.galileoai.dao.BaiduApiConfigDao;
import com.galileoai.entity.BaiduApiConfig;
import com.galileoai.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class BaiduAipFace {


    private final static Logger logger = LoggerFactory.getLogger(BaiduAipFace.class);
    /**
     * baidu-app-id:
     baidu-api-key:
     baidu-secret-key:
     */
    @Value("${baidu-app-id}")
    private String APP_ID;//
    @Value("${baidu-api-key}")
    private String API_KEY;//
    @Value("${baidu-secret-key}")
    private String SECRET_KEY;//

    @Autowired
    private BaiduApiConfigDao baiduApiConfigDao;

    @Bean
    public int updateBaiduConfig(){
        int num=baiduApiConfigDao.updateConfig(Utils.getNowDate());
        logger.info("百度配置已经更新,"+num);
        return num;
    }

    @Bean
    public AipFace aipFaceIni(){
        logger.info("BaiduAipFace.aipFaceIni");
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        //System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        return client;
    }

   /* @Bean
    public AipBodyAnalysis AipBodyAnalysis(){


        baiduApiConfigDao.findByDo_numLessThanAndMyself(500);


        // 初始化一个AipBodyAnalysis
        AipBodyAnalysis client = new AipBodyAnalysis(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        //System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        return client;
    }*/
}
