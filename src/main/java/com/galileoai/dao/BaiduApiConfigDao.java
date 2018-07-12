package com.galileoai.dao;

import com.galileoai.entity.BaiduApiConfig;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface BaiduApiConfigDao extends JpaRepository<BaiduApiConfig, String> {


    @Query(value = "select * from baidu_api_config where do_num<500 ORDER BY update_date DESC LIMIT 1", nativeQuery = true)
    BaiduApiConfig findByDo_numLessThanAndMyself(Integer doNum);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE baidu_api_config SET do_num=0,update_date=?1 WHERE update_date<?1", nativeQuery = true)
    int updateConfig(String today);


    //BaiduApiConfig findByDo_numLessThan(Integer do_num);
    //List<BaiduApiConfig> findByDo_numLessThan(Integer do_num);
}
