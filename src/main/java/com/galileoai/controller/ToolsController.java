
package com.galileoai.controller;


import com.galileo_ai.interaction.R;
import com.galileo_ai.interaction.ret.user.UserReturn;
import com.galileo_ai.service.ToolsService;
import com.galileo_ai.service.UserService;
import com.galileo_ai.utils.CreateNamePicture;
import com.galileoai.ShellKit;
import com.galileoai.ret.Res;
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
    public Object userLogin(@RequestParam("file") MultipartFile file)throws Exception {
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

            logger.info("开始录制toolsServi");
            ShellKit.runShell(shpath+" " + fileAllPath);
            logger.info("录制没错");
            res.setCode(0);
            res.setMsg("");

        } catch (Exception er) {
        }



        res.setTime(new Date());
        res.setType(0);//图片类型
        res.setUrl("获取图片的网址");
        res.setX(12);
        res.setY(23);
        res.setW(100);
        res.setH(20);
        return res;
    }
}
