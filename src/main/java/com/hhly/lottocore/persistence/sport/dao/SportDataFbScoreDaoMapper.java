package com.hhly.lottocore.persistence.sport.dao;


import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDataFbScorePO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataFbScoreBO;

public interface SportDataFbScoreDaoMapper {

    List<SportDataFbScoreBO> findByAgainstInfoId(Long sportAgainstInfoId);

    int insert(SportDataFbScorePO record);

    int updateByPrimaryKey(SportDataFbScorePO record);
}