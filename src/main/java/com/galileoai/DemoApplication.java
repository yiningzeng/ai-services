package com.galileoai;

import com.baidu.aip.ocr.AipOcr;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.galileoai.ret.ResPlate;
import com.galileoai.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import io.swagger.annotations.ApiImplicitParam;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SpringBootApplication
@RestController
public class DemoApplication {

	private final static Logger logger = LoggerFactory.getLogger(DemoApplication.class);
	@Value("${filepath}")
	private String filepath;
	@Value("${pythonpath}")
	private String pythonpath;//
	@Value("${istest}")
	private Integer istest;//
	//@Value("${kuayu-origin}")
//设置APPID/AK/SK
	public static AipOcr client;
	public static final String APP_ID = "11170015";
	public static final String API_KEY = "Bvk4osbTe2QG0vWfC1bZ4fmW";
	public static final String SECRET_KEY = "QTsdvvUTIEjrvd26VaCNpfAONAZGN45f";
	//@Value("${kuayu-origin}")

	public static void main(String[] args) {
		client= new AipOcr(APP_ID, API_KEY, SECRET_KEY);
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);
		Random random=new Random(5);
		for (int i=0;i<100;i++){
			int randNumber = random.nextInt(4) + 1;
			System.out.println(randNumber);}




		/**
		 * eyJ2ZXIiOjEsImhhc2giOiI0NDE5ZDNjZjAyODJlNzE3YjQwZDg2MWY5ODFiZmIxMiIsIm5vbmNlIjoiNWI0NzY2NTllNWQ4ZCIsImV4cGlyZWQiOjE1MzM5OTc5MTN9
		 * eyJ2ZXIiOjEsImhhc2giOiI0NDE5ZDNjZjAyODJlNzE3YjQwZDg2MWY5ODFiZmIxMiIsIm5vbmNlIjoiNWI0NzY2NTllNWQ4ZCIsImV4cGlyZWQiOjE1MzM5OTc5MTN9
		 * eyJ2ZXIiOjEsImhhc2giOiI0NDE5ZDNjZjAyODJlNzE3YjQwZDg2MWY5ODFiZmIxMiIsIm5vbmNlIjoiNWI0NzY2NTllNWQ4ZCIsImV4cGlyZWQiOjE1MzM5OTc5MTN9
		 *  $nonce = uniqid();
		 *     $expired = time() + $expired_add; //单位:秒
		 *
		 *     $app_key = str_replace("0x", "", $app_key);
		 *     $app_key = str_replace(",", "", $app_key);
		 *     if(strlen($app_key) < 32) {
		 *         return false;
		 *     }
		 *     $app_key_32 = substr($app_key, 0, 32);
		 *
		 *     $source = $app_id.$app_key_32.$idname.$nonce.$expired;
		 *     $sum = md5($source);
		 *
		 *     $tokenInfo = [
		 *         'ver' => 1,
		 *         'hash'  => $sum,
		 *         'nonce' => $nonce,
		 *         'expired' => $expired,
		 *     ];
		 *     $token = base64_encode(json_encode($tokenInfo));
		 *     return $token;
		 *
		 *
		 *
		 */









		System.out.println(Utils.getNowDate());

