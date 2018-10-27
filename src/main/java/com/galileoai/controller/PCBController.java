
package com.galileoai.controller;


import com.galileoai.*;
import com.galileoai.ret.ResPcb;
import com.galileoai.ret.ResPlate;
import com.galileoai.utils.Base64Test;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
@Slf4j
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

    @Value("${pcbServiceStart}")
    private String pcbServiceStart;

    @Value("${pcbServiceStop}")
    private String pcbServiceStop;
    @Value("${pcbServiceSearch}")
    private String pcbServiceSearch;


    @ApiOperation(value="查询服务")
    @GetMapping(value = "/service/{port}/status/")
    public Object getStatus(@PathVariable("port") Integer port)throws Exception{

        StreamGobblerCallback.Work work =new StreamGobblerCallback.Work();

        ShellKit.runShell(pcbServiceSearch+" "+port,work);
        while (work.isDoing()){
//            log.info("我");
            Thread.sleep(20);
        }
//        Thread.sleep(500);
        //Active: inactive (dead)
        //Active: active (running)
        if(work.getRes().contains("1"))return "1";
        return "0";
    }

    @ApiOperation(value="开服务")
    @GetMapping(value = "/service/{port}/open/")
    public Object openService(@PathVariable("port") String port)throws Exception {

        StreamGobblerCallback.Work work = new StreamGobblerCallback.Work();
        ShellKit.runShell(pcbServiceStart.replace("port", port), work);
        while (work.isDoing()) {
//            log.info("我");
            Thread.sleep(20);
        }
//        Thread.sleep(500);
        //Active: inactive (dead)
        //Active: active (running)
        if (work.getRes().contains("success")) return "1";
        return "0";
    }

    @ApiOperation(value="关服务")
    @GetMapping(value = "/service/{port}/close/")
    public Object closeService(@PathVariable("port") Integer port)throws Exception{

        StreamGobblerCallback.Work work =new StreamGobblerCallback.Work();

        ShellKit.runShell(pcbServiceStop+" "+port,work);
        while (work.isDoing()){
//            log.info("我");
            Thread.sleep(20);
        }
        if(work.getRes().contains("success"))return "1";
        return "0";
    }

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
            ress=ress.replace("/opt/lampp/htdocs/img","http://111.231.134.58:81/img");
            if(ress.contains("unexpected end of stream on Connection")||ress.contains("Connection reset")||ress.contains("Failed to connect to")){
                logger.info("访问出错:"+ress);
                int i=0;
                while(i<10){
                    i++;
                    ress = MyOkHttpClient.getInstance().get(url);
                    ress=ress.replace("/opt/lampp/htdocs/img","http://111.231.134.58:81/img");
                    if(ress.contains("unexpected end of stream on Connection")||ress.contains("Connection reset")||ress.contains("Failed to connect to")){
                        logger.info("访问出错:"+ress);
                        continue;
                    }
                    else break;
                }
            }
            logger.info("图片检测返回结果:"+ress);
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
