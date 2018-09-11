package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDataBjHfWDFPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataBjHfWDFBO;

public interface SportDataBjHfWDFDaoMapper {
    List<SportDataBjHfWDFBO> findByAgainstInfoId(Long sportAgainstInfoId);

    int insert(SportDataBjHfWDFPO record);


    int updateByPrimaryKey(SportDataBjHfWDFPO record);
}