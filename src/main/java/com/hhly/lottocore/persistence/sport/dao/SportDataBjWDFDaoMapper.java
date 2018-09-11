package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDataBjWDFPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataBjWDFBO;

public interface SportDataBjWDFDaoMapper {

    List<SportDataBjWDFBO> findByAgainstInfoId(Long sportAgainstInfoId);

    int insert(SportDataBjWDFPO record);

    int updateByPrimaryKey(SportDataBjWDFPO record);
}