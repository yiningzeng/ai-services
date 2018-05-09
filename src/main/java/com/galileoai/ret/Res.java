package com.galileoai.ret;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Res {
    @ApiModelProperty(value = "是否成功字段")
    private Integer code;
    @ApiModelProperty(value = "错误信息")
    private String msg;
    @ApiModelProperty(value = "图片类型")
    private Integer type;
    @ApiModelProperty(value = "生成的图片路径")
    private String url;
    @ApiModelProperty(value = "左上角点的x坐标")
    private Integer x;
    @ApiModelProperty(value = "左上角点的y坐标")
    private Integer y;
    @ApiModelProperty(value = "边框长")
    private Integer w;
    @ApiModelProperty(value = "边框高")
    private Integer h;
    @ApiModelProperty(value = "生成时间")
    private Date time;
}
