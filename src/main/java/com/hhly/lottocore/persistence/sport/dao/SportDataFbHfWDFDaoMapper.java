package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDataFbHfWDFPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataFbHfWDFBO;

public interface SportDataFbHfWDFDaoMapper {

    List<SportDataFbHfWDFBO> findByAgainstInfoId(Long sportAgainstInfoId);

    int insert(SportDataFbHfWDFPO record);

    int updateByPrimaryKey(SportDataFbHfWDFPO record);
}