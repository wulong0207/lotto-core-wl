package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import com.hhly.lottocore.remote.sportorder.service.AbstractLotteryValidator;
import com.hhly.lottocore.remote.sportorder.service.FootballOldService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FootballOldSingleUploadValidate extends AbstractLotteryValidator {
	
	@Autowired
	private FootballOldService footballOldService;

	@Override
	public ResultBO<?> handle(SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map) throws Exception {
		ResultBO<?> result = super.verifyOrderRequired(jczqSingleUploadVO);
		if(result.isError()){return result;}
		return footballOldService.validate(jczqSingleUploadVO, map);
	}

}
