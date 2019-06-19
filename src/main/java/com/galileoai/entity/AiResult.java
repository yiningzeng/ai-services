package com.galileoai.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class AiResult {
    @Id
    private String id;
    @ApiModelProperty(value = "图片原始文件名", example = "a.jpg")
    private String fileName;
    @ApiModelProperty(value = "检测时间", example = "80ms")
    private String time;
    @ApiModelProperty(value = "检测结果", example = "每个点的位置信息和标签")
    private String result;
}
