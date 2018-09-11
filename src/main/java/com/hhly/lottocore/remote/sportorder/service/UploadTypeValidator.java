package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;

import java.util.Map;

/**
 * 单式上传各类型(选择场次、包含场次)验证必须实现该接口
 * @author longguoyou
 * @date 2017年6月13日
 * @compay 益彩网络科技有限公司
 */
public interface UploadTypeValidator {
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年6月13日
	 * @param jczqSingleUploadVO
	 * @param map
	 * @return
	 */
	ResultBO<?> validate(SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map)throws Exception ;
}
