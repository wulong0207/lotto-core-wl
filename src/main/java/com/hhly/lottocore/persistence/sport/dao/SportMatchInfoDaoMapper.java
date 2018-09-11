package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportMatchInfoPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportMatchInfoBO;
import com.hhly.skeleton.cms.sportmgr.vo.SportMatchInfoVO;

public interface SportMatchInfoDaoMapper {


    int insert(SportMatchInfoPO record);

    int updateByPrimaryKey(SportMatchInfoPO record);
    
    List<SportMatchInfoBO> find(SportMatchInfoVO sportMatchInfoVO);
}