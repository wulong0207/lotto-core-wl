package com.hhly.lottocore.persistence.ordercopy.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueLevelPO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.MUserIssueLevelBO;

public interface MUserIssueLevelDaoMapper {


    int deleteByPrimaryKey(Integer id);

    int insert(MUserIssueLevelPO record);

    int insertSelective(MUserIssueLevelPO record);


    MUserIssueLevelBO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MUserIssueLevelPO record);

    int updateByPrimaryKey(MUserIssueLevelPO record);
    
    /**
     * 获取发单用户专家级别服务接口
     * @author longguoyou
     * @date 2017年10月27日
     * @param id
     * @return
     */
    List<Map<Integer,Long>> getUserLevel(@Param("userIssueId") Integer id);
}