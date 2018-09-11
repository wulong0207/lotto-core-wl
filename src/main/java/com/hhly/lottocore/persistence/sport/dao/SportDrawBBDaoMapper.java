package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

import com.hhly.lottocore.persistence.sport.po.SportDrawBBPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportBBDataExcelBO;
import com.hhly.skeleton.cms.sportmgr.bo.SportBBLotteryInfoBO;
import com.hhly.skeleton.cms.sportmgr.vo.SMGLotteryParamVO;

public interface SportDrawBBDaoMapper {

    List<SportBBLotteryInfoBO> findBBData(SMGLotteryParamVO vo);

    Integer findBBDataCount(SMGLotteryParamVO vo);

    int insert(SportDrawBBPO record);

    int updateByPrimaryKey(SportDrawBBPO record);
    
    List<SportBBDataExcelBO> findAllList(SMGLotteryParamVO vo);
    
    @MapKey("systemCode")
    Map<String, SportBBLotteryInfoBO> findBbOrderMatchDetail(SMGLotteryParamVO vo);
}