package com.galileoai;

import org.python.util.PythonInterpreter;
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

		try {
			// Get the file and save it somewhere
			byte[] bytes = file.getBytes();

			FileOutputStream out = new FileOutputStream(filepath+"test.jpg");
			out.write(bytes);
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		//调用python校验图片
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("print('hello')");
		String res="上传成功";
		try {
			//String a=getPara("car").substring(1),b="D34567",c="LJeff34",d="iqngfao";
			//String[] args1=new String[]{ "python", "D:\\pyworkpeace\\9_30_1.py", a, b, c, d };
			//Process pr=Runtime.getRuntime().exec(args1);
			String url="http://blog.csdn.net/thorny_v/article/details/61417386";
			System.out.println("start;"+url);
			String[] args1 = new String[] { "python", "/home/baymin/test.py", url};
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
		}


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
