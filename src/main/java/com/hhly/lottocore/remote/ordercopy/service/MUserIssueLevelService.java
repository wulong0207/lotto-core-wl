package com.hhly.lottocore.remote.ordercopy.service;

import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueLevelVO;

public interface MUserIssueLevelService {
	   /**
	    * 获取用户专家级别
	    * @author longguoyou
	    * @date 2017年10月27日
	    * @param userIssueId 发单用户ID
	    * @return
	    */
       Integer getUserIssueLevel(Integer userIssueId);

    /**
     * 新增用户专家级别
     *
     * @param vo
     * @return
     */
    int addUserIssueLevel(MUserIssueLevelVO vo);
}
