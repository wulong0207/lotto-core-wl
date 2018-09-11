package com.hhly.lottocore.persistence.dic.dao;

import java.util.List;

import com.hhly.skeleton.cms.base.vo.StringVO;
import org.apache.ibatis.annotations.Param;

import com.hhly.lottocore.persistence.dic.po.DicDataDetailPO;
import com.hhly.skeleton.cms.dicmgr.bo.DicDataDetailBO;
import com.hhly.skeleton.cms.dicmgr.vo.DicDataDetailVO;


public interface DicDataDetailDaoMapper {

	List<DicDataDetailBO> findByCode(@Param("code") String code);

	List<DicDataDetailBO> findDicDatail(DicDataDetailVO vo);

	int findTotal(DicDataDetailVO vo);

	int delByPrimaryKey(@Param("id") int id);

	int updByPrimaryKey(DicDataDetailPO po);

	int add(DicDataDetailPO po);

	int delByPrimaryKeyList(@Param("ids") List<String> ids);

	List<DicDataDetailBO> findSimple(@Param("code") String code);
    
	/**
	 * @param dicDataDetailVO
	 *            参数对象
	 * @return 单个查询对象
	 * @desc 查询唯一的数据记录
	 */
	DicDataDetailBO findSingle(DicDataDetailVO dicDataDetailVO);

	/**
	 * @param dicDataDetailVO
	 *            参数对象
	 * @return 多个查询对象（列表）
	 * @desc 查询多条数据记录（列表）
	 */
	List<DicDataDetailBO> findMultiple(DicDataDetailVO dicDataDetailVO);

    /** 
     * 通过ID获取字典Code
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2017-2-13 下午5:51:47
     * @param id
     * @return
     */
    String getdataDetailCode(@Param("id") String id);

	/**
	 * 查询号码段集合
	 * @return
	 */
	List<String>findMobileNumLimitList();
}