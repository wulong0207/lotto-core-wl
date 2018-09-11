package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.SingleUploadEnum.UploadType;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.StringUtil;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;

import java.util.Map;

/**
 * 单式上传各分支彩种验证必须实现该接口
 * @author longguoyou
 * @date 2017年6月13日
 * @compay 益彩网络科技有限公司
 */
abstract public class AbstractLotteryValidator {
	/**
	 * 参数空验证
	 * @author longguoyou
	 * @date 2017年7月14日
	 * @param singleUploadJCVO
	 * @return
	 */
	protected ResultBO<?> verifyOrderRequired(SingleUploadJCVO singleUploadJCVO){
		//token空验证
		if(ObjectUtil.isBlank(singleUploadJCVO.getToken())){
			return ResultBO.err(MessageCodeConstants.USER_TOKEN_IS_NULL_FIELD);
		}
		//彩种空验证
		if(!Lottery.contain(singleUploadJCVO.getLotteryCode())){
			return ResultBO.err(MessageCodeConstants.LOTTERY_CODE_IS_NULL_FIELD);
		}
		//子玩法空验证
		if(ObjectUtil.isBlank(singleUploadJCVO.getLotteryChildCode())){
			return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_IS_NULL_FIELD);
		}
		//上传类型空验证
		if(ObjectUtil.isBlank(singleUploadJCVO.getUploadType())){
			return ResultBO.err(MessageCodeConstants.UPLOADTYPE_IS_NULL_FIELD);
		}
		//是否第一次上传空验证
		if(ObjectUtil.isBlank(singleUploadJCVO.getFlag())){
			return ResultBO.err(MessageCodeConstants.IF_FIRST_TIME_FLAG_IS_NULL_FIELD);
		}
		//文件路径空验证
		if(ObjectUtil.isBlank(singleUploadJCVO.getFilePath())){
			return ResultBO.err(MessageCodeConstants.FILE_PATH_IS_NULL_FIELD);
		}
		//选择赛事信息空验证
		if(UploadType.SELECTED.getValue() == singleUploadJCVO.getUploadType() && ObjectUtil.isBlank(singleUploadJCVO.getSelectedMatchs())){
			Lottery lottery = Lottery.getLottery(Integer.valueOf(singleUploadJCVO.getLotteryCode()));
			switch (lottery) {
			case FB:
				return ResultBO.err(MessageCodeConstants.SELECTED_MATCH_INFO_IS_NULL_FIELD);
			case SFC:
			case ZC_NINE:
			case JQ4:
			case ZC6:
				break;
			case BB:
				return ResultBO.err(MessageCodeConstants.SELECTED_MATCH_INFO_IS_NULL_FIELD);
			case BJDC:
				return ResultBO.err(MessageCodeConstants.SELECTED_MATCH_INFO_IS_NULL_FIELD);
			case SFGG:
				break;
			}
		}
		//验证转换字符不空时，是否重复
		if(!ObjectUtil.isBlank(singleUploadJCVO.getShiftContent()) && StringUtil.isRepeat(singleUploadJCVO.getShiftContent().split(SymbolConstants.COMMA))){
			return ResultBO.err(MessageCodeConstants.SHIFTCONTENT_IS_REPEAT_SERVICE);
		}
		return ResultBO.ok();
	}
	/**
	 * 
	 * @author longguoyou
	 * @date 2017年6月13日
	 * @param lotteryChildCode
	 * @param type
	 * @param map
	 * @return
	 */
	abstract public ResultBO<?> handle(SingleUploadJCVO jczqSingleUploadVO, Map<String,Object> map)throws Exception ;
}
