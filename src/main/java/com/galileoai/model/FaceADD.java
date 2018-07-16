package com.galileoai.model;

import lombok.Data;

@Data
public class FaceADD {
    public String face_token;
    public FaceRectangle face_rectangle;
    @Data
    public class FaceRectangle{
        public int width;
        public int top;
        public int left;
        public int height;
    }
}
