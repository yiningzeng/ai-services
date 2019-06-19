package com.galileoai.ret;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResPcb {
    @ApiModelProperty(value = "pcb图片的标示")
    private String id;


    @ApiModelProperty(value = "图片之前的名字")
    private String fileBeforeName;
    @ApiModelProperty(value = "结果外网路径")
    private String url;
    @ApiModelProperty(value = "标签")
    private String label_str;

    //ai返回解析
    @ApiModelProperty(value = "检测时间")
    private String points;
    @ApiModelProperty(value = "识别个数")
    private Integer num=0;
    @ApiModelProperty(value = "检测时间")
    private String process_time;
    @ApiModelProperty(value = "ai保存的结果图片名")
    private String img_name;
}
