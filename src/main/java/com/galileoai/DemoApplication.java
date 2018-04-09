package com.galileoai;

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
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@RestController
public class DemoApplication {

	@Value("${filepath}")
	private String filepath;
	@Value("${pythonpath}")
	private String pythonpath;//
	//@Value("${kuayu-origin}")

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);
	}


	@PostMapping(value = "/val")
	public Object userUpdatePass(@RequestParam("file") MultipartFile file)throws Exception {
		/*try {
			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();
			Path path = Paths.get(filepath+"test.jpg");
			Files.write(path, bytes);

		} catch (IOException e) {
			e.printStackTrace();
		}*/

		String fileAllPath="";
		try {
			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();

			fileAllPath=filepath+System.currentTimeMillis()+".jpg";

			FileOutputStream out = new FileOutputStream(fileAllPath);
			out.write(bytes);
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		//调用python校验图片
/*		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("print('hello')");*/


若找不到更好的，只能通过存到本地的文件去获取了！！！！
		String res="";
		try {
			//String a=getPara("car").substring(1),b="D34567",c="LJeff34",d="iqngfao";
			//String[] args1=new String[]{ "python", "D:\\pyworkpeace\\9_30_1.py", a, b, c, d };
			//Process pr=Runtime.getRuntime().exec(args1);
			String url="数组结果";
			System.out.println("start;"+url);
			String[] args1 = new String[] { "python", pythonpath,"--img="+fileAllPath};
			Process pr=Runtime.getRuntime().exec(args1);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				res+=line;
				System.out.println(line);
			}
			in.close();
			pr.waitFor();
			System.out.println("end");
		}
		catch (Exception e) {
			e.printStackTrace();
			res=e.getMessage();
		}

		/*return "{\n" +
				"\t\"code\": 0,\n" +
				"\t\"data\": [{\n" +
				"\t\t\t\"name\": \"玫瑰花\",\n" +
				"\t\t\t\"value\": 0.87\n" +
				"\t\t},\n" +
				"\t\t{\n" +
				"\t\t\t\"name\": \"菊花\",\n" +
				"\t\t\t\"value\": 0.77\n" +
				"\t\t}\n" +
				"\t]\n" +
				"}";*/
		return res;
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
