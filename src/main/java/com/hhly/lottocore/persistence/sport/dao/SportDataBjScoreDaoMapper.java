package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDataBjScorePO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataBjScoreBO;

public interface SportDataBjScoreDaoMapper {

    List<SportDataBjScoreBO> findByAgainstInfoId(Long sportAgainstInfoId);

    int insert(SportDataBjScorePO record);


    int updateByPrimaryKey(SportDataBjScorePO record);
}