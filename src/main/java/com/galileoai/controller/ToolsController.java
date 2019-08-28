
package com.galileoai.controller;


import com.galileoai.MyOkHttpClient;
import com.galileoai.ShellKit;
import com.galileoai.ret.Res;
import com.galileoai.service.BaiduService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by baymin
 * 2017-07-10 23:15
 */
@RestController
@RequestMapping("/tools")
@Validated
@Api(description = "工具类接口")
public class ToolsController {
    private final static Logger logger = LoggerFactory.getLogger(ToolsController.class);
    @Value("${filepath}")
    private String filepath;
    @Value("${shpath}")
    private String shpath;


    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="图片上传")
    @PostMapping(value = "/img")
    @ApiImplicitParam(name = "type", value = "检测点类型", required = true, dataType = "string",paramType = "query")
    public Object userLogin(@RequestParam(value = "type") Integer type,
                            @RequestParam("file") MultipartFile file)throws Exception {
        String ress="";
        try {
            File dir = new File(filepath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String fileAllPath = "";
            String name=System.currentTimeMillis()+".jpg";
            fileAllPath = filepath + name ;
            FileOutputStream out = new FileOutputStream(fileAllPath);
            out.write(bytes);
            out.flush();
            out.close();
            logger.info("开始录制toolsServi");
            String url="";
            if(type==1) {
                //String aa=ShellKit.runShell(shpath+" " + fileAllPath);
                 url = "http://localhost:8088?file=" + URLEncoder.encode("/home/icubic/xie1/" + name, "UTF-8");
            }
            else if(type==2){
                url = "http://localhost:8086?file=" + URLEncoder.encode("/home/icubic/xie1/" + name, "UTF-8");
            }
            else if(type==3){
                url = "http://localhost:8087?file=" + URLEncoder.encode("/home/icubic/xie1/" + name, "UTF-8");
            }
            logger.info("生产url:"+url);
            ress=MyOkHttpClient.getInstance().get(url);
            logger.info("录制没错");
            logger.info(ress);
        } catch (Exception er) {
            er.printStackTrace();
        }
        return ress;
    }
    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="图片上传")
    @GetMapping(value = "/img")
    public Object getimg(@RequestParam("file") MultipartFile file)throws Exception {
        Res res = new Res();
        res.setCode(-1);
        res.setMsg("有误");
        try {
            File dir = new File(filepath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            String fileAllPath = "";
            fileAllPath = filepath + System.currentTimeMillis() + ".jpg";

            FileOutputStream out = new FileOutputStream(fileAllPath);
            out.write(bytes);
            out.flush();
            out.close();

            return out;

        } catch (Exception er) {
            er.printStackTrace();
        }

        return res;
    }
}
