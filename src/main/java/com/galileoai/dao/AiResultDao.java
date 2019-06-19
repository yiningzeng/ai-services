package com.galileoai.dao;

import com.galileoai.entity.AiResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface AiResultDao extends JpaRepository<AiResult, Integer> {

    @Query(name = "查询所有数据",value = "select id, file_name,num, time, final_label,final_score, port, url, create_time,result from ai_result where port = ?1 and create_time between ?2 and ?3", nativeQuery = true)
    List<AiResult> findAll(String port, String startTime, String endTime);
}
