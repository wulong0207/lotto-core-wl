package com.hhly.lottocore.persistence.sport.dao;

import java.util.List;

import com.hhly.lottocore.persistence.sport.po.SportDrawOldInfoPO;
import com.hhly.skeleton.cms.sportmgr.bo.SportDataFbOldExcelBo;
import com.hhly.skeleton.cms.sportmgr.bo.SportOldLotteryInfoBO;
import com.hhly.skeleton.cms.sportmgr.vo.SMGLotteryParamVO;


public interface SportDrawOldInfoDaoMapper {

    int deleteByPrimaryKey(Long sportDrawOldInfoId);

    int insert(SportDrawOldInfoPO record);

    int countByParamVO(SMGLotteryParamVO vo);

    List<SportOldLotteryInfoBO> selectByParamVO(SMGLotteryParamVO vo);

    int updateByPrimaryKey(SportDrawOldInfoPO po);
    
    List<SportDataFbOldExcelBo> findAllList(SMGLotteryParamVO vo);
    
    List<SportOldLotteryInfoBO> findOldOrderMatchDetail(SMGLotteryParamVO vo);
    
}