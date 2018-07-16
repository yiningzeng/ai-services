package com.galileoai.model;

import lombok.Data;

import java.util.List;

@Data
public class FaceDetect {
    public int time_used;
    public String image_id,request_id;
    public List<FaceADD> faces;
}
