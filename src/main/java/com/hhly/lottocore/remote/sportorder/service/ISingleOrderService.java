package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadLogBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadLogVO;

import java.util.List;
import java.util.Map;

/**
 * 单式上传验证服务接口(供前置lotto 通过Hession 调用)
 * @author longguoyou
 * @date 2017年6月13日
 * @compay 益彩网络科技有限公司
 */
public interface ISingleOrderService {
	/**
	 * 验证txt文件投注内容
	 * @author longguoyou
	 * @date 2017年6月27日
	 * @param jczqSingleUploadVO
	 * @param map
	 * @return
	 * @throws Exception
	 */
	ResultBO<?> validateOrder(String filePath, SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map)throws Exception;
	
	/**
	 * 获取第一次到上传文件生成的文件名
	 * @author longguoyou
	 * @date 2017年6月27日
	 * @param userId
	 * @return
	 */
	String getRedisFileName(Integer userId);
	
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年6月28日
	 * @param singleUploadLogVO
	 * @return
	 */
	List<SingleUploadLogBO> findSingleUploadLogInfo(SingleUploadLogVO singleUploadLogVO);
	/**
	 * 单式上传日志
	 * @author longguoyou
	 * @date 2017年7月3日
	 * @param singleUploadJCVO
	 * @param result 验证返回的结果
	 */
    void log(SingleUploadJCVO singleUploadJCVO, ResultBO<?> result);
}
