package com.galileoai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceCompare {

    //https://console.faceplusplus.com.cn/documents/4887586
    
    public int time_used;
    public float confidence;
    public String image_id1,image_id2,request_id,error_message;
    public List<FaceADD> faces1,faces2;
    public transient Threshold thresholds;



    @Data
    public class Threshold{

        //public float
    }

}
