package com.hhly.lottocore.persistence.ordercopy.dao;

import java.util.List;

import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueLinkPO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.MUserIssueLinkBO;
import com.hhly.skeleton.lotto.base.ordercopy.bo.QueryUserIssueLinkBO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueLinkVO;
import com.hhly.skeleton.lotto.base.recommend.bo.RcmdAttentionBO;
import com.hhly.skeleton.lotto.base.recommend.vo.RcmdSingleVO;
import org.apache.ibatis.annotations.Param;

public interface MUserIssueLinkDaoMapper {


    int deleteByPrimaryKey(Integer id);

    int insert(MUserIssueLinkPO record);

    int insertSelective(MUserIssueLinkPO record);


    MUserIssueLinkBO selectByPrimaryKey(Integer id);
    
    List<QueryUserIssueLinkBO> selectByCondition(MUserIssueLinkVO MUserIssueLinkVO);
    
    int selectByConditionCount(MUserIssueLinkVO MUserIssueLinkVO);

    int updateByPrimaryKeySelective(MUserIssueLinkPO record);

    int updateByPrimaryKey(MUserIssueLinkPO record);

    int selectCountByUserId(@Param("userId") Integer userId, @Param("userIssueId") Long userIssueId);

    List<RcmdAttentionBO> queryMyAttentionList(RcmdSingleVO rcmdSingleVO);

    Integer queryMyAttentionCount(RcmdSingleVO rcmdSingleVO);
}