
package com.galileoai.controller;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.baidu.aip.face.MatchRequest;
import com.galileoai.R;
import com.galileoai.config.BaiduAipFace;
import com.galileoai.config.MyOkHttp;
import com.galileoai.dao.BaiduApiConfigDao;
import com.galileoai.model.BodyAttr;
import com.galileoai.model.FaceCompare;
import com.galileoai.model.FaceDetect;
import com.galileoai.service.BaiduService;
import com.galileoai.utils.Utils;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by baymin
 * 2017-07-10 23:15
 */
@RestController
@RequestMapping("/face++")
@Validated
@Api(description = "人脸识别等")
public class FaceADDPersonController {
    private final static Logger logger = LoggerFactory.getLogger(FaceADDPersonController.class);

    @Value("${face-api-key}")
    private String faceApiKey;
    @Value("${face-api-secret}")
    private String faceApiSecret;//

    @Autowired
    private MyOkHttp myOkHttp;


    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="人脸检测")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "image_base64", value = "图片的base64编码", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/detect")
    public Object faceDetect(@RequestParam(value = "image_base64")
                                     String image_base64)throws Exception {

        try {
            //post方式提交的数据
            FormBody formBody = new FormBody.Builder()
                    .add("api_key", faceApiKey)
                    .add("api_secret", faceApiSecret)
                    .add("image_base64",image_base64)
                    .build();
            final Request request = new Request.Builder()
                    .url("https://api-cn.faceplusplus.com/facepp/v3/detect")//请求的url
                    .post(formBody)
                    .build();
            Response response = myOkHttp.getOkHttpClient().newCall(request).execute();
            String resss = response.body().string();

            logger.info("人脸检测##############face++ result:"+resss);
            Gson gson=new Gson();
            FaceDetect faceDetect= gson.fromJson(resss, FaceDetect.class);
            if(faceDetect.faces.size()>0) return R.success(faceDetect.getFaces().get(0).face_token);
            return R.error("no face");
        }
        catch (Exception er){
            return R.error("no face");
        }
    }

    /**
     * 人脸对比
     * @return
     * @throws Exception
     */
    @ApiOperation(value="人脸对比")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "face_token1", value = "需要比对的人脸", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "image_base64_2", value = "图片2的BASE64",defaultValue = "BASE64",required = true, dataType = "string", paramType = "query"),
        })
    @PostMapping(value = "/match")
    public Object faceMatch(@RequestParam(value = "face_token1")
                                     String face_token1,
                            @RequestParam(value = "image_base64_2")
                                    String image_base64_2)throws Exception {
        try {
            //post方式提交的数据
            FormBody formBody = new FormBody.Builder()
                    .add("api_key", faceApiKey)
                    .add("api_secret", faceApiSecret)
                    .add("face_token1",face_token1)
                    .add("image_base64_2",image_base64_2)
                    .build();
            final Request request = new Request.Builder()
                    .url("https://api-cn.faceplusplus.com/facepp/v3/compare")//请求的url
                    .post(formBody)
                    .build();
            Response response = myOkHttp.getOkHttpClient().newCall(request).execute();
            String resss = response.body().string();

            logger.info("人脸对比##############face++ result:"+resss);

            Gson gson=new Gson();
            FaceCompare faceCompare= gson.fromJson(resss, FaceCompare.class);
            return R.success(faceCompare.getConfidence());
        }
        catch (Exception er){
            return R.error(0.00);
        }
    }



}
