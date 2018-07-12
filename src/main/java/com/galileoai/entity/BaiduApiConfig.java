package com.galileoai.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class BaiduApiConfig {

    @Id
    private String app_id;
    private String api_key;
    private String secret_key;
    private Integer do_num;//执行次数
    private Date update_date;//最后更新时间

}
