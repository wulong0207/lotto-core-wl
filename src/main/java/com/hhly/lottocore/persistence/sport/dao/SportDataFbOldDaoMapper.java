package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDataFbOldPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataFBOldBO;

public interface SportDataFbOldDaoMapper {

    List<SportDataFBOldBO> findByAgainstInfoId(Long sportAgainstInfoId);

    int insert(SportDataFbOldPO record);

    int updateByPrimaryKey(SportDataFbOldPO record);
}