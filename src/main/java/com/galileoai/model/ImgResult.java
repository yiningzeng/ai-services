package com.galileoai.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baymin on 18-9-1:下午3:25
 * email: zengwei@galileo-ai.com
 * -------------------------------------
 * description:
 */
@Data
public class ImgResult {
    @ApiModelProperty("表示此次识别的结果数目")
    private Integer num=0;

    @ApiModelProperty("表示此次识别的结果数目")
    private List<Range> data=new ArrayList<>();

    @Data
    public class Range {
        @ApiModelProperty("左上角点X")
        private Integer tlx;
        @ApiModelProperty("左上角点Y")
        private Integer tly;
        @ApiModelProperty("右下角点X")
        private Integer brx;
        @ApiModelProperty("右下角点Y")
        private Integer bry;
        @ApiModelProperty("标签的下标,可无视")
        private Integer label_num;
        @ApiModelProperty("准确率")
        private float score;
        @ApiModelProperty("标签的说明")
        private String label_str;
    }

}
