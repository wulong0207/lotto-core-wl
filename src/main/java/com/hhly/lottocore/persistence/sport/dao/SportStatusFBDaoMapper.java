package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.persistence.sport.po.SportStatusFBPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportStatusFBBO;

public interface SportStatusFBDaoMapper {

    int insert(SportStatusFBPO record);

    int updateByPrimaryKey(SportStatusFBPO record);
    /**
     * 
     * @Description: 查询多场赛事的 子玩法的状态信息
     * @param ids list集合  对阵自增id
     * @return List SportStatusFBPO
     * @author wuLong
     * @date 2017年3月29日 下午4:54:48
     */
    List<SportStatusFBBO> getBysSportAgainstInfoIds(@Param("sportAgainstInfoIds")List<Long> sportAgainstInfoIds);
}