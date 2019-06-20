
package com.galileoai.controller;


import com.galileoai.MyOkHttpClient;
import com.galileoai.R;
import com.galileoai.ShellKit;
import com.galileoai.model.ImgResult;
import com.galileoai.ret.ResPcb;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by baymin
 * 2017-07-10 23:15
 */
@RestController
@RequestMapping("/shangfei")
@Validated
@Api(description = "商飞检测demo")
@Slf4j
public class ShangFeiController {
    private final static Logger logger = LoggerFactory.getLogger(ShangFeiController.class);
    @Value("${ShangfeiTestingUrl}")
    private String ShangfeiTestingUrl;



    /**
     * 图片分类画框
     * @return
     * @throws Exception
     */
    @ApiOperation(value="商飞图片检查",notes = "检测demo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "img", value = "识别的图片", dataType = "string", paramType = "query"),
           })
    @PostMapping(value = "/testing")
    public Object doAction(@RequestParam(value = "img") String img) {
        String res = MyOkHttpClient.getInstance().aiPost(ShangfeiTestingUrl,img, img);
        log.info("图片检测返回结果->{}", res);
        if (res.contains("{\"num\":-1,")) return R.error("未识别出结果");
        res = res.replace("\"[", "[").replace("]\"", "]").replace("\\\"", "\"");
        try {
            ImgResult imgResult = new Gson().fromJson(res, ImgResult.class);
            List<ImgResult.Range> ranges = imgResult.getData();
            ImgResult.Range tempBigRang=ranges.get(0);
            float bigest=0;
            for (ImgResult.Range r:
                    ranges) {
                float tempBig=(r.getBry()-r.getTly())*(r.getBrx()-r.getTlx());
                if(tempBig>bigest){
                    log.info("检索到面积大的，{}直接替换为{}",bigest,tempBig);
                    bigest=tempBig;
                    tempBigRang=r;
                }
            }
            ranges.clear();
            ranges.add(tempBigRang);
            imgResult.setNum(1);
            imgResult.setData(ranges);
//            //使用lambda表达式过滤出结果并放到ImgResult.data列表里
//            imgResult.setData(imgResult.getData().stream()
//                    .filter((ImgResult.Range b) -> b.getLabel_num().equals(nowAiModel.getLabelNum())&&b.getLabel_str().equals(nowAiModel.getLabelStr()))
//                    .collect(Collectors.toList()));
//            imgResult.setNum(imgResult.getData().size());
            return R.success(imgResult);
        } catch (Exception e) {
            log.error(e.getMessage());
            return R.error(new ImgResult());
        }
    }
}
