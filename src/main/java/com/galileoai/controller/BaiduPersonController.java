
package com.galileoai.controller;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.baidu.aip.face.MatchRequest;
import com.galileoai.R;
import com.galileoai.config.BaiduAipFace;
import com.galileoai.dao.BaiduApiConfigDao;
import com.galileoai.entity.BaiduApiConfig;
import com.galileoai.model.BodyAttr;
import com.galileoai.service.BaiduService;
import com.galileoai.utils.Utils;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/aipface")
@Validated
@Api(description = "人脸识别等")
public class BaiduPersonController {
    private final static Logger logger = LoggerFactory.getLogger(BaiduPersonController.class);


    @Autowired
    private BaiduAipFace baiduAipFace;

    @Autowired
    private BaiduApiConfigDao baiduApiConfigDao;
    @Autowired
    private BaiduService baiduService;
    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="人脸检测")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "image", value = "图片的base64编码", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/detect")
    public Object faceDetect(@RequestParam(value = "image")
                                         String image)throws Exception {

        baiduAipFace.updateBaiduConfig();
        //BaiduApiConfig aa= baiduApiConfigDao.findByDo_numLessThan(500);
        // 传入可选参数调用接口
        /*HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");*/
        String faceToken="no face";
        try {
            JSONObject res = baiduAipFace.aipFaceIni().detect(image, "BASE64", null).getJSONObject("result");
            if (res != null) {
                if (res.getInt("face_num") >= 1) {
                    JSONArray array = res.getJSONArray("face_list");
                    JSONObject face = (JSONObject) array.get(0);
                    return R.success(face.getString("face_token"));
                }
            }
        }
        catch (Exception e){

        }
        return R.error(faceToken);
    }

    /**
     * 人脸对比
     * @return
     * @throws Exception
     */
    @ApiOperation(value="人脸对比")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "image1", value = "图片的base64编码", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "image1_type", value = "图片1的类型,默认是BASE64，可选FACE_TOKEN",defaultValue = "BASE64",required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "image2", value = "图片的base64编码", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "image2_type", value = "图片2的类型",defaultValue = "BASE64",required = true, dataType = "string", paramType = "query"),
    })
    @PostMapping(value = "/match")
    public Object faceMatch(@RequestParam(value = "image1")
                                     String image1,
                            @RequestParam(value = "image2")
                                    String image2,
                            @RequestParam(value = "image1_type",defaultValue = "BASE64")
                                        String image1Type,
                            @RequestParam(value = "image2_type",defaultValue = "BASE64")
                                        String image2Type)throws Exception {
        try {
            // image1/image2也可以为url或facetoken, 相应的imageType参数需要与之对应。
            MatchRequest req1 = new MatchRequest(image1, image1Type);
            MatchRequest req2 = new MatchRequest(image2, image2Type);
            ArrayList<MatchRequest> requests = new ArrayList<MatchRequest>();
            requests.add(req1);
            requests.add(req2);

        /*    JSONObject res = baiduAipFace.aipFaceIni().match(requests);
            System.out.println(res.toString());
            return R.success(res.toString());*/

            Double score=0.00;
            try {
                JSONObject res = baiduAipFace.aipFaceIni().match(requests).getJSONObject("result");
                System.out.println(res.toString());
                //return R.success(res.toString());

                if (res != null) {
                    return R.success(res.getDouble("score"));
                }
            }
            catch (Exception e){

            }
            return R.error(score);
        }
        catch (Exception err){
            return R.error(0.00);
        }
    }


    /**
     * body_attr
     * @return
     * @throws Exception
     */
    @ApiOperation(value="特征值比对")
    @PostMapping(value = "/body_attr")
    public Object bodySearch(@RequestParam("file") MultipartFile file)throws Exception {

        try {
            baiduService.updateBaiduConfig();
            AipBodyAnalysis aipBodyAnalysis=baiduService.AipBodyAnalysis();
            if(aipBodyAnalysis==null)return R.error(0.00);

            // 传入可选参数调用接口
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("type", "upper_color,lower_color");


            JSONObject res = aipBodyAnalysis.bodyAttr(file.getBytes(), options);

            baiduService.updateBaiduConfig();
            System.out.println(res.toString());
            Gson gson = new Gson();
            BodyAttr bodyAttr = gson.fromJson(res.toString(), BodyAttr.class);
            return R.success(bodyAttr);
        }
        catch (Exception err){
            return R.error(0.00);
        }


    }

    /**
     * body_attr
     * @return
     * @throws Exception
     */
    @ApiOperation(value="特征值比对")
    @ApiImplicitParams({
            //@ApiImplicitParam(name = "authorization", value = "Authorization token", required = true, dataType = "string", paramType = "header"),
            @ApiImplicitParam(name = "image", value = "图片的base64编码", required = true, dataType = "string", paramType = "query")
    })
    @PostMapping(value = "/body_attr_by_img64")
    public Object bodySearchByImgBase64(@RequestParam(value = "image")
                                                    String image)throws Exception {

        try {
            baiduService.updateBaiduConfig();
            AipBodyAnalysis aipBodyAnalysis=baiduService.AipBodyAnalysis();
            if(aipBodyAnalysis==null)return R.error(0.00);
            // 传入可选参数调用接口
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("type", "upper_color,lower_color");
            JSONObject res = aipBodyAnalysis.bodyAttr(Utils.getByteByImgbase64(image), options);


            System.out.println(res.toString());
            Gson gson = new Gson();
            BodyAttr bodyAttr = gson.fromJson(res.toString(), BodyAttr.class);


            return R.success(res.toString());
        }
        catch (Exception err){
            return R.error(0.00);
        }


    }


}
