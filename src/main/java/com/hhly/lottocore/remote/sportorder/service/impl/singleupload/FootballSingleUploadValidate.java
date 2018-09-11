package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import com.hhly.lottocore.remote.sportorder.service.AbstractLotteryValidator;
import com.hhly.lottocore.remote.sportorder.service.FootballSelectedMacthService;
import com.hhly.lottocore.remote.sportorder.service.IncludeMatchService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SingleUploadEnum.UploadType;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @desc 竞彩足球 单式上传相关验证
 * @author YiJian
 * @date 2017年6月7日 下午2:31:40
 * @company 深圳益彩网络科技有限公司
 * @version v1.0
 */
@Component
public class FootballSingleUploadValidate extends AbstractLotteryValidator{

	@Autowired
    private IncludeMatchService includeMatchService;
	
	@Autowired
	private FootballSelectedMacthService selectedMatchService;

	public ResultBO<?> handle(SingleUploadJCVO jczqSingleUploadVO, Map<String, Object> map) throws Exception{
		ResultBO<?> result = super.verifyOrderRequired(jczqSingleUploadVO);
		if(result.isError()){return result;}
		UploadType uploadType = UploadType.getUploadType(jczqSingleUploadVO.getUploadType());
		switch(uploadType){
		case SELECTED:
			return selectedMatchService.validate(jczqSingleUploadVO, map);
		case INCLUDE:
			return includeMatchService.validate(jczqSingleUploadVO, map);
		}
		return null;
	}
}
