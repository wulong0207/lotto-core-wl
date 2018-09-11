package com.hhly.lottocore.persistence.sport.dao;


import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportTeamInfoPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportTeamInfoBO;
import com.hhly.skeleton.cms.sportmgr.vo.SportTeamInfoVO;

public interface SportTeamInfoDaoMapper {

    int updateByPrimaryKey(SportTeamInfoPO record);

	List<SportTeamInfoBO> find(SportTeamInfoVO sportTeamInfoVO);
}