package com.hhly.lottocore.remote.ordercopy.service.impl;

import java.util.List;
import java.util.Map;

import com.hhly.lottocore.persistence.ordercopy.po.MUserIssueLevelPO;
import com.hhly.skeleton.lotto.base.ordercopy.vo.MUserIssueLevelVO;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.persistence.ordercopy.dao.MUserIssueLevelDaoMapper;
import com.hhly.lottocore.remote.ordercopy.service.MUserIssueLevelService;
import com.hhly.skeleton.base.util.ObjectUtil;
@Service("mUserIssueLevelService")
public class MUserIssueLevelServiceImpl implements MUserIssueLevelService {

	private static Logger logger = LoggerFactory.getLogger(MUserIssueLevelServiceImpl.class);
	
	@Autowired
	private MUserIssueLevelDaoMapper mUserIssueLevelDaoMapper;
	
	@Override
	public Integer getUserIssueLevel(Integer userIssueId) {
		List<Map<Integer,Long>> listMap = mUserIssueLevelDaoMapper.getUserLevel(userIssueId);
		if(!ObjectUtil.isBlank(listMap)){
			return listMap.get(0).get("userLevel").intValue();
		}
		return null;
	}

	/**
	 * 新增用户专家级别
	 *
	 * @param vo
	 * @return
	 */
	@Override
	public int addUserIssueLevel(MUserIssueLevelVO vo) {
		return mUserIssueLevelDaoMapper.insertSelective(new MUserIssueLevelPO(vo));
	}

}
