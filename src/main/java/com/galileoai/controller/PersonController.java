
package com.galileoai.controller;


import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.baidu.aip.face.AipFace;
import com.galileoai.DemoApplication;
import com.galileoai.MyOkHttpClient;
import com.galileoai.R;
import com.galileoai.ret.ResPlate;
import com.galileoai.utils.Base64Test;
import com.galileoai.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by baymin
 * 2017-07-10 23:15
 */
@RestController
@RequestMapping("/person")
@Validated
@Api(description = "人体分析")
public class PersonController {
    private final static Logger logger = LoggerFactory.getLogger(PersonController.class);
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

    //设置APPID/AK/SK
    public String APP_ID = "11468667";
    public String API_KEY = "diarG0OlmBSRHpI13dHkvqQ8";
    public String SECRET_KEY = "RYYGvjDnjehaRRnaerppKBY7xxvxV0Tm";




    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="人体关键点识别")
    @PostMapping(value = "/body_attr")
    public Object bodyAnalysis(@RequestParam("file") MultipartFile file)throws Exception {

        try {

            String fileAllPath = Utils.imgSave(file,platepath);

            // 初始化一个AipBodyAnalysis
            AipBodyAnalysis client = new AipBodyAnalysis(APP_ID, API_KEY, SECRET_KEY);

            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);

            // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
            //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
            //client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

            // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
            // 也可以直接通过jvm启动参数设置此环境变量
            System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

            // 调用接口
            JSONObject res = client.bodyAttr(fileAllPath, new HashMap<String, String>());

            String ress=res.toString();
            System.out.println(ress);
            return ress;

        }
        catch (Exception err){
            return R.error(err.getMessage());
        }

        //return resPlate;
    }



    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="新增人脸")
    @PostMapping(value = "/face_add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id（由数字、字母、下划线组成），长度限制128B", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "user_info", value = "用户资料，长度限制256B", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "group_id", value = "用户组id，标识一组用户（由数字、字母、下划线组成），长度限制128B。如果需要将一个uid注册到多个group下，group_id需要用多个逗号分隔，每个group_id长度限制为48个英文字符。注：group无需单独创建，注册用户时则会自动创建group。\n" +
                    "产品建议：根据您的业务需求，可以将需要注册的用户，按照业务划分，分配到不同的group下，例如按照会员手机尾号作为groupid，用于刷脸支付、会员计费消费等，这样可以尽可能控制每个group下的用户数与人脸数，提升检索的准确率", required = true, dataType = "string", paramType = "query"),
    })
    public Object faceIdentify(@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "userId")
                                       String userId,
                               @RequestParam(value = "user_info")
                                           String userInfo,
                               @RequestParam(value = "group_id")
                                           String groupId)throws Exception {
        try {
            String fileAllPath = Utils.imgSave(file,platepath);
            // 初始化一个AipFace
            AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);


            // 也可以直接通过jvm启动参数设置此环境变量
            System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");


            // 传入可选参数调用接口
            HashMap<String, String> options = new HashMap<String, String>();
            options.put("user_info", userInfo);
            //String uid = "user1";
            //String userInfo = "user's info";
            //String groupId = "group1,group2";


            /**
             * 人脸注册接口
             *
             * @param image - 图片信息(**总数据大小应小于10M**)，图片上传方式根据image_type来判断
             * @param imageType - 图片类型 **BASE64**:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；**URL**:图片的 URL地址( 可能由于网络等原因导致下载图片时间过长)**；FACE_TOKEN**: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
             * @param groupId - 用户组id（由数字、字母、下划线组成），长度限制128B
             * @param userId - 用户id（由数字、字母、下划线组成），长度限制128B
             * @param options - 可选参数对象，key: value都为string类型
             * options - options列表:
             *   user_info 用户资料，长度限制256B
             *   quality_control 图片质量控制  **NONE**: 不进行控制 **LOW**:较低的质量要求 **NORMAL**: 一般的质量要求 **HIGH**: 较高的质量要求 **默认 NONE**
             *   liveness_control 活体检测控制  **NONE**: 不进行控制 **LOW**:较低的活体要求(高通过率 低攻击拒绝率) **NORMAL**: 一般的活体要求(平衡的攻击拒绝率, 通过率) **HIGH**: 较高的活体要求(高攻击拒绝率 低通过率) **默认NONE**
             * @return JSONObject
             */
            //public JSONObject addUser(String image, String imageType, String groupId, String userId, HashMap<String, String> options) {


            JSONObject res = client.addUser(new BASE64Encoder().encode(file.getBytes()),"BASE64",groupId,userId,options);
            //client.addUser(fileAllPath,"jpg",groupId,uid, );

            // 参数为本地图片二进制数组
            //client.addUser()
            //JSONObject res = client.addUser(String.valueOf(file.getBytes()), uid, userInfo, groupId, options);


            String ress=res.toString();
            System.out.println(ress);
            return ress;

        }
        catch (Exception err){
            return R.error(err.getMessage());
        }

        //return resPlate;
    }


    /**
     * 人脸搜索接口
     * @return
     * @throws Exception
     **  - 图片信息(**总数据大小应小于10M**)，图片上传方式根据image_type来判断
     *      * @param imageType - 图片类型 **BASE64**:图片的base64值，base64编码后的图片数据，需urlencode，编码后的图片大小不超过2M；**URL**:图片的 URL地址( 可能由于网络等原因导致下载图片时间过长)**；FACE_TOKEN**: 人脸图片的唯一标识，调用人脸检测接口时，会为每个人脸图片赋予一个唯一的FACE_TOKEN，同一张图片多次检测得到的FACE_TOKEN是同一个
     *      * @param options - 可选参数对象，key: value都为string类型
     *      * options - options列表:
     *      *   face_field 包括**age,beauty,expression,faceshape,gender,glasses,landmark,race,quality,facetype信息**  <br> 逗号分隔. 默认只返回face_token、人脸框、概率和旋转角度
     *      *   max_face_num 最多处理人脸的数目，默认值为1，仅检测图片中面积最大的那个人脸；**最大值10**，检测图片中面积最大的几张人脸。
     *      *   face_type 人脸的类型 **LIVE**表示生活照：通常为手机、相机拍摄的人像图片、或从网络获取的人像图片等**IDCARD**表示身份证芯片照：二代身份证内置芯片中的人像照片 **WATERMARK**表示带水印证件照：一般为带水印的小图，如公安网小图 **CERT**表示证件照片：如拍摄的身份证、工卡、护照、学生证等证件图片 默认**LIVE**
     *      * @return JSONObject
     */
    @ApiOperation(value="人脸搜索接口")
    @PostMapping(value = "/face_search")
    @ApiImplicitParams({
           @ApiImplicitParam(name = "groupIdList", value = "从指定的group中进行查找 用逗号分隔，**上限20个**", required = true, dataType = "string", paramType = "query"),
    })
    public Object faceSearch(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "groupIdList")
                                String groupIdList)throws Exception {
        long now=System.currentTimeMillis();

        try {
            //String fileAllPath = Utils.imgSave(file,platepath);
            // 初始化一个AipFace
            AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);

            // 也可以直接通过jvm启动参数设置此环境变量
            System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");


            JSONObject res = client.search(new BASE64Encoder().encode(file.getBytes()),"BASE64",groupIdList,null);
            //client.addUser(fileAllPath,"jpg",groupId,uid, );

            // 参数为本地图片二进制数组
            //client.addUser()
            //JSONObject res = client.addUser(String.valueOf(file.getBytes()), uid, userInfo, groupId, options);


            String ress=res.toString();
            System.out.println(ress);
            return ress;

        }
        catch (Exception err){
            return R.error(err.getMessage());
        }
        finally {
            System.out.println((System.currentTimeMillis()-now));
        }

        //return resPlate;
    }

}
