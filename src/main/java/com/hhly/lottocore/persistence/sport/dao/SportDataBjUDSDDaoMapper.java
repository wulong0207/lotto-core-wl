package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDataBjUDSDPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataBjUDSDBO;

public interface SportDataBjUDSDDaoMapper {

    List<SportDataBjUDSDBO> findByAgainstInfoId(Long sportAgainstInfoId);

    int insert(SportDataBjUDSDPO record);


    int updateByPrimaryKey(SportDataBjUDSDPO record);
}