package com.galileoai.ret;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResPlate {
    @ApiModelProperty(value = "车牌")
    private String number;
    @ApiModelProperty(value = "左上角点x")
    private Integer x=0;
    @ApiModelProperty(value = "左上角点y")
    private Integer y=0;
    @ApiModelProperty(value = "边框长")
    private Integer w=0;
    @ApiModelProperty(value = "边框高")
    private Integer h=0;
}
