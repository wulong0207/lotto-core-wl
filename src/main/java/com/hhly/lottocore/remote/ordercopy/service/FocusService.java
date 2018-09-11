package com.hhly.lottocore.remote.ordercopy.service;

import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueInfoPO;
import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueLinkPO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.FocusOptEnum.OptEnum;
import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueLinkVO;

/**
 * 抄单-关注服务 Service
 * @author longguoyou
 * @date 2017年9月22日
 * @compay 益彩网络科技有限公司
 */
public interface FocusService {
	 /**
	  * 更新关注:包括新增和删除操作
	  * @author longguoyou
	  * @date 2017年9月26日
	  * @param mUserIssueLinkPO
	  * @param flag 操作标识
	  * @return
	  */
	 ResultBO<?> updateFocus(MUserIssueLinkPO mUserIssueLinkPO, String flag);
	 /**
	  * 更新  发单用户信息表，focus_num 加一 /减 一
	  * @author longguoyou
	  * @date 2017年9月21日
	  * @return
	  */
	 ResultBO<?> updateSummaryOfFocus(MUserIssueInfoPO mUserIssueInfoPO, OptEnum optEnum);
	 /**
	  * 是否存在关注记录
	  * @author longguoyou
	  * @date 2017年9月26日
	  * @param mUserIssueLinkVO
	  * @return
	  */
	 boolean isFocus(MUserIssueLinkVO mUserIssueLinkVO);
	 /**
	  * 分页查询关注记录
	  * @author longguoyou
	  * @date 2017年9月21日
	  * @param mUserIssueLinkVO
	  * @return
	  */
	 ResultBO<?> queryFocusByMUserIssueLinkVO(MUserIssueLinkVO mUserIssueLinkVO);
}
