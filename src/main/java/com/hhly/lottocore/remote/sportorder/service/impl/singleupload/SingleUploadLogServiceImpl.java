package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import com.hhly.lottocore.persistence.singleupload.dao.SingleUploadLogDaoMapper;
import com.hhly.lottocore.persistence.singleupload.po.SingleUploadLogPO;
import com.hhly.lottocore.remote.sportorder.service.SingleUploadLogService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 单式上传操作日志服务接口实现
 * @author longguoyou
 * @date 2017年6月10日
 * @compay 益彩网络科技有限公司
 */
public class SingleUploadLogServiceImpl implements SingleUploadLogService {
	
	@Autowired
	private SingleUploadLogDaoMapper singleUploadLogDaoMapper;

	@Override
	public void addSingleUploadLog(SingleUploadLogPO singleUploadLogPO) {
		singleUploadLogDaoMapper.insertSingleUploadLog(singleUploadLogPO);
	}

}
