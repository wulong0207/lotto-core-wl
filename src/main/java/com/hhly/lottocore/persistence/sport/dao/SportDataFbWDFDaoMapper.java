package com.hhly.lottocore.persistence.sport.dao;


import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDataFbWDFPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataFbWDFBO;

public interface SportDataFbWDFDaoMapper {

    List<SportDataFbWDFBO> findByAgainstInfoId(Long sportAgainstInfoId);

    int insert(SportDataFbWDFPO record);

    int updateByPrimaryKey(SportDataFbWDFPO record);
}