package com.galileoai.config;

import com.baidu.aip.face.AipFace;
import com.galileoai.dao.BaiduApiConfigDao;
import com.galileoai.utils.Utils;
import okhttp3.*;
import org.hibernate.mapping.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Configuration
public class MyOkHttp {


    private final static Logger logger = LoggerFactory.getLogger(MyOkHttp.class);
    final MediaType JSON = MediaType.parse("application/json; charset=utf-8");



    public FormBody setFormBody(FormBody formBody){
        return formBody;
    }


    @Bean
    public OkHttpClient getOkHttpClient(){
        logger.info("OkHttpClient");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .build();
        return client;
    }



   /* @Bean
    public String postJson(String url,String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = getOkHttpClient().newCall(request).execute();
        return response.body().string();
    }
*/

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
