package com.hhly.lottocore.persistence.sport.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

import com.hhly.lottocore.persistence.sport.po.SportDrawWFPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportWFDataExcelBO;
import com.hhly.skeleton.cms.sportmgr.bo.SportWFLotteryInfoBO;
import com.hhly.skeleton.cms.sportmgr.vo.SMGLotteryParamVO;

public interface SportDrawWFDaoMapper {

    Integer findCountWFData(SMGLotteryParamVO vo);

    List<SportWFLotteryInfoBO> findWFData(SMGLotteryParamVO vo);

    int insert(SportDrawWFPO record);

    int updateByPrimaryKey(SportDrawWFPO record);
    
    List<SportWFDataExcelBO> findAllList(SMGLotteryParamVO vo);
    
    @MapKey("systemCode")
    Map<String, SportWFLotteryInfoBO> findWfOrderMatchDetail(SMGLotteryParamVO vo);    
}