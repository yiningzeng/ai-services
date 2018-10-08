package com.galileoai.ret;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResPcb {
    @ApiModelProperty(value = "pcb图片的标示")
    private String id;
    @ApiModelProperty(value = "识别个数")
    private Integer num=0;
    @ApiModelProperty(value = "图片之前的名字")
    private String fileBeforeName;
    @ApiModelProperty(value = "结果外网路径")
    private String url;
}
