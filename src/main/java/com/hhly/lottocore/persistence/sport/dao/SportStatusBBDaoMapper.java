package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.persistence.sport.po.SportStatusBBPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportStatusBBBO;

public interface SportStatusBBDaoMapper {


    int insert(SportStatusBBPO record);


    int updateByPrimaryKey(SportStatusBBPO record);
    
    /**
     * 
     * @Description: 查询多场赛事的 子玩法的状态信息
     * @param ids list集合  对阵自增id
     * @return List SportStatusFBBO
     * @author wuLong
     * @date 2017年3月29日 下午4:54:48
     */
    List<SportStatusBBBO> getBysSportAgainstInfoIds(@Param("sportAgainstInfoIds")List<Long> sportAgainstInfoIds);
}