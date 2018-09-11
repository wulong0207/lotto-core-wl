package com.hhly.lottocore.persistence.operate.dao;

import java.util.List;

import com.hhly.skeleton.cms.operatemgr.vo.OperateAdVO;
import com.hhly.skeleton.lotto.base.operate.bo.OperateAdLottoBO;

/**
 * @desc    广告图管理
 * @author  Tony Wang
 * @date    2017年2月8日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface OperateAdDaoMapper {
	/**
	 * 查询默认广告信息
	 * @return
	 */
	List<OperateAdLottoBO> findDefaultAd(OperateAdVO vo);
}
