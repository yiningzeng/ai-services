
package com.galileoai.controller;


import com.galileoai.*;
import com.galileoai.dao.AiResultDao;
import com.galileoai.entity.AiResult;
import com.galileoai.ret.ResPcb;
import com.galileoai.utils.ExportExcelUtil;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    @Value("${pcbOutNetAIImageBaseUrl}")
    private String pcbOutNetAIImageBaseUrl;
    @Value("${pcbOutNetExportBaseUrl}")
    private String pcbOutNetExportBaseUrl;
    @Value("${pcbRestartShellPath}")
    private String pcbRestartShellPath;

    @Value("${pcbServiceStart}")
    private String pcbServiceStart;

    @Value("${pcbServiceStop}")
    private String pcbServiceStop;
    @Value("${pcbServiceSearch}")
    private String pcbServiceSearch;

    @Value("${excelPath}")
    private String excelPath;

    @Autowired
    private AiResultDao aiResultDao;

    @ApiOperation(value="查询服务")
    @GetMapping(value = "/service/{port}/status/")
    public Object getStatus(@PathVariable("port") Integer port)throws Exception{

        StreamGobblerCallback.Work work =new StreamGobblerCallback.Work();

        ShellKit.runShell(pcbServiceSearch+" "+port,work);
        while (work.isDoing()){
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
        ShellKit.runShell(pcbServiceStart + " " +port, work);
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

    @ApiOperation(value="下载excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "port", required = true, dataType = "string",paramType = "path"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true, dataType = "string", paramType = "query"),
    })
    @GetMapping(value = "/service/{port}/excel")
    public Object downloadExcel(@PathVariable("port") String port,
                                @RequestParam(value = "startTime") String startTime,
                                @RequestParam(value = "endTime") String endTime)throws Exception{
        ExportExcelUtil<AiResult> util = new ExportExcelUtil<AiResult>();

        String fileName = System.currentTimeMillis() + ".xls";

        //id, file_name,num, time, final_label,final_score,result, port, url, create_time
        String[] columnNames = { "ID", "文件名","检测到的数目", "检测用时(秒)", "判别类别", "判别类别置信概率", "检测结果", "检测端点", "在线结果图片","接收图片时间", "完成时间"};
        try {
            util.exportExcel("检测结果", columnNames, aiResultDao.findAll(port, startTime, endTime), new FileOutputStream(excelPath + fileName), ExportExcelUtil.EXCEL_FILE_2003);
            return R.success(pcbOutNetExportBaseUrl + fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return R.error("导出出错");
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
        AiResult aiResult =new AiResult();
        aiResult.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
        ResPcb resPcb = new ResPcb();
        //res.set(-1);
        //res.setMsg("有误");
        try {

//            MultipartFile file=files[0];
//            File dir = new File(pcbpath);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
//            String fileAllPath = "";
//            String name = file.getOriginalFilename() + ".jpg";
//            fileAllPath = pcbpath + name;
//            FileOutputStream out = new FileOutputStream(fileAllPath);
//            out.write(bytes);
//            out.flush();
//            out.close();
//            long now = System.currentTimeMillis();
//
            String url = pcbTestingUrl + ":" + port + "/pandas/";//"?file=" + URLEncoder.encode(pcbpath + name, "UTF-8");

            logger.info("生产url:" + url);

            String ress = MyOkHttpClient.getInstance().aiPost(url,file.getOriginalFilename(),new BASE64Encoder().encode(bytes));
            log.info("检测结果:"+ress);

            if(ress.contains("500 Internal Server Error")||ress.contains("unexpected end of stream on Connection")||ress.contains("Connection reset")||ress.contains("Failed to connect to")){
                logger.info("访问出错:"+ress);
                int i=0;
                while(i<10){
                    i++;
                    Thread.currentThread().sleep(1000);//毫秒
                    ress = MyOkHttpClient.getInstance().aiPost(url,file.getOriginalFilename(),new BASE64Encoder().encode(bytes));
//                    ress=ress.replace("/opt/lampp/htdocs/img","http://111.231.134.58:81/img");
                    if(ress.contains("500 Internal Server Error")||ress.contains("unexpected end of stream on Connection")||ress.contains("Connection reset")||ress.contains("Failed to connect to")){
                        logger.info("访问出错:"+ress);
                        continue;
                    }
                    else break;
                }
            }

//            ress=ress.replace("/opt/lampp/htdocs/img","http://111.231.134.58:81/img");
//            String ress = "{\"process_time\": 1.1531128883361816, \"img_name\": \"1560938728.jpg\", \"num\": 2, \"label_str\":\"OK,NG,0.6\"}";
            resPcb=new Gson().fromJson(ress, ResPcb.class);
            if (resPcb.getNum()>0){
                resPcb.setLabel_str(resPcb.getLabel_str().replace("OK,",""));
            }
            else {
                resPcb.setLabel_str(resPcb.getLabel_str().replace("OK,","others, "));
            }
            if(resPcb.getLabel_str().contains("others")){
                resPcb.setLabel_str("others, ");
            }

            resPcb.setUrl(pcbOutNetAIImageBaseUrl+resPcb.getImg_name());
            resPcb.setFileBeforeName(file.getOriginalFilename());
            resPcb.setId(file.getOriginalFilename());


            aiResult.setFileName(resPcb.getFileBeforeName());
            aiResult.setTime(resPcb.getProcess_time().substring(0,4));
            aiResult.setResult(resPcb.getPoints());
            aiResult.setPort(port);
            try{
                String[] finalR =resPcb.getLabel_str().split(",");
                aiResult.setFinalLabel(finalR[0]);
                aiResult.setFinalScore(finalR[1]);
            }
            catch (Exception e){

            }

            aiResult.setNum(resPcb.getNum().toString());
            aiResult.setUrl(resPcb.getUrl());
            aiResult.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
            aiResultDao.save(aiResult);
            return R.success(resPcb);

//            if(ress.contains("unexpected end of stream on Connection")||ress.contains("Connection reset")||ress.contains("Failed to connect to")){
//                logger.info("访问出错:"+ress);
//                int i=0;
//                while(i<10){
//                    i++;
//                    ress = MyOkHttpClient.getInstance().get(url);
//                    ress=ress.replace("/opt/lampp/htdocs/img","http://111.231.134.58:81/img");
//                    if(ress.contains("unexpected end of stream on Connection")||ress.contains("Connection reset")||ress.contains("Failed to connect to")){
//                        logger.info("访问出错:"+ress);
//                        continue;
//                    }
//                    else break;
//                }
//            }
//            logger.info("图片检测返回结果:"+ress);
//            resPcb=new Gson().fromJson(ress, ResPcb.class);
//            resPcb.setId("");
//            resPcb.setFileBeforeName(file.getOriginalFilename());
//            return R.success(resPcb);
        } catch (Exception er) {
            resPcb.setFileBeforeName(file.getOriginalFilename());
            er.printStackTrace();
            return R.error(resPcb);
        }
    }

}
