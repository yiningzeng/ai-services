
package com.galileoai.controller;


import com.galileoai.DemoApplication;
import com.galileoai.MyOkHttpClient;
import com.galileoai.R;
import com.galileoai.ShellKit;
import com.galileoai.ret.ResPcb;
import com.galileoai.ret.ResPlate;
import com.galileoai.utils.Base64Test;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by baymin
 * 2017-07-10 23:15
 */
@RestController
@RequestMapping("/pcb")
@Validated
@Api(description = "pcb板识别")
public class PCBController {
    private final static Logger logger = LoggerFactory.getLogger(PCBController.class);
    @Value("${pcbpath}")
    private String pcbpath;
    @Value("${pcbTestingUrl}")
    private String pcbTestingUrl;
    @Value("${pcbResultUrl}")
    private String pcbResultUrl;
    @Value("${pcbRestartShellPath}")
    private String pcbRestartShellPath;



    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="pcb坏点检测")
    @PostMapping(value = "/testing")
    public Object getimg(@RequestParam(value = "port") String port,@RequestParam("file") MultipartFile file)throws Exception {
//        ShellKit.runShell(pcbRestartShellPath);
        ResPcb resPcb = new ResPcb();
        //res.set(-1);
        //res.setMsg("有误");
        try {

//            MultipartFile file=files[0];
            File dir = new File(pcbpath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String fileAllPath = "";
            String name = file.getOriginalFilename() + ".jpg";
            fileAllPath = pcbpath + name;
            FileOutputStream out = new FileOutputStream(fileAllPath);
            out.write(bytes);
            out.flush();
            out.close();
            long now = System.currentTimeMillis();


            String url = pcbTestingUrl+":"+port+"?file=" + URLEncoder.encode(pcbpath + name, "UTF-8");

            logger.info("生产url:" + url);
            String ress = MyOkHttpClient.getInstance().get(url);
            ress=ress.replace("/opt/lampp/htdocs/img","http://pcbdemo.galileo-ai.com:7001/img");

//            ress="{\"num\": 1,url:\"https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png\"}";
            logger.info("图片检测返回结果:"+ress);

            try{
                if(ress.contains("unexpected end of stream on Connection")||ress.contains("Connection reset")||ress.contains("Failed to connect to")){
                    ShellKit.runShell(pcbRestartShellPath);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            resPcb=new Gson().fromJson(ress, ResPcb.class);
            resPcb.setId(name);
            resPcb.setFileBeforeName(file.getOriginalFilename());
            return R.success(resPcb);

        } catch (Exception er) {
            resPcb.setFileBeforeName(file.getOriginalFilename());
            er.printStackTrace();
            return R.error(resPcb);
        }



    }

}
