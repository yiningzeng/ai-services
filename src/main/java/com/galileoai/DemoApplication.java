package com.galileoai;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
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
import java.util.List;

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

	public static void main(String[] args) {
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

	@Configuration
	public class CommonConfig {

		@Bean
		public MultipartConfigElement multipartConfigElement() {
			MultipartConfigFactory factory = new MultipartConfigFactory();
			factory.setMaxFileSize(1024L * 1024L *100);
			return factory.createMultipartConfig();
		}
	}
}
