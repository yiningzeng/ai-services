
package com.galileoai.controller;


import com.baidu.aip.ocr.AipOcr;
import com.galileoai.DemoApplication;
import com.galileoai.MyOkHttpClient;
import com.galileoai.R;
import com.galileoai.ret.Res;
import com.galileoai.ret.ResPlate;
import com.galileoai.utils.Base64Test;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by baymin
 * 2017-07-10 23:15
 */
@RestController
@RequestMapping("/car")
@Validated
@Api(description = "car-plate")
public class CarPlateController {
    private final static Logger logger = LoggerFactory.getLogger(CarPlateController.class);
    @Value("${platepath}")
    private String platepath;
    @Value("${plateurl}")
    private String plateurl;
    @Value("${shpath}")
    private String shpath;
    @Value("${isbaidu}")
    private Integer isbaidu;
    /*private static AipOcr client;
    public static final String APP_ID = "11170015";
    public static final String API_KEY = "Bvk4osbTe2QG0vWfC1bZ4fmW";
    public static final String SECRET_KEY = "QTsdvvUTIEjrvd26VaCNpfAONAZGN45f";

    public */

    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="车牌识别")
    @PostMapping(value = "/img")
    public Object getimg(@RequestParam("file") MultipartFile file)throws Exception {

        ResPlate resPlate = new ResPlate();
        //res.set(-1);
        //res.setMsg("有误");
        try {
//            MultipartFile file=files[0];
            File dir = new File(platepath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String fileAllPath = "";
            String name = System.currentTimeMillis() + ".jpg";
            fileAllPath = platepath + name;
            FileOutputStream out = new FileOutputStream(fileAllPath);
            out.write(bytes);
            out.flush();
            out.close();

            long now = System.currentTimeMillis();


            String url = plateurl+"?file=" + URLEncoder.encode(platepath + name, "UTF-8");

            logger.info("生产url:" + url);
            String ress = MyOkHttpClient.getInstance().get(url);

            logger.info("自己返回的结果"+ress);
            if (ress.contains("\"code\": 0")) {
                logger.info("自己的识别"+ress);
                return ress;
            }
            else if(ress.contains("yes")){
                //region 第三方识别

                if (isbaidu == 1) {

                    // 初始化一个AipOcr

                    // 传入可选参数调用接口
                    HashMap<String, String> options = new HashMap<String, String>();
                    options.put("multi_detect", "true");

                    JSONObject aa = DemoApplication.client.plateLicense(file.getBytes(), options);

                    System.out.println(aa.toString(2));
                    try {
                        JsonObject jsonObject = new JsonParser().parse(aa.toString()).getAsJsonObject().get("words_result").getAsJsonArray().get(0).getAsJsonObject();
                        resPlate.setNumber(jsonObject.get("number").getAsString());
                        JsonArray jsonArray = jsonObject.getAsJsonArray("vertexes_location");


                        JsonObject pointLeftTop = jsonArray.get(0).getAsJsonObject();
                        JsonObject pointRightTop = jsonArray.get(1).getAsJsonObject();
                        JsonObject pointLeftBottom = jsonArray.get(3).getAsJsonObject();
                        JsonObject pointRightBottom = jsonArray.get(2).getAsJsonObject();

                        //第一种情况，左上角的点最高
                        if (pointLeftTop.get("y").getAsInt() < pointRightTop.get("y").getAsInt()) {
                            resPlate.setX(pointLeftBottom.get("x").getAsInt());
                            resPlate.setY(pointLeftTop.get("y").getAsInt());
                            resPlate.setW(pointRightTop.get("x").getAsInt() - pointLeftBottom.get("x").getAsInt());
                            resPlate.setH(pointRightBottom.get("y").getAsInt() - pointLeftTop.get("y").getAsInt());
                        } else {//you上角的点最高
                            resPlate.setX(pointLeftTop.get("x").getAsInt());
                            resPlate.setY(pointRightTop.get("y").getAsInt());
                            resPlate.setW(pointRightBottom.get("x").getAsInt() - pointLeftTop.get("x").getAsInt());
                            resPlate.setH(pointLeftBottom.get("y").getAsInt() - pointRightTop.get("y").getAsInt());
                        }
                        logger.info("return :" + resPlate.toString());
                        return R.success(resPlate);
                    } catch (Exception er) {
                        return R.error(resPlate);
                    } finally {
                        now = System.currentTimeMillis() - now;
                        logger.info("baidu baidu time :" + now);
                    }

                } else {
                    try {

                        ress = MyOkHttpClient.getInstance().lplatePost("https://plateocr.market.alicloudapi.com/vehiclelicense",
                                "data:image/jpg;base64," + Base64Test.GetImageStr(fileAllPath));
                        //String ress="{\"code\":\"1\",\"msg\":\"查询成功\",\"result\":[{\"color\":\"blue\",\"number\":\"渝GCH123\",\"probability\":[1,1,1,1,0.99999988079071,0.9999988079071,0.99998712539673],\"vertexes_location\":[{\"y\":271,\"x\":315},{\"y\":288,\"x\":479},{\"y\":335,\"x\":474},{\"y\":318,\"x\":309}]}]}\n";
                        //System.out.println(ress);

                        logger.info(ress);
                        JsonObject jsonObject = new JsonParser().parse(ress).getAsJsonObject();
                        if (jsonObject.get("code").getAsString().equals("1")) {
                            //resPlate.setNumber(jsonObject.get("number").getAsString());
                            JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                            if (jsonArray.size() > 0) {
                                JsonObject plateJson = jsonArray.get(0).getAsJsonObject();
                                resPlate.setNumber(plateJson.get("number").getAsString());

                                JsonArray point = plateJson.get("vertexes_location").getAsJsonArray();

                                JsonObject pointLeftTop = point.get(0).getAsJsonObject();
                                JsonObject pointRightTop = point.get(1).getAsJsonObject();
                                JsonObject pointLeftBottom = point.get(3).getAsJsonObject();
                                JsonObject pointRightBottom = point.get(2).getAsJsonObject();

                                //第一种情况，左上角的点最高
                                if (pointLeftTop.get("y").getAsInt() < pointRightTop.get("y").getAsInt()) {
                                    resPlate.setX(pointLeftBottom.get("x").getAsInt());
                                    resPlate.setY(pointLeftTop.get("y").getAsInt());
                                    resPlate.setW(pointRightTop.get("x").getAsInt() - pointLeftBottom.get("x").getAsInt());
                                    resPlate.setH(pointRightBottom.get("y").getAsInt() - pointLeftTop.get("y").getAsInt());
                                } else {//you上角的点最高
                                    resPlate.setX(pointLeftTop.get("x").getAsInt());
                                    resPlate.setY(pointRightTop.get("y").getAsInt());
                                    resPlate.setW(pointRightBottom.get("x").getAsInt() - pointLeftTop.get("x").getAsInt());
                                    resPlate.setH(pointLeftBottom.get("y").getAsInt() - pointRightTop.get("y").getAsInt());
                                }
                                logger.info("return :" + resPlate.toString());
                                return R.success(resPlate);

                            }
                        }
                    } catch (Exception er) {
                        return R.error(resPlate);
                    } finally {
                        now = System.currentTimeMillis() - now;
                        logger.info("time :" + now);
                    }
                }


                return R.error(resPlate);

                //endregion
            }
            else return R.error(resPlate);


        } catch (Exception er) {
            er.printStackTrace();
            return R.error(resPlate);
        }


        //return resPlate;
    }

}
