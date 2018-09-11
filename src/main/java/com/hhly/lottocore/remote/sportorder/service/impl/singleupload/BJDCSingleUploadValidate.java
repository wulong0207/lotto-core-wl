package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import com.hhly.lottocore.remote.sportorder.service.AbstractLotteryValidator;
import com.hhly.lottocore.remote.sportorder.service.BJDCIncludeMatchService;
import com.hhly.lottocore.remote.sportorder.service.BJDCSelectedMatchService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SingleUploadEnum.UploadType;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BJDCSingleUploadValidate extends AbstractLotteryValidator {

	/**
	 * 北京单场包含场次
	 */
	@Autowired
    private BJDCIncludeMatchService bjdcIncludeMatchService;
	
	/**
	 * 北京单场选择场次
	 */
	@Autowired
	private BJDCSelectedMatchService bjdcSelectedMatchService;

	public ResultBO<?> handle(SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map) throws Exception{
		ResultBO<?> result = super.verifyOrderRequired(jczqSingleUploadVO);
		if(result.isError()){return result;}
		UploadType uploadType = UploadType.getUploadType(jczqSingleUploadVO.getUploadType());
		switch(uploadType){
		case SELECTED:
			return bjdcSelectedMatchService.validate(jczqSingleUploadVO, map);
		case INCLUDE:
			return bjdcIncludeMatchService.validate(jczqSingleUploadVO, map);
		}
		return null;
	}
}
