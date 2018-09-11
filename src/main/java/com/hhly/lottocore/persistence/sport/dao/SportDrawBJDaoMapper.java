package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;

import com.hhly.lottocore.persistence.sport.po.SportDrawBJPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportBJDataExcelBO;
import com.hhly.skeleton.cms.sportmgr.bo.SportBJLotteryInfoBO;
import com.hhly.skeleton.cms.sportmgr.vo.SMGLotteryParamVO;

public interface SportDrawBJDaoMapper {

    int findBJDataCount(SMGLotteryParamVO vo);

    List<SportBJLotteryInfoBO> findBJData(SMGLotteryParamVO vo);

    int insert(SportDrawBJPO record);

    int updateByPrimaryKey(SportDrawBJPO record);
    
    List<SportBJDataExcelBO> findAllList(SMGLotteryParamVO vo);

    @MapKey("systemCode")
    Map<String, SportBJLotteryInfoBO> findBjOrderMatchDetail(SMGLotteryParamVO vo);
}