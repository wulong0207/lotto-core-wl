package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.lottocore.persistence.singleupload.po.SingleUploadLogPO;

/**
 * 单式上传操作日志服务接口
 * @author longguoyou
 * @date 2017年6月10日
 * @compay 益彩网络科技有限公司
 */
public interface SingleUploadLogService{
      void addSingleUploadLog(SingleUploadLogPO singleUploadLogPO);
}
