/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import com.hhly.lottocore.persistence.sport.dao.SportAgainstInfoDaoMapper;
import com.hhly.lottocore.remote.sportorder.service.Validator;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.constants.*;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * 冠军/冠亚军彩种订单验证
 */
@Component("footballGYJOrderValidate")
public class FootballGYJOrderValidate extends FootballOrderBaseValidate implements Validator{

	@Autowired
	private SportAgainstInfoDaoMapper sportAgainstInfoDaoMapper;


	public ResultBO<?> handle(OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO, List<?> list){
		return this.verifyChildBetContent(orderDetailVO, orderInfoVO, list);
	}

	/**
	 *
	 * 老足彩：14场胜平负/任九/四场进球彩/六场半全场 验证处理中心：投注内容
	 * @author longguoyou
	 * @date 2017年2月7日 下午2:55:09
	 * @param orderDetailVO 订单详情
	 * @param orderInfoVO 订单
	 * @param list 限号列表
	 * @return
	 *
	 */
	private ResultBO<?> verifyChildBetContent(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO, List<?> list) {
		String[] betContent = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());
		//1.验证过关方式格式(是否包含下划线，是否是1串1)
		ResultBO<?> resultBO = this.varifyGYJPasswayPattern(betContent);
		if(resultBO.isError()){
			return resultBO;
		}
		List<String> systemCodes = Arrays.asList(orderInfoVO.getBuyScreen().split(SymbolConstants.COMMA));
		List<SportAgainstInfoBO> listAgainstInfoBO = sportAgainstInfoDaoMapper.findSportAgainstInfoBySystemCodes(systemCodes,orderInfoVO.getLotteryCode());
		//2、验证的对阵的赛事状态
		resultBO = this.varifyMatchStatus(listAgainstInfoBO,orderInfoVO.getLotteryCode());
		if(resultBO.isError()){
			return resultBO;
		}
		//3、验证对阵编号合法性
		resultBO = this.verifyGYJSystemCodes(orderInfoVO, orderDetailVO);
		if(resultBO.isError()){
			return resultBO;
		}
		//4、验证单复式
		resultBO = validContentType(orderDetailVO);
		if(resultBO.isError()){
			return resultBO;
		}
		//5、竞技彩：每个方案倍数、注数、金额校验(同投注内容对比.大乐透追号价钱为3元), 限号
		return super.verifyBetContent(betContent, orderDetailVO, orderInfoVO.getLotteryCode(), list,orderInfoVO.getIsSingleOrder());
	}

	/**
	 * 验证单复式
	 * @param orderDetailVO
	 * @return
	 */
	private ResultBO validContentType(OrderDetailVO orderDetailVO) {
		//101000@1.2|101001@1.3^1_1^2
		String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());//解析投注详情
		//投注内容的场次编号
		String[] betContentArr = FormatConversionJCUtil.stringSplitArray(contents[0], SymbolConstants.VERTICAL_BAR, true);
		if(betContentArr.length==1){//一个选项。单式
			if(orderDetailVO.getContentType() != OrderEnum.BetContentType.SINGLE.getValue()){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}else{//多个选项 复式
			if(orderDetailVO.getContentType() != OrderEnum.BetContentType.MULTIPLE.getValue()){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		return ResultBO.ok();
	}


	/**
	 * 验证对阵场次编号合法性：1)存在性。 2)一致性，与投注内容是否一致。并设置到getMatchSet
	 * @param orderInfoVO
	 * @param orderDetailVO
	 * @return
	 */
	private ResultBO<?> verifyGYJSystemCodes(OrderInfoVO orderInfoVO , OrderDetailVO orderDetailVO){
		//101000@1.2|101001@1.3^1_1^2
		String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());//解析投注详情
		//投注内容的场次编号
		String[] betContentArr = FormatConversionJCUtil.stringSplitArray(contents[0], SymbolConstants.VERTICAL_BAR, true);
		Set<String> set = new TreeSet<String >();
		for(String betContent : betContentArr){
			String systemCode = betContent.split(SymbolConstants.AT)[0];
			set.add(systemCode);
		}
		String[] buyScreenArr  = orderInfoVO.getBuyScreen().split(SymbolConstants.COMMA);
		//1、存在性验证 （奖金优化时，会出现方案赛事编号，和传参不完全一致问题）
		if(buyScreenArr.length != set.size()){
			return ResultBO.err(MessageCodeConstants.SYSTEM_CODE_PARAM_ILLEGAL_SERVICE);
		}
		orderInfoVO.setMatchSet(set);
        return ResultBO.ok();
	}



	/**
	 * 验证赛事的销售状态
	 * @return
	 */
	private ResultBO varifyMatchStatus(List<SportAgainstInfoBO> listAgainstInfoBO,Integer lotteryCode){
		ResultBO resultBo = null;
		if(!ObjectUtil.isBlank(listAgainstInfoBO)){
			for(SportAgainstInfoBO sportAgainstInfoBO : listAgainstInfoBO){
				resultBo = super.verifyAgainstInfoStatus(lotteryCode, sportAgainstInfoBO);
				if(resultBo.isError()){
					return resultBo;
				}
			}
		}else {
			return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
		}
		return ResultBO.ok();
	}


	/**
	 * 验证过关方式1串1
	 * @param betContent
	 * @return
	 */
	private  ResultBO<?> varifyGYJPasswayPattern(String[] betContent){
		if(betContent.length >= 2){
			String passway = betContent[1];//只有单关且是1_1
			String passways [] = passway.split(SymbolConstants.UNDERLINE);
			boolean flag = false;
			if(passways.length==2){
				if(passways[0].equals("1") && passways[1].equals("1") ){
					flag = true;
				}
			}
			if(flag == false) {
				return ResultBO.err(MessageCodeConstants.PASS_FORMAT_ILLEGAL_SERVICE);
			}
		}
		return ResultBO.ok();
	}



	@Override
	public ResultBO<?> verifyChildBetContent(OrderDetailVO orderDetailVO, String[] betContent, OrderInfoVO orderInfoVO) {
		return null;
	}
}
