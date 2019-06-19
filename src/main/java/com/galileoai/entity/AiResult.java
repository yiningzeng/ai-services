package com.galileoai.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "id", example = "1")
    private Integer id;
    @ApiModelProperty(value = "图片原始文件名", example = "a.jpg")
    private String fileName;
    @ApiModelProperty(value = "缺陷数量", example = "2")
    private String num;
    @ApiModelProperty(value = "检测时间", example = "80ms")
    private String time;
    @ApiModelProperty(value = "判别类别", example = "OK")
    private String finalLabel;
    @ApiModelProperty(value = "判别类别置信概率", example = "0.3")
    private String finalScore;

    @ApiModelProperty(value = "端口", example = "检测点的端口")
    private String port;
    @ApiModelProperty(value = "在线图片")
    private String url;
    @ApiModelProperty(value = "检测时间")
    private Date createTime=new Date();
    @ApiModelProperty(value = "检测结果", example = "每个点的位置信息和标签")
    private String result;
}
