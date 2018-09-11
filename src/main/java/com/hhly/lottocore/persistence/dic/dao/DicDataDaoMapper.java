package com.hhly.lottocore.persistence.dic.dao;

import java.util.List;

import com.hhly.lottocore.persistence.dic.po.DicDataPO;
import com.hhly.skeleton.cms.dicmgr.bo.DicDataBO;
import com.hhly.skeleton.cms.dicmgr.vo.DicDataVO;

public interface DicDataDaoMapper {
	/**
	 * 分页查询
	 * @param dicDataVO
	 * @return
	 */
	List<DicDataBO> find(DicDataVO dicDataVO);
	/**
	 * 分页查询总条数
	 * @param dicDataVO
	 * @return
	 */
	int findTotal(DicDataVO dicDataVO);
	
	int add(DicDataPO record);

	int updByPrimaryKey(DicDataPO record);

	
   
}