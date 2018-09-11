package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.persistence.sport.po.SportStatusBJPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportStatusBJBO;

public interface SportStatusBJDaoMapper {

    int insert(SportStatusBJPO record);

    int updateByPrimaryKey(SportStatusBJPO record);
    
    /**
     * 
     * @Description: 查询多场赛事的 子玩法的状态信息
     * @param ids list集合  对阵自增id
     * @return List SportStatusBJBO
     * @author longguoyou
     * @date 2017年3月29日 下午4:54:48
     */
    List<SportStatusBJBO> getBysSportAgainstInfoIds(@Param("sportAgainstInfoIds")List<Long> sportAgainstInfoIds);
}