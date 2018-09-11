package com.hhly.lottocore.persistence.singleupload.dao;

import java.util.List;

import com.hhly.lottocore.persistence.singleupload.po.SingleUploadLogPO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadLogBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadLogVO;

public interface SingleUploadLogDaoMapper {
	/**
	 * 新增单式上传日志
	 * @author longguoyou
	 * @date 2017年6月10日
	 * @param singleUploadPO
	 * @return
	 */
	int insertSingleUploadLog(SingleUploadLogPO singleUploadPO);
	/**
	 * 查询单式上传日志信息
	 * @author longguoyou
	 * @date 2017年6月28日
	 * @param singleUploadLogVO
	 * @return
	 */
	List<SingleUploadLogBO> findSingleUploadLogInfo(SingleUploadLogVO singleUploadLogVO);
}