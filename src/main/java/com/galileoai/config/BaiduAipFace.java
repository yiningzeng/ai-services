package com.galileoai.config;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.baidu.aip.face.AipFace;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaiduAipFace {

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

    @Bean
    public AipFace aipFaceIni(){

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
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        return client;
    }

    @Bean
    public AipBodyAnalysis AipBodyAnalysis(){

        // 初始化一个AipBodyAnalysis
        AipBodyAnalysis client = new AipBodyAnalysis(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        return client;
    }
}
