package com.galileoai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@AllArgsConstructor
@Data
public class BodyAttr {

    /**
     * {
     * 	"log_id": 7783055380416435166,
     * 	"person_num": 1,
     * 	"person_info": [{
     * 		"attributes": {
     * 			"lower_color": {
     * 				"score": 0.4863463044166565,
     * 				"name": "黑"
     *                        },
     * 			"upper_color": {
     * 				"score": 0.5242728590965271,
     * 				"name": "红"
     *            }        * 		},
     * 		"location": {
     * 			"score": 0.9999798536300659,
     * 			"top": 12,
     * 			"left": 155,
     * 			"width": 219,
     * 			"height": 595
     *        }
     *    }]
     * }
     */
    private long log_id;
    private Integer person_num;
    private List<PersonInfo> person_info;

    @AllArgsConstructor
    @Data
    public class PersonInfo{
        private Attributes attributes;
        private Location location;

        @AllArgsConstructor
        @Data
        public class Attributes{
            private LowerColor lower_color;
            private UpperColor upper_color;
            private Headwear headwear;

            /**
             * 下半身颜色
             */
            @AllArgsConstructor
            @Data
            public class LowerColor{
                private float score;
                private String name;

            }

            /**
             * 上半身颜色
             */
            @AllArgsConstructor
            @Data
            public class UpperColor{
                private float score;
                private String name;

            }

            /**
             * "headwear":{
             *           "name":"无帽",
             *           "score":0.3280346989631653
             *         },
             */
            @AllArgsConstructor
            @Data
            public class Headwear{
                private float score;
                private String name;
            }
        }

        /**
         * 身体位置坐标啥的
         */
        @AllArgsConstructor
        @Data
        public class Location{
            private float score;
            private float top;
            private float left;
            private float width;
            private float height;
        }
    }



}