		SpringApplication.run(DemoApplication.class, args);
	}


	@PostMapping(value = "/val")
	public Object userUpdatePass(@RequestParam("file") MultipartFile file)throws Exception {
		try {
			File dir=new File(filepath);
			if(!dir.exists()){
				dir.mkdirs();
			}
			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			String fileAllPath="";
			fileAllPath=filepath+System.currentTimeMillis()+".jpg";

			FileOutputStream out = new FileOutputStream(fileAllPath);
			out.write(bytes);
			out.flush();
			out.close();
			String url="http://47.98.42.128:8081?file="+ URLEncoder.encode(fileAllPath,"UTF-8");
			logger.info("生产url:"+url);
			if(istest==1) {
				url = "http://47.98.42.128:8081?file=%2froot%2ftflearn%2fexamples%2fimages%2ftest%2f1.jpg";
				logger.info("测试url:" + url);
			}
			String res=MyOkHttpClient.getInstance().get(url);

			//{"res": "[[3,5,4,2,1,0]]"}
			logger.info("返回值："+res);
			JsonObject obj = new JsonParser().parse(res).getAsJsonObject();
			String list=obj.get("res").getAsString();
			String[] stringList=list.replace("[[","").split(",",2);

			String finalRes="识别有误";
			switch (stringList[0]){
				case "0":
					finalRes="绿色磁铁";
					break;
				case "1":
					finalRes="红色磁铁";
					break;
				case "2":
					finalRes="重纹理";
					break;
				case "3":
					finalRes="轻纹理";
					break;
				case "4":
					finalRes="安装正确";
					break;
				case "5":
					finalRes="漏装";
					break;
				case "6":
					finalRes="有杯垫";
					break;
				case "7":
					finalRes="漏装杯垫";
					break;
			}
			return R.success(finalRes);
		} catch (IOException e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
	}

	@PostMapping(value = "/plate")
	public Object plate(@RequestParam("file") MultipartFile file)throws Exception {
		ResPlate resPlate=new ResPlate();
		try {
			File dir=new File(filepath);
			if(!dir.exists()){
				dir.mkdirs();
			}
			// Get the file and save it asdsd
			byte[] bytes = file.getBytes();
			String fileAllPath="";
			fileAllPath=filepath+System.currentTimeMillis()+".jpg";

			FileOutputStream out = new FileOutputStream(fileAllPath);
			out.write(bytes);
			out.flush();
			out.close();

			// 初始化一个AipOcr
			client.setConnectionTimeoutInMillis(2000);
			client.setSocketTimeoutInMillis(60000);
			// 传入可选参数调用接口
			HashMap<String, String> options = new HashMap<String, String>();
			options.put("multi_detect", "true");


			// 参数为本地图片路径
			//String image = "test.jpg";
			// = client.plateLicense(image, options);
			//System.out.println(res.toString(2));

			// 参数为本地图片二进制数组
			JSONObject aa= client.plateLicense(file.getBytes(), options);
			//region
			System.out.println(aa.toString(2));
			String json = "{\n" +
					"  \"log_id\": 8670858267801205732,\n" +
					"  \"words_result\": [{\n" +
					"    \"number\": \"皖Q15538\",\n" +
					"    \"vertexes_location\": [\n" +
					"      {\n" +
					"        \"x\": 104,\n" +
					"        \"y\": 1408\n" +
					"      },\n" +
					"      {\n" +
					"        \"x\": 211,\n" +
					"        \"y\": 1415\n" +
					"      },\n" +
					"      {\n" +
					"        \"x\": 208,\n" +
					"        \"y\": 1448\n" +
					"      },\n" +
					"      {\n" +
					"        \"x\": 101,\n" +
					"        \"y\": 1441\n" +
					"      }\n" +
					"    ],\n" +
					"    \"color\": \"blue\",\n" +
					"    \"probability\": [\n" +
					"      0.9762517213821411,\n" +
					"      0.9999984502792358,\n" +
					"      0.9999880790710449,\n" +
					"      0.9999998807907104,\n" +
					"      0.9999996423721313,\n" +
					"      0.9999998807907104,\n" +
					"      0.9999992847442627\n" +
					"    ]\n" +
					"  }]\n" +
					"}";
			//endregion
			try{
				JsonObject jsonObject =new JsonParser().parse(aa.toString()).getAsJsonObject().get("words_result").getAsJsonArray().get(0).getAsJsonObject();
				resPlate.setNumber(jsonObject.get("number").getAsString());
				JsonArray jsonArray= jsonObject.getAsJsonArray("vertexes_location");

				JsonObject pointLeftTop=jsonArray.get(0).getAsJsonObject();
				JsonObject pointRightTop=jsonArray.get(1).getAsJsonObject();
				JsonObject pointRightBottom=jsonArray.get(2).getAsJsonObject();
				JsonObject pointLeftBottom=jsonArray.get(3).getAsJsonObject();


				resPlate.setX(pointLeftTop.get("x").getAsInt());
				resPlate.setY(pointLeftTop.get("y").getAsInt());

				resPlate.setW(pointRightBottom.get("x").getAsInt()-pointLeftTop.get("x").getAsInt());
				resPlate.setH(pointRightBottom.get("y").getAsInt()-pointLeftTop.get("y").getAsInt());
				//if(pointLeftTop.get("x").getAsInt()<)
				return R.success(resPlate);
			}
			catch (Exception er){
				return R.error("");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return R.error(e.getMessage());
		}
	}



	@Configuration
	public class CommonConfig {

		@Bean
		public MultipartConfigElement multipartConfigElement() {
			MultipartConfigFactory factory = new MultipartConfigFactory();
			factory.setMaxFileSize(1024L * 1024L *100);
			return factory.createMultipartConfig();
		}
	}
	/**
	 * 文件上传配置
	 *
	 * @return
	 */
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//  单个数据大小
		factory.setMaxFileSize("10MB"); // KB,MB
		/// 总上传数据大小
		factory.setMaxRequestSize("20MB");
		return factory.createMultipartConfig();
	}
}
