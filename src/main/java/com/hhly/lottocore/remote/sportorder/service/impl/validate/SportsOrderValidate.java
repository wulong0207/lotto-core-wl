package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberDetailBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LimitNumberInfoBO;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.persistence.sport.dao.SportAgainstInfoDaoMapper;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.base.util.RedisUtil;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.OrderEnum;
import com.hhly.skeleton.base.common.OrderEnum.BetContentType;
import com.hhly.skeleton.base.common.SportEnum;
import com.hhly.skeleton.base.common.SportEnum.SportTabTypeEnum;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCConstants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.sportsutil.SportsZsUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LotChildBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;

/**
 * 
 * @author longguoyou
 * @date 2017年3月3日 上午11:07:48
 * @desc 竞技彩公共验证方法集
 */
@Component("sportsOrderValidate")
public class SportsOrderValidate  {

	private static Logger logger = LoggerFactory.getLogger(SportsOrderValidate.class);
	private static String ONE_ONE = "1_1";//1串1
	private static String TWO_ONE = "2_1";//2串1

	@Autowired
	private SportAgainstInfoDaoMapper sportAgainstInfoDaoMapper;
	
	@Autowired
	public RedisUtil redisUtil;
	
	@Resource(name="jcDataService")
	private IJcDataService jcDataService;


	/**
	 * 限号
	 * @param list
	 * @return
	 */
	public ResultBO<?> limitlotteryCode(OrderDetailVO orderDetailVO,List<?> list,String[] betContent,int lotteryCode,Short isSingleOrder){
		if(betContent[0].contains(SymbolConstants.NUMBER_SIGN)){
			betContent[0] = betContent[0].replace(SymbolConstants.NUMBER_SIGN,SymbolConstants.VERTICAL_BAR);
		}
		//1、如果有胆码，先判断，胆码投注内容是否是限号的内容;除了过关方式与选择总场次一致外，如4场比赛，4_1
        if(betContent[0].contains(SymbolConstants.NUMBER_SIGN)){
            String passways = betContent[1];
            //胆投注内容是否包含限号，非胆投注内容是否包含限号
            boolean flagDan = false,flagNonDan = false;
            Integer counter = 0;
            List<LimitNumberInfoBO> listLimit = (List<LimitNumberInfoBO>)list;//限号内容
			Map<String,String> limitDanMap = getLimitDanMap(betContent[0],null);
			Map<String,String> limitNonDanMap = getLimitDanMap(betContent[0],passways.split(SymbolConstants.COMMA));
            if(passways.contains(SymbolConstants.COMMA)){
				flagDan = judeInclude(counter,listLimit,limitDanMap,orderDetailVO,false);
		    }else{
                if(!(passways.length() == Constants.NUM_3 && Integer.valueOf(passways.substring(Constants.NUM_0,Constants.NUM_1)) ==
					   orderDetailVO.getBuyScreen().split(SymbolConstants.COMMA).length)){
				    flagDan = judeInclude(counter,listLimit,limitDanMap,orderDetailVO,false);
			    }
		    }
		    flagNonDan = judeInclude(counter,listLimit,limitNonDanMap,orderDetailVO,true);
            //包含限号
		    if(!flagDan && !flagNonDan){
                return ResultBO.err(MessageCodeConstants.LOTTERY_LIMIT_SERVICE);
			}
		}else{//2、无胆码
			if(JCZQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode()) || BJDCConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
				if(isSingleOrder == Constants.NUM_1){return ResultBO.ok();}
				Map<String,String> limitMap = getLimitMap(betContent);
				if(ObjectUtil.isBlank(list)){return ResultBO.ok();}
				List<LimitNumberInfoBO> listLimit = (List<LimitNumberInfoBO>)list;//限号内容
				for(LimitNumberInfoBO limitBO : listLimit){
					List<LimitNumberDetailBO> listLimitDetailBO = limitBO.getLimitNumberList();
					for(LimitNumberDetailBO detailBO : listLimitDetailBO){
						boolean flagContent = true;
						if(detailBO.getLotteryChildCode().equals(orderDetailVO.getLotteryChildCode())){
							//判断内容是否限号
							String limitContent = detailBO.getLimitContent();
							String[] limitCont  = FormatConversionJCUtil.singleBetContentAnalysis(limitContent);
							if(!ObjectUtil.isBlank(limitCont) && limitCont.length != Constants.NUM_2){
								if(logger.isErrorEnabled()){
									logger.error("配置限号的内容不符合规则["+limitContent+"]");
								}
								return ResultBO.ok();
							}
							if(limitMap.containsValue(limitCont[1])){//过关方式匹配
								String[] limitBet = FormatConversionJCUtil.betContentDetailsAnalysis(limitCont[0]);
								for(String bet : limitBet){//[1807065057(3)],[1807076060(3)],[1807065058(0)]
									if(!limitMap.containsValue(bet)){
										flagContent = false;
										break;
									}
								}
							}else{//过关方式不匹配
								flagContent = false;
							}
							if(!flagContent){
								if(logger.isDebugEnabled()) {
									logger.debug("限号内容[" + limitContent + "]检验完毕,继续下一个限号内容循环！");
								}
								continue;
							}
							if(logger.isInfoEnabled()){
								logger.info("方案内容limitMap="+limitMap);
								logger.info("限号内容limitContent="+limitContent);
							}
							if(flagContent){
								String tips = translate(detailBO.getLimitContent(),lotteryCode);
								return ResultBO.err(MessageCodeConstants.LOTTERY_LIMIT_SERVICE,tips);
							}
						}
					}
				}
			}
			//暂不支持1807065057(3,1)复式限号
//			if(bet.contains(SymbolConstants.COMMA)){
//				String preString = bet.substring(0,bet.indexOf(SymbolConstants.PARENTHESES_LEFT)+Constants.NUM_1);
//				String options = bet.substring(bet.indexOf(SymbolConstants.PARENTHESES_LEFT)+Constants.NUM_1,bet.indexOf(SymbolConstants.PARENTHESES_RIGHT));//多个选项情况
//				for(String option : options.split(SymbolConstants.COMMA)){
//					StringBuffer stringBuffer = new StringBuffer();
//					stringBuffer.append(preString).append(option).append(SymbolConstants.PARENTHESES_RIGHT);
//					if(!limitMap.containsValue(stringBuffer.toString())){
//						flagContent = false;
//						break;
//					}
//				}
//			}else{
//			}
		}
		return ResultBO.ok();
	}

	/**
	 * 判断限号内容是否胆码投注内容
	 * @param counter 需查找次数
	 * @param listLimit
	 * @param limitDanMap
	 * @param orderDetailVO
	 * @param flag 是否非胆
	 * @return
	 */
	private static boolean judeInclude(Integer counter,List<LimitNumberInfoBO> listLimit, Map<String, String> limitDanMap,OrderDetailVO orderDetailVO,boolean flag) {
		int foundDan = 0, foundNonDan = 0;//胆区、非胆区，需查找到限号内容次数
		for(LimitNumberInfoBO limitBO : listLimit) {
			List<LimitNumberDetailBO> listLimitDetailBO = limitBO.getLimitNumberList();
			for(LimitNumberDetailBO detailBO : listLimitDetailBO){
				if(detailBO.getLotteryChildCode().equals(orderDetailVO.getLotteryChildCode())){
					String limitContent = detailBO.getLimitContent();
					String[] limitCont  = FormatConversionJCUtil.singleBetContentAnalysis(limitContent);
					String[] limitBet = FormatConversionJCUtil.betContentDetailsAnalysis(limitCont[0]);
					if(!flag){
						foundDan = limitDanMap.size();
						counter = limitBet.length - limitDanMap.size();//非胆区需查找次数
						//处理逻辑
						for(String bet: limitBet){
							if(limitDanMap.containsValue(bet)){
								foundDan--;
							}
						}
					}else{
						foundNonDan = counter;
						for(String bet : limitBet){
							if(limitDanMap.containsValue(bet)){
								foundNonDan--;
							}
						}
					}
                    if((!flag && foundDan == 0) || (flag && foundNonDan == 0)){//全归零，证明符合条件
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Map<String,String> getLimitDanMap(String betContent,String[] passways) {
		//1807032102_S(3@1.75,1@3.70)|1807043101_R[-1](3@3.75,1@3.45)#1807043102_S(3@2.49)|1807043103_S(3@2.08)^3_1^1
		String[] danContent = FormatConversionJCUtil.singleMatchDanBetContentAnalysis(betContent);//解析单个赛事投注
		String[] danBetContent = FormatConversionJCUtil.betContentDetailsAnalysis(ObjectUtil.isBlank(passways)?danContent[0]:danContent[1]);//解析胆码或者其它投注
		int i = 1,j = 1;
		Map<String,String> retDanMap = new HashMap<String,String>();
		for(String betOne : danBetContent){
			String systemCode = betOne.substring(0,betOne.indexOf(SymbolConstants.PARENTHESES_LEFT));
			String betAndSps = betOne.substring(betOne.indexOf(SymbolConstants.PARENTHESES_LEFT) + 1, betOne.indexOf(SymbolConstants.PARENTHESES_RIGHT));
			String[] options = FormatConversionJCUtil.optionBetContentAnalysis(betAndSps);
			for(String bet : options){
				StringBuilder strBuffer = new StringBuilder();
				strBuffer.append(systemCode).append(SymbolConstants.PARENTHESES_LEFT);//1807032102(
				String[] betAndSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(bet);
				strBuffer.append(betAndSp[0]).append(SymbolConstants.PARENTHESES_RIGHT);//1807032102(3)
				retDanMap.put("BET_DAN_"+i,strBuffer.toString());
				i++;
			}
		}
		if(!ObjectUtil.isBlank(passways)){
			for(String pass : passways){
				retDanMap.put("PASS_DAN_"+j, pass);
				j++;
			}
		}
		return retDanMap;
	}

	/**
	 * 组装投注内容: 区分单一玩法/混投，目前将胆码当非胆码处理
	 * @param betContent 原始投注内容
	 * @return
	 */
	private static Map<String,String> getLimitMap(String[] betContent) {
		String betStr = betContent[0];
		int i = 1,j = 1;
		Map<String,String> retMap = new HashMap<String,String>();
		//限号内容格式：1708314010(0)|1708314021(3)^2_1
		//有胆的先转普通投注
		if(betContent[0].contains(SymbolConstants.NUMBER_SIGN)) {
			betStr = betContent[0].replaceAll(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
		}
		if(!betStr.contains(SymbolConstants.UNDERLINE)){//单一玩法： 1807032102(3@1.71,1@3.70)|1807043101(3@1.85)|1807043102(3@2.49)^3_1^1
			String[] betContentOne = FormatConversionJCUtil.betContentDetailsAnalysis(betStr);
			for(String betOne : betContentOne){
				String systemCode = betOne.substring(0,betOne.indexOf(SymbolConstants.PARENTHESES_LEFT));
				String betAndSps = betOne.substring(betOne.indexOf(SymbolConstants.PARENTHESES_LEFT) + 1, betOne.indexOf(SymbolConstants.PARENTHESES_RIGHT));
				String[] options = FormatConversionJCUtil.optionBetContentAnalysis(betAndSps);
				for(String bet : options){
					StringBuilder strBuffer = new StringBuilder();
					strBuffer.append(systemCode).append(SymbolConstants.PARENTHESES_LEFT);//1807032102(
					String[] betAndSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(bet);
					strBuffer.append(betAndSp[0]).append(SymbolConstants.PARENTHESES_RIGHT);//1807032102(3)
					retMap.put("BET_"+i,strBuffer.toString());
					i++;
				}
			}
		}else{//混投：1807065057_S(3@4.20)|1807076060_S(3@3.35)_R[+1](0@4.55)|1807065058_S(0@3.35)_R[-1](3@3.70)
			String[] betContentOne = FormatConversionJCUtil.betContentDetailsAnalysis(betStr);
			for(String betOne : betContentOne){
				String[] strs = betOne.split(SymbolConstants.UNDERLINE);
				for(int k = 1; k<strs.length ;k ++){
					String betAndSps = strs[k].substring(strs[k].indexOf(SymbolConstants.PARENTHESES_LEFT) + 1, strs[k].indexOf(SymbolConstants.PARENTHESES_RIGHT));//3@1.71,1@3.70
					String[] options = FormatConversionJCUtil.optionBetContentAnalysis(betAndSps);//[3@1.71][1@3.70]
					for(String bet : options){
						StringBuilder strBuilder = new StringBuilder();
						strBuilder.append(strs[0]).append(SymbolConstants.UNDERLINE);//1807032102_
						strBuilder.append(strs[k].substring(0,strs[k].indexOf(SymbolConstants.PARENTHESES_LEFT)));//1807032102_S  TODO: 要不要让球数？
						strBuilder.append(SymbolConstants.PARENTHESES_LEFT);//1807032102_S(
						String[] betAndSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(bet);
						strBuilder.append(betAndSp[0]).append(SymbolConstants.PARENTHESES_RIGHT);//1807032102_S(3)
						retMap.put("BET_"+i,strBuilder.toString());
						i++;
					}
				}
			}
		}
		//过关方式：2_1,3_1
		String[] passway = betContent[1].split(SymbolConstants.COMMA);
		for(String pass : passway){
			retMap.put("PASS_"+j, pass);
			j++;
		}
		return retMap;
	}

	public static void main(String[] args) {
		String betOne = "1807032102(3@1.71,1@3.70)|1807043101(3@1.85)|1807043102(3@2.49)^3_1^1";
		String betDan = "1807065057_S(3@4.20)#1807076060_S(3@3.35,0@2.13)_R[+1](0@4.55)|1807065058_S(0@3.35)_R[-1](3@3.70)^3_1^1";
		String[] betContent = FormatConversionJCUtil.singleBetContentAnalysis(betOne);
//		Map<String,String> map = getLimitMap(betContent);
		/*Map<String,String> danMap = getLimitDanMap(betContent[0],betContent[1].split(SymbolConstants.COMMA));
		//List<LimitNumberInfoBO> listLimit, Map<String, String> limitDanMap,OrderDetailVO orderDetailVO,boolean flag
		List<LimitNumberInfoBO> listLimit = new ArrayList<LimitNumberInfoBO>();
		LimitNumberInfoBO infoBO = new LimitNumberInfoBO();
		List<LimitNumberDetailBO> detail = new ArrayList<LimitNumberDetailBO>();
		LimitNumberDetailBO detailBO = new LimitNumberDetailBO();
		detailBO.setLimitContent("1807032102_S(3)|1807043102_S(3)^2_1");
		detailBO.setLotteryChildCode(30001);
		detail.add(detailBO);
		infoBO.setLimitNumberList(detail);
		listLimit.add(infoBO);
		OrderDetailVO orderDetailVO = new OrderDetailVO();
		orderDetailVO.setLotteryChildCode(30001);
		boolean flag = judeInclude(0,listLimit,danMap,orderDetailVO,true);
		System.out.println(betOne.indexOf(SymbolConstants.PARENTHESES_LEFT));*/
//		System.out.println(map);
		String info = "123[+1]sdd";
		System.out.println(info.substring(info.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT),info.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)));
	}


	/**
	 * 竞技彩：每个方案倍数、注数、金额校验(同投注内容对比.大乐透追号价钱为3元), 限号
	 * @author longguoyou
	 * @date 2017年3月3日 上午10:46:34
	 * @param orderDetailVO
	 * @param lotteryCode
	 * @param list 限号列表
	 * @return
	 */
	protected ResultBO<?> verifyBetContent(String[] betContent, OrderDetailVO orderDetailVO, int lotteryCode, List<?> list,Short isSingleOrder){
		//1.限号
		ResultBO<?> result = limitlotteryCode(orderDetailVO,list,betContent,lotteryCode,isSingleOrder);
		if(result.isError()){
			return result;
		}
		//2.解析返回投注注数
		return this.verifyBetNumBoundary(lotteryCode, orderDetailVO, betContent,isSingleOrder);
	}

	/**
	 * 获取场次编号，多个场次以 "," 分割
	 * @param betContent 
	 * @param contentType 单式、复式 或 胆拖
	 * @param lotteryChildCode 子玩法
	 * @return
	 */
	private String singleGameCode(String[] betContent, Integer contentType, Integer lotteryChildCode){
		if(!ObjectUtil.isBlank(lotteryChildCode)){
			String lotteryCode = String.valueOf(lotteryChildCode).substring(0, 3);
			LotteryEnum.Lottery lottery = LotteryEnum.Lottery.getLottery(Integer.valueOf(lotteryCode));
			switch (lottery) {
			case BJDC:
			case SFGG:
			//竞技彩
			case BB:
			case FB:
				String gameContent = betContent[0];
				String gameDetails[] = FormatConversionJCUtil.betContentDetailsAnalysis(gameContent);// "|"线分隔
				StringBuffer gameCodeStr = new StringBuffer();
				for(String gameDetail : gameDetails){
					if(gameDetail.contains(SymbolConstants.NUMBER_SIGN)){
						gameDetail = gameDetail.replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
					}
					String systemCode = getSystemCode(gameDetail, contentType, lotteryChildCode);
					if(!ObjectUtil.isBlank(systemCode)){
						gameCodeStr.append(systemCode).append(SymbolConstants.COMMA);
					}
				}
				return gameCodeStr.substring(0, gameCodeStr.length() - 1);
			default:
				break;
			}
		}
		return null;
	}
	/**
	 * 解析处理混投和普通投注投注内容
	 * @author longguoyou
	 * @date 2017年5月16日
	 * @param gameDetail
	 * @param contentType
	 * @param lotteryChildCode
	 * @return
	 */
	private String getSystemCode(String gameDetail, Integer contentType, Integer lotteryChildCode){
		String systemCode = SymbolConstants.ENPTY_STRING;
		if(!ObjectUtil.isBlank(lotteryChildCode)){
			if(lotteryChildCode == JCZQConstants.ID_FHT || lotteryChildCode == JCLQConstants.ID_JCLQ_HHGG){//竞彩混合
				systemCode = FormatConversionJCUtil.stringSplitArray(gameDetail, SymbolConstants.UNDERLINE, true)[0];
			}else {
				//让分胜平负和大小分
				String systemCodestr[] = FormatConversionJCUtil.stringSplitArray(gameDetail, SymbolConstants.MIDDLE_PARENTHESES_LEFT, true);
				if(systemCodestr.length >= 2){//是让分胜负或者大小分
					systemCode = FormatConversionJCUtil.stringSplitArray(gameDetail, SymbolConstants.MIDDLE_PARENTHESES_LEFT, true)[0];
				}else{//其他玩法
					systemCode = FormatConversionJCUtil.stringSplitArray(gameDetail, SymbolConstants.PARENTHESES_LEFT, true)[0];
				}
			}
		}
		return systemCode;
	}

	/**
	 * 验证过关方式内容
	 * @author longguoyou
	 * @date 2017年2月21日 下午7:06:56
	 * @param betContent
	 * @param  orderInfoVO
	 * @param detailBetContent
	 * @return
	 */
	protected ResultBO<?> varifyPasswayContent(String[] betContent, OrderInfoVO orderInfoVO, String detailBetContent){
		//1.选了几场赛事
		//2.获取过关串
		//3.过关串的命中场次不能大于赛事场数
		//4.如果有定胆的话，过关串的命中场次必须小于等于赛事场数
		String content = betContent[0];
		String passWays = betContent[1];
		boolean isSingleWay = false;
		//北单会有单关和其它过关方式混合过关情况
		if(!BJDCConstants.checkLotteryId(orderInfoVO.getLotteryChildCode())){
			if(passWays.length() == 3 && passWays.equals(ONE_ONE)){
				isSingleWay = true;
			}
//			if(!isSingleWay && passWays.contains(ONE_ONE)){//TODO 1_1,2_1,3_1 过关情况改造处
//				return ResultBO.err(MessageCodeConstants.PASS_CONTENT_ILLEGAL_SERVICE);
//			}
			}
		Set<String> setMatch = new HashSet<String>();
		getMatchIds(setMatch, detailBetContent);
		int matchNum = setMatch.size();//4
		int danNum = 0;//1
		if(content.contains(SymbolConstants.NUMBER_SIGN)){
			danNum = content.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.NUMBER_SIGN)[0].split(SymbolConstants.DOUBLE_SLASH+SymbolConstants.VERTICAL_BAR).length;
		}
		for(String passway : passWays.split(SymbolConstants.COMMA)){
			int mzcc = Integer.valueOf(passway.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.UNDERLINE)[0]);
			if(danNum > 0){
				if(!(mzcc > danNum && mzcc <= matchNum)){
					return ResultBO.err(MessageCodeConstants.PASS_CONTENT_ILLEGAL_SERVICE);
				}
			}else{
				if(mzcc > matchNum){
					return ResultBO.err(MessageCodeConstants.PASS_CONTENT_ILLEGAL_SERVICE);
				}
			}
		}
		if(orderInfoVO.getTabType() == SportEnum.SportTabTypeEnum.TWO_AND_ONE.getCode()){//2选1
			if(passWays.length() == 3 && passWays.equals(ONE_ONE)){
				return ResultBO.err(MessageCodeConstants.PASS_CONTENT_ILLEGAL_SERVICE);
			}
		}else if(orderInfoVO.getTabType() == SportEnum.SportTabTypeEnum.SINGLE_BET.getCode()){//单关 1_1
			if(passWays.length() != 3 && !passWays.equals(ONE_ONE)){
				return ResultBO.err(MessageCodeConstants.PASS_CONTENT_ILLEGAL_SERVICE);
			}
		}else if(orderInfoVO.getTabType() == SportEnum.SportTabTypeEnum.SINGLE_WIN.getCode()){//单场致胜
			if(passWays.length() != 3 && !passWays.equals(TWO_ONE)){
				return ResultBO.err(MessageCodeConstants.PASS_CONTENT_ILLEGAL_SERVICE);
			}
		}else if(orderInfoVO.getTabType() == SportEnum.SportTabTypeEnum.PASS_WAY_BET.getCode()){//过关投注
			if(passWays.length() == 3 && passWays.equals(ONE_ONE)){
				return ResultBO.err(MessageCodeConstants.PASS_CONTENT_ILLEGAL_SERVICE);
			}
		}
		return ResultBO.ok();
	}
	
	/** 
	 * 验证过关方式格式
	 * @author longguoyou
	 * @date 2017年2月21日 下午7:07:05
	 * @param betContent
	 * @param orderInfoVO
	 * @return
	 */
	protected  ResultBO<?> varifyPasswayPattern(String[] betContent, OrderInfoVO orderInfoVO){
		if(betContent.length >= 2){
			for(String passway : betContent[1].split(SymbolConstants.COMMA)){
				if(!JCConstants.checkFormatJCPassWay(passway)) {
					return ResultBO.err(MessageCodeConstants.PASS_FORMAT_ILLEGAL_SERVICE);
				}
			}
		}
		return ResultBO.ok();
	}
	
	/***
     * 验证投注内容合法性<br>
	 *
	 * @author yuanshangbing
	 * @date 2017年2月16日 下午3:31:51
	 * @param contents  [0]投注的详细内容.包括 系统编号,玩法代号,让分数,投注选项,SP值(多组);[1]过关方式(多个);[2]倍数(单个)<br>
	 * @param orderDetailVO
	 * @param orderInfoVO
	 * @return
	 */
	protected ResultBO<?> varifyContentRelated(String[] contents, OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO){
		if(!ObjectUtil.isBlank(orderDetailVO)){
			ResultBO<?> result = null;
			//2017.04.12 重新整理重构
			if(orderDetailVO.getCodeWay() == OrderEnum.CodeWay.HAND.getValue()){//手选
				//验证竞足、竞篮投注内容合法性
				result = this.judgeContent(contents, orderDetailVO, orderInfoVO);
				if(result.isError()){
					return result;
				}
			}else if(orderDetailVO.getCodeWay() == OrderEnum.CodeWay.MACHINE.getValue()){
				return ResultBO.err(MessageCodeConstants.PARAM_IS_FIELD);
			}else{
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 验证contentType与投注内容关系合法性
	 * @author longguoyou
	 * @date 2017年4月12日
	 * @param contents
	 * @param orderDetailVO
	 * @return
	 */
	protected  ResultBO<?> varifyContentType(String[] contents, OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO){
		ResultBO<?> result = null;
		if(orderDetailVO.getContentType() == BetContentType.SINGLE.getValue()){//单式
			//验证单式投注内容合法性
			result = varifySingleContent(contents, orderDetailVO.getLotteryChildCode(), orderInfoVO);
			if(result.isError()){
				return result;
			}
		}else if(orderDetailVO.getContentType() == BetContentType.MULTIPLE.getValue()){//复式
			//验证复式投注内容合法性
			result = varifyMultipleContent(contents, orderDetailVO.getLotteryChildCode());
			if(result.isError()){
				return result;
			}
		}else if(orderDetailVO.getContentType() == BetContentType.DANTUO.getValue()){//胆拖
			//验证胆拖投注内容合法性
			result = varifyDantuoContent(contents);
			if(result.isError()){
				return result;
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 胆拖投注内容要求
	 * @author longguoyou
	 * @date 2017年4月12日
	 * @param contents
	 * @return
	 */
	private ResultBO<?> varifyDantuoContent(String[] contents) {
		String betContent = contents[0];
		if(!betContent.contains(SymbolConstants.NUMBER_SIGN)){//不包含井号
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		return ResultBO.ok();
	}
    /**
     * 复式投注内容要求
     * @author longguoyou
     * @date 2017年4月12日
     * @param contents
     * @return
     */
	private ResultBO<?> varifyMultipleContent(String[] contents, Integer lotteryChildCode) {
		boolean flag = false;
		String betContent = contents[0];
		if(betContent.contains(SymbolConstants.NUMBER_SIGN)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		String[] betContentArr = FormatConversionJCUtil.stringSplitArray(betContent, SymbolConstants.VERTICAL_BAR, true);
		if(lotteryChildCode == JCZQConstants.ID_FHT || lotteryChildCode == JCLQConstants.ID_JCLQ_HHGG){//混投
			//投注内容格式：1704016020_R[+1](3@1.57)_S(1@1.89)_Q(11@1.78,10@1.78)_Z(2@3.45)_B(33@2.31)|1704027010_Z(0@4.21)|1704027008_R[-2](3@3.33)_Z(0@4.21)
			String[] perContent = null;
			String[] betOptions = null;
		    for(String strContent : betContentArr){
				perContent = strContent.split(SymbolConstants.UNDERLINE);
				if(perContent.length > 2){
					flag = true;
					break;
				}
				//从第二个元素开始，第一个元素为赛事编号
				for(int j = 1; j < perContent.length; j++){
					betOptions = perContent[j].split(SymbolConstants.COMMA);
					if(betOptions.length > 1){
						flag = true;
						break;
					}
				}
			}
		}else{
			//1、判断投注的赛果是否超一个
			for(String perGame : betContentArr){
				String optionStr  = FormatConversionJCUtil.singleGameBetContentSubstring(perGame);//(1@1.89,0@4.21)
				String[] options  = FormatConversionJCUtil.optionBetContentAnalysis(optionStr);//[1@1.89][0@4.21]
				if(options.length > 1){//是否有一场比赛，选择两种赛果进行投注
					flag = true;
					break;
				}
			}
		}
		//没有选择一场赛事量种赛果投注并且过关方式选择一种(场数_1)并且相等
		if(!flag && contents[1].length() == 3 && betContentArr.length == Integer.valueOf(contents[1].substring(0, 1))){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		return ResultBO.ok();
	}
    /**
     * 单式投注内容要求
     * @author longguoyou
     * @date 2017年4月12日
     * @param contents
     * @return
     */
	private ResultBO<?> varifySingleContent(String[] contents, Integer lotteryChildCode ,OrderInfoVO orderInfoVO) {
		String betContent = contents[0];
		if(betContent.contains(SymbolConstants.NUMBER_SIGN)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		String[] betContentArr = FormatConversionJCUtil.stringSplitArray(betContent, SymbolConstants.VERTICAL_BAR, true);
		if(lotteryChildCode == JCZQConstants.ID_FHT || lotteryChildCode == JCLQConstants.ID_JCLQ_HHGG){//混投
			//投注内容格式：1704016020_R[+1](3@1.57)_S(1@1.89)_Q(11@1.78,10@1.78)_Z(2@3.45)_B(33@2.31)|1704027010_Z(0@4.21)|1704027008_R[-2](3@3.33)_Z(0@4.21)
			String[] perContent = null;
			String[] betOptions = null;
		    for(String strContent : betContentArr){
				perContent = strContent.split(SymbolConstants.UNDERLINE);
				if(perContent.length > 2){//子玩法多于1
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				//从第二个元素开始，第一个元素为赛事编号
				for(int j = 1; j < perContent.length; j++){
					betOptions = perContent[j].split(SymbolConstants.COMMA);
					if(betOptions.length > 1){
						return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
					}
				}
			}
		}else{
			if(orderInfoVO.getTabType() != SportEnum.SportTabTypeEnum.SINGLE_BET.getCode()){//不是单关的，需验
				//1、先判断过关方式是否与场数一致
				if(contents[1].length() == 3 && betContentArr.length != Integer.valueOf(contents[1].substring(0, 1))){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
			}
			//2、再判断投注的赛果是否超一个
			for(String perGame : betContentArr){
				String optionStr  = FormatConversionJCUtil.singleGameBetContentSubstring(perGame);//(1@1.89,0@4.21)
				String[] options  = FormatConversionJCUtil.optionBetContentAnalysis(optionStr);//[1@1.89][0@4.21]
				if(options.length > 1){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 高级上传 投注内容验证<br>
	 * 竞彩足球：<br>e.g. 161128001_R[+1](3@1.57,0@2.27)_S(1@1.89,0@4.21)_Q(11@1.78,23@5.21)_Z(2@3.45,3@2.34)_B(33@2.31,31@4.00)#
	 *                  161128002_S(1@1.89,0@4.21)|161128003_Z(0@4.21)|161128004_R[-2](3@3.33)_Z(0@4.21)^2_1,3_1^868<br>
	 *              
	 * 竞彩篮球：<br>e.g. 161128001_R[+11.5](3@1.57,0@2.27)_S(3@1.89,0@4.21)_C (11@1.78,16@5.21)_D[165.5] (99@3.45,00@2.34)#
	 *                  161128002_S(3@1.89,0@4.21)|161128003_C(06@4.21)|161128004_R[-2.5](3@3.33)_D[180.5] (99@4.21)^2_1,3_1^868<br>
	 * @author longguoyou
	 * @date 2017年3月16日
	 * @param betContent
	 * @param lotteryCode
	 * @param orderInfoVO
	 * @return
	 */
	public ResultBO<?> verifyUserUpload(String betContent, Integer lotteryCode, OrderInfoVO orderInfoVO){
		ResultBO<?> result = null;
		//区分让胜平负和胜平负区别 投注内容合法性
		result = dealRspfAndSpfDifferent(betContent, lotteryCode);
		if(result.isError()){
			return result;
		}
		String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(betContent);
		if(ObjectUtil.isBlank(contents) || contents.length < 3){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		//1、验证倍数
		if(Integer.valueOf(contents[2]) > Constants.NUM_1000){
			return ResultBO.err(MessageCodeConstants.MULTIPLE_LIMIT_SERVICE);
		}
		//2、验证过关方式
		String passway = contents[1];
		result = varifyPasswayContent(passway.split(SymbolConstants.COMMA), orderInfoVO, betContent);
		if(result.isError()){
			return result;
		}
		//高级上传，多个方法案与分号分隔
		String[] contentsLevelTwo = contents[0].split(SymbolConstants.SEMICOLON);
		if(contentsLevelTwo.length > 1){
			for(String content : contentsLevelTwo){
				result = repeat(content, lotteryCode);
				if(result.isError()){
					return result;
				}
			}
		}
		return repeat(contents[0], lotteryCode);
	}
	
	/**
	 * 处理封装
	 * @author longguoyou
	 * @date 2017年3月31日
	 * @param content
	 * @param lotteryCode
	 * @return
	 */
	private ResultBO<?> repeat(String content, Integer lotteryCode){
		ResultBO<?> result = null;
		//各对阵投注内容 如： [161128002_S(1@1.89,0@4.21)#161128003_Z(0@4.21)],[161128004_R[-2](3@3.33)_Z(0@4.21)]
		String[] optionStr  = FormatConversionJCUtil.betContentDetailsAnalysis(content);
		for(String match : optionStr){
			if(match.contains(SymbolConstants.NUMBER_SIGN)){
				//解析对阵内容 如：[161128002_S(1@1.89,0@4.21)_R(1@1.85,0@3.25)],[161128003_Z(0@4.21)]
				String[] danoptions = FormatConversionJCUtil.singleMatchDanBetContentAnalysis(match);//有胆码
				for(String dan : danoptions){
					String[] options  = FormatConversionJCUtil.singleMatchBetContentAnalysis(dan);//无胆码
					result =  deal(options, lotteryCode, 1);
					if(result.isError()){
						return result;
					}
				}
			}else{
				//解析对阵内容 如： [161128004],[R[-2](3@3.33,2@3.21)],[Z(0@4.21)]
				String[] options  = FormatConversionJCUtil.singleMatchBetContentAnalysis(match);//无胆码
				return deal(options, lotteryCode, 0);
			}
		}
		return ResultBO.ok();
	}
	/**
	 * 处理判断投注内容
	 * @author longguoyou
	 * @date 2017年3月17日
	 * @param options
	 * @param lotteryCode
	 * @param beginIndex 开始遍历下标
	 * @return
	 */
	private ResultBO<?> deal(String[] options, Integer lotteryCode, int beginIndex){
		Integer lotteryId = 0;
		String way = null;
		for(int i = beginIndex,len = options.length; i < len; i++){
			way = options[i].substring(0, 1);
			if(JCZQConstants.checkLotteryId(lotteryCode)){//竞足
				//3、验证投注内容
				SportEnum.SportFbSubWay sportFbSubWay = SportEnum.SportFbSubWay.getSportFbSubWay(way);
				switch (sportFbSubWay) {
					case JCZQ_Q:
						lotteryId = JCZQConstants.ID_FBF;
						break;
					case JCZQ_B:
						lotteryId = JCZQConstants.ID_FBCQ;
						break;
					case JCZQ_R:
						lotteryId = JCZQConstants.ID_RQS;
						break;
					case JCZQ_S:
						lotteryId = JCZQConstants.ID_JCZQ;
						break;
					default:
						lotteryId = JCZQConstants.ID_FZJQ;
						break;
				}
				if(!JCZQConstants.checkJCZQBetContentGame(lotteryId, options[i].substring(0, options[i].indexOf(SymbolConstants.AT)))){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
			}
			if(JCLQConstants.checkLotteryId(lotteryCode)){//竞篮
				//3、验证投注内容
				SportEnum.SportBBSubWay sportBBSubWay = SportEnum.SportBBSubWay.getSportBBSubWay(way);
				switch (sportBBSubWay) {
					case JCLQ_S:
						lotteryId = JCLQConstants.ID_JCLQ_SF;
						break;
					case JCLQ_R:
						lotteryId = JCLQConstants.ID_JCLQ_RF;
						break;
					case JCLQ_D:
						lotteryId = JCLQConstants.ID_JCLQ_DXF;
						break;
					case JCLQ_C:
						lotteryId = JCLQConstants.ID_JCLQ_SFC;
						break;
					default:
						lotteryId = JCLQConstants.ID_JCLQ_HHGG;
						break;
				}
				if(!JCLQConstants.checkJCLQBetContentGame(lotteryId, options[i].substring(0, options[i].indexOf(SymbolConstants.AT)))){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 投注内容判断,已对胆码符合井号进行转换
	 * @author longguoyou
	 * @date 2017年3月6日 下午6:54:33
	 * @param contents [0]投注的详细内容.包括 系统编号,玩法代号,让分数,投注选项,SP值(多组);[1]过关方式(多个);[2]倍数(单个)<br>
	 * @param orderDetailVO
	 * @param orderInfoVO
	 * @return
	 */
	private ResultBO<?> judgeContent(String[] contents, OrderDetailVO  orderDetailVO, OrderInfoVO orderInfoVO){
		if(ObjectUtil.isBlank(contents) || contents.length < 2){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		String betContent = contents[0];
		ResultBO<?> result = null;
		//区分让胜平负和胜平负区别 投注内容合法性
		result = dealRspfAndSpfDifferent(betContent, orderDetailVO.getLotteryChildCode());
		if(result.isError()){
			return result;
		}
		if(betContent.indexOf(SymbolConstants.NUMBER_SIGN) > -1){//有胆码情况
			betContent = betContent.replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
			result = this.betSingle(orderDetailVO, betContent, orderInfoVO);
			if(result.isError()){
				return result;
			}
		}else{//无胆码情况
			return this.betSingle(orderDetailVO, betContent, orderInfoVO);
		}
		return ResultBO.ok();
	}

	/**
	 * 竞彩足球、篮球混合过关投注内容判断
	 * @author longguoyou
	 * @date 2017年3月6日 下午6:55:18
	 * @param betContent  e.g. 161128004_R[-2](3@3.33)_Z(0@4.21)
	 * @param orderDetailVO 子玩法
	 * @return
	 */
	private ResultBO<?> judgeMixContent(String betContent, OrderDetailVO  orderDetailVO){
		if(!betContent.contains(SymbolConstants.UNDERLINE)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		if(JCZQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
			SportEnum.SportFbSubWay[] val = SportEnum.SportFbSubWay.values();
			String[] betMixArr = FormatConversionJCUtil.stringSplitArray(betContent, SymbolConstants.UNDERLINE, false);//e.g. [161128004],[R[-2](3@3.33)],[Z(0@4.21)]
			for(int i = 1 ,len = betMixArr.length; i < len; i++){
				boolean flag = true;
				String content = betMixArr[i];
				//验玩法标识符, 数组除第一个元素外，其它的元素的第一个字符即为玩法标识符
				for(SportEnum.SportFbSubWay v :val){
					String subway = v.getValue();
					if(subway.equals(content.substring(0, 1))){//玩法
						flag = false;
						ResultBO<?> result = this.betMix(orderDetailVO, subway, content);//投注内容
						if(result.isError()){
							return result;
						}
					}
				}
				if(flag){
					return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE);
				}
			}
		}else if(JCLQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
			SportEnum.SportBBSubWay[] val = SportEnum.SportBBSubWay.values();//竞彩篮球玩法枚举
			String[] betMixArr = FormatConversionJCUtil.stringSplitArray(betContent, SymbolConstants.UNDERLINE, false);
			for(int i = 1 ,len = betMixArr.length; i < len; i++){
				boolean flag = true;
				String content = betMixArr[i];
				//验玩法标识符, 数组除第一个元素外，其它的元素的第一个字符即为玩法标识符
				for(SportEnum.SportBBSubWay v :val){
					String subway = v.getValue();
					if(subway.equals(content.substring(0, 1))){//玩法
						flag = false;
						ResultBO<?> result = this.betMix(orderDetailVO, subway, content);//投注内容
						if(result.isError()){
							return result;
						}
					}
				}
				if(flag){
					return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_ILLEGAL_SERVICE);
				}
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 竞彩足球、篮球混合子玩法投注内容合法性判断
	 * @author longguoyou
	 * @date 2017年3月6日 下午6:56:06
	 * @param orderDetailVO
	 * @param subway
	 * @param betContent
	 * @return
	 */
	private ResultBO<?> betMix(OrderDetailVO orderDetailVO,String subway, String betContent){
		Integer lotteryId = 0;
		String way = null;
		if(!ObjectUtil.isBlank(betContent)){//[Z(0@4.21)]
			way = betContent.substring(0, 1);//取得Z
		}
		ResultBO<?> result = null;
		//混投正则表达式验证投注内容
		result = regexpContent(way, betContent, orderDetailVO, true);
		if(result.isError()){
			return result;
		}
		//验投注选项
		String optionStr  = FormatConversionJCUtil.singleGameBetContentSubstring(betContent);//(1@1.89,0@4.21)
		String[] options  = FormatConversionJCUtil.optionBetContentAnalysis(optionStr);//[1@1.89][0@4.21]
		Set<String> setOptions = new HashSet<String>();
		for(String content : options){
			setOptions.add(content);
			if(JCZQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
				if(way.equals(SportEnum.SportFbSubWay.JCZQ_Q.getValue())){
					lotteryId = JCZQConstants.ID_FBF;
				}else if(way.equals(SportEnum.SportFbSubWay.JCZQ_B.getValue())){
					lotteryId = JCZQConstants.ID_FBCQ;
				}else if(way.equals(SportEnum.SportFbSubWay.JCZQ_R.getValue())){
					lotteryId = JCZQConstants.ID_RQS;
				}else if(way.equals(SportEnum.SportFbSubWay.JCZQ_S.getValue())){
					lotteryId = JCZQConstants.ID_JCZQ;
				}else if(way.equals(SportEnum.SportFbSubWay.JCZQ_Z.getValue())){
					lotteryId = JCZQConstants.ID_FZJQ;
				}
				if(!JCZQConstants.checkJCZQBetContentGame(lotteryId, content.substring(0, content.indexOf(SymbolConstants.AT)))){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
			}
			if(JCLQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
				if(way.equals(SportEnum.SportBBSubWay.JCLQ_S.getValue())){
					lotteryId = JCLQConstants.ID_JCLQ_SF;
				}else if(way.equals(SportEnum.SportBBSubWay.JCLQ_R.getValue())){
					lotteryId = JCLQConstants.ID_JCLQ_RF;
				}else if(way.equals(SportEnum.SportBBSubWay.JCLQ_D.getValue())){
					lotteryId = JCLQConstants.ID_JCLQ_DXF;
				}else if(way.equals(SportEnum.SportBBSubWay.JCLQ_C.getValue())){
					lotteryId = JCLQConstants.ID_JCLQ_SFC;
				}else if(way.equals(SportEnum.SportBBSubWay.JCLQ_M.getValue())){
					lotteryId = JCLQConstants.ID_JCLQ_HHGG;
				}
				if(!JCLQConstants.checkJCLQBetContentGame(lotteryId, content.substring(0, content.indexOf(SymbolConstants.AT)))){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
			}
		}
		/**
		 * 防止投注选项内容重复，如：(3@2.14,3@2.14,3@2.14)
		 */
		if(options.length != setOptions.size()){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		return ResultBO.ok();
	}
	/**
	 * 正则表达式验证投注内容
	 * @author longguoyou
	 * @date 2017年4月13日
	 * @param way  子玩法简称符合
	 * @param orderDetailVO
	 * @param betContent 按下划线截取后的每个元素 混投：[R[+1](3@1.25)],[S[+1](3@1.25)],[Q[+1](3@1.25,0@1.25)] / 单一玩法：[16251211R[+1](3@1.25,4@1.23)],[16251212(3@1.25,4@1.23)],[16251213(3@1.25,4@1.23)]
	 * @param flag 是否混投
	 * @return
	 */
	private ResultBO<?> regexpContent(String way, String betContent, OrderDetailVO orderDetailVO , boolean flag){
		logger.debug("正则表达式验证投注内容开始....");
		String childCode = "";
		String betRegexp = "";
		String regexp = "";
		final String systemCodeRegexp = flag ?  "^[" : (BJDCConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())?"[0-9]{7,11}":"^\\d{10}");
		if(flag){//混投
			if(JCZQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){//竞彩足球：整数 球数
				childCode = JCZQConstants.CHILD_CODE_SIMPLE_NAME + (way.equals("R")?"]\\[[+-][1-9][0-9]*\\]":"]");
				betRegexp = JCZQConstants.getLotteryCodeBySimpleCode(way);
			}else if(JCLQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){//竞彩篮球: 存在小数 ，分数
				String temp = "]";
				if(way.equals("R")){//R[+11.5]
					temp = "]((\\[[+-][1-9][0-9]*\\.[0-9][0-9]*\\])|(\\[[+-][1-9][0-9]*\\]))";
				}else if(way.equals("D")){//D[165.5]
					temp = "]((\\[[1-9][0-9]*\\.[0-9][0-9]*\\])|(\\[[1-9][0-9]*\\]))";
				}
				childCode = JCLQConstants.CHILD_CODE_SIMPLE_NAME + temp;
				betRegexp = JCLQConstants.getLotteryCodeBySimpleCode(way);
			}else if(BJDCConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){//北京单场
				childCode = BJDCConstants.CHILD_CODE_SIMPLE_NAME + (way.equals("R")?"]\\[[+-]\\d{1}\\]":"]");//让个位数  
				betRegexp = BJDCConstants.getLotteryCodeBySimpleCode(way);
			}
		}
		if(!flag){//非混投
			if(JCZQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){//且是竞足
				childCode = (way.equals("R")?"\\[[+-][1-9][0-9]*\\]":"");//且是让球胜平负	
				betRegexp = JCZQConstants.getLotteryCodeBySimpleCode(way);
			}else if(JCLQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){//且是竞篮 
				if(way.equals("R")){//且是让胜平负
					childCode = "((\\[[+-][1-9][0-9]*\\.{0,1}[0-9][0-9]*\\]|\\[[+-][1-9][0-9]*\\]))"; //存在小数 ，分数 。如：[+11]/[+11.5]
				}else if(way.equals("D")){//且是大小分
					childCode = "((\\[[1-9][0-9]*\\.[0-9][0-9]*\\])|(\\[[1-9][0-9]*\\]))";//存在小数 ，分数 。如：[166.5]
				}
				betRegexp = JCLQConstants.getLotteryCodeBySimpleCode(way);
//				childCode = (way.equals("R")?"((\\[[+-][1-9][0-9]*\\.{0,1}[0-9][0-9]*\\]|\\[[+-][1-9][0-9]*\\]))":"");//且是让胜平负
			}else if(BJDCConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){//是北单
				//childCode = ((way.equals("R") || way.equals("W"))?"\\[[+-][0-9][0-9]*\\]":"");//让球胜平负 / 胜负彩
				if(way.equals("R")){//让球胜平负
					childCode = "\\[[+-][0-9][0-9]*\\]";
				}else if(way.equals("W")){//胜负过关
					childCode = "((\\[[+-][0-9][0-9]*\\.[0-9][0-9]*\\])|(\\[[+-][1-9][0-9]*\\]))";//存在小数,如+0.5
				}
				betRegexp = BJDCConstants.getLotteryCodeBySimpleCode(way);
			}
		}
		//没有取得投注内容正则表达式
		if(ObjectUtil.isBlank(betRegexp)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		String optionStr  = FormatConversionJCUtil.singleGameBetContentSubstring(betContent);//(1@1.89,0@4.21)
		String[] options  = FormatConversionJCUtil.optionBetContentAnalysis(optionStr);//[1@1.89][0@4.21]
		if(options.length == 1){//单式
			regexp = systemCodeRegexp + childCode + "\\(" + betRegexp + "@[1-9][0-9]*\\.[0-9]{1,2}\\)";
		}else if(options.length > 1){//复式
			if(orderDetailVO.getContentType() == BetContentType.SINGLE.getValue()){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			//^[SRCD]((\\[[1-9][0-9]*\\.[0-9][0-9]*\\])|(\\[[1-9][0-9]*\\))\\((9(9)|0(0))@[1-9][0-9]*(\\.[0-9]{1,2})\\)
			regexp = systemCodeRegexp + childCode + "\\((" + betRegexp + "@[1-9][0-9]*\\.[0-9]{1,2},)*" + betRegexp + "@[1-9][0-9]*\\.[0-9]{1,2}\\)";
		}
		logger.debug("regexp:"+regexp);
		logger.debug("betContent:"+betContent);
		//动态正则表达式验证不通过
		if(!ObjectUtil.isBlank(regexp)){
			if(!betContent.matches(regexp)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		logger.debug("正则表达式验证投注内容结束....");
		return ResultBO.ok();
	}

	/**
	 * 
	 * 竞彩足球、篮球所有过关子玩法投注内容合法性判断
	 * @author longguoyou
	 * @date 2017年3月6日 下午6:57:37
	 * @param orderDetailVO
	 * @param betContent 投注内容 contents[0]
	 * @param orderInfoVO
	 * @return
	 */
	private ResultBO<?> betSingle(OrderDetailVO orderDetailVO, String betContent, OrderInfoVO orderInfoVO){
		ResultBO<?> result = null;
		String[] betContentArr = FormatConversionJCUtil.stringSplitArray(betContent, SymbolConstants.VERTICAL_BAR, true);
		//验证单个方案是否超15场赛事
		if(betContentArr.length > Constants.NUM_15){
			return ResultBO.err(MessageCodeConstants.SINGLE_PLAN_BET_MATCH_LIMIT_SERVICE);
		}
		if(orderInfoVO.getTabType() != SportTabTypeEnum.SINGLE_BET.getCode()){//不是单关，都要验证
			if(ObjectUtil.isBlank(betContentArr) || betContentArr.length < 2){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		if(orderDetailVO.getLotteryChildCode() == JCZQConstants.ID_FHT || orderDetailVO.getLotteryChildCode() == JCLQConstants.ID_JCLQ_HHGG){
			for(String strContent : betContentArr){
				//验证竞足、竞篮混投投注内容合法性
				result = this.judgeMixContent(strContent, orderDetailVO);
				if(result.isError()){
					return result;
				}
			}
		}else{
			//不是混投，投注内容包含下划线，不合法
			if(betContent.contains(SymbolConstants.UNDERLINE)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			if(JCZQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode()) || JCLQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
				//针对单一玩法正则表达式验证投注内容(不包含混投)
				result = varifySingleWayByRegexp(orderDetailVO, betContent);
				if(result.isError()){
					return result;
				}
			}
			String content = null;
			Integer lotteryCode = orderDetailVO.getLotteryChildCode();
			for(String strContent: betContentArr){
				/**防止同一选项重复投注*/
				String optionStr  = FormatConversionJCUtil.singleGameBetContentSubstring(strContent);//3@2.14,0@2.13
				Set<String> setOptions = new HashSet<String>();
				for(String option : optionStr.split(SymbolConstants.COMMA)){
					setOptions.add(option);
				}
				if(setOptions.size() != optionStr.split(SymbolConstants.COMMA).length){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				/*****************/
				for(String betCont : strContent.split(SymbolConstants.COMMA)){
					content  = FormatConversionJCUtil.stringSubstringToString(betCont, SymbolConstants.PARENTHESES_LEFT, SymbolConstants.AT, true);
					if(JCZQConstants.checkLotteryId(lotteryCode) && !JCZQConstants.checkJCZQBetContentGame(lotteryCode, content)){
						return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
					}else if(JCLQConstants.checkLotteryId(lotteryCode) && !JCLQConstants.checkJCLQBetContentGame(lotteryCode, content)){
						return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
					}
				}
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 针对单一玩法正则表达式验证投注内容(不包含混投)
	 * @author longguoyou
	 * @date 2017年4月14日
	 * @param orderDetailVO
	 * @param betContent
	 * @return
	 */
	protected ResultBO<?> varifySingleWayByRegexp(OrderDetailVO orderDetailVO, String betContent){
		ResultBO<?> result = null;
		String way = null;
		if(JCZQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
			switch(orderDetailVO.getLotteryChildCode()){
			case JCZQConstants.ID_FBCQ: way = "B"; break;
			case JCZQConstants.ID_FBF:  way = "Q"; break;
			case JCZQConstants.ID_FZJQ: way = "Z"; break;
			case JCZQConstants.ID_JCZQ: way = "S"; break;
			case JCZQConstants.ID_RQS:  way = "R"; break;
			}
		}else if(JCLQConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
			switch(orderDetailVO.getLotteryChildCode()){
			case JCLQConstants.ID_JCLQ_DXF: way = "D";break;
			case JCLQConstants.ID_JCLQ_RF:  way = "R";break;
			case JCLQConstants.ID_JCLQ_SF:  way = "S";break;
			case JCLQConstants.ID_JCLQ_SFC: way = "C";break;
			}
		}else if(BJDCConstants.checkLotteryId(orderDetailVO.getLotteryChildCode())){
			switch(orderDetailVO.getLotteryChildCode()){
			case BJDCConstants.ID_FBCQ: way = "B"; break;
			case BJDCConstants.ID_FBF:  way = "Q"; break;
			case BJDCConstants.ID_FZJQ: way = "Z"; break;
			case BJDCConstants.ID_RQS:  way = "R"; break;
			case BJDCConstants.ID_SXDX: way = "S"; break;
			case BJDCConstants.ID_SFC:  way = "W"; break;
			}
		}
		if(betContent.contains(SymbolConstants.NUMBER_SIGN)){
			betContent = betContent.replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
		}
		String[] betContentArr = FormatConversionJCUtil.stringSplitArray(betContent, SymbolConstants.VERTICAL_BAR, true);
		for(int i = 0; i < betContentArr.length; i++){
			//正则表达式验证投注内容
			result = regexpContent(way, betContentArr[i], orderDetailVO, false);
			if(result.isError()){
				return result;
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 单个方案的投注内容解析注数验证(是否与传参betNum相等)，并返回解析注数
	 * @author longguoyou
	 * @date 2017年3月13日
	 * @param lotteryCode
	 * @param orderDetailVO
	 * @return
	 */
	public ResultBO<?> verifyBetNumBoundary(Integer lotteryCode, OrderDetailVO orderDetailVO, String[] betContent,Short isSingleOrder){
		Map<String,Object> map = new HashMap<String,Object>();
		int calBetNum = 0 ;
		//单式上传，14场和任九注数都是1
		if(Constants.NUM_1 == Integer.valueOf(isSingleOrder) &&
				(LotteryEnum.Lottery.ZC_NINE.getName() == lotteryCode || LotteryEnum.Lottery.SFC.getName() == lotteryCode)) {
			calBetNum = Constants.NUM_1;
		}else{
			calBetNum = handleLotteryZs(orderDetailVO, lotteryCode);
		}
		int betMultiple = betContent.length == 3 ? Integer.valueOf(betContent[2]) : 0;
	    if(calBetNum != orderDetailVO.getBuyNumber()){
	        return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_BETNUM_NOT_EQUAL_PARAM_BET_NUM_SERVICE);
	    }    
	    map.put(Constants.BET_NUM_KEY, calBetNum);
	    map.put(Constants.BET_MULTIPLE_KEY, betMultiple);
		return ResultBO.ok(map);
	}
	
	/**
	 * 计算对应彩种投注注数
	 * @param lotteryCode
	 * @param orderDetailVO
	 * @return
	 */
	private int handleLotteryZs(OrderDetailVO orderDetailVO, Integer lotteryCode){
		return SportsZsUtil.getSportsManyNote(orderDetailVO.getPlanContent(), lotteryCode);
	}

	/**
	 * 翻译限号内容如：巴西(胜平负-胜)、巴西(让球胜平负-胜)、巴西(-2球负）、巴西(半全场-胜胜)、巴西(总进球-5球)、巴西(全场比分-6:2)
	 * @param limitContent 限号内容翻译 1801025012(3)、1801025012(33)、1801025012_S(3)、1801025012_R[+1](3)
	 * @param lotteryCode 彩种
	 * @return
	 */
	public String translate(String limitContent,Integer lotteryCode){
		//单一玩法：1807032102(3,1)|1807043101(3)|1807043102(3)^3_1/1807032102[-1](3)|1807043101[+1](3)|1807043102[+1](3)^3_1
		//混投：1807032102_S(3,1)|1807032102_R[+1](3)|1807043101_S(0)|1807043102_S(1)^3_1
		String[] contents = FormatConversionJCUtil.singleBetContentAnalysis(limitContent);
		//拆分内容
		String[] betContent = contents[0].split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
		String info  = "";
		StringBuffer allBuffer = new StringBuffer();
		if(JCZQConstants.checkLotteryId(lotteryCode) || BJDCConstants.checkLotteryId(lotteryCode)){//只验证竞足、北单
			for(String content : betContent){
				StringBuffer stringBuffer = new StringBuffer();
				if(content.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT) && !content.contains(SymbolConstants.UNDERLINE)){//混投不处理
					info = content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT),content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+Constants.NUM_1);
					if(logger.isDebugEnabled()){logger.debug("附件信息info:"+info);}
				}
				boolean flag = lotteryCode.equals(JCZQConstants.ID_FHT) || lotteryCode.equals(JCLQConstants.ID_JCLQ_HHGG);
				String homeName = getHomeTeamName(Integer.valueOf(lotteryCode.toString().substring(0,3)),content,flag);
				if(logger.isDebugEnabled()){logger.debug("球队名称："+homeName);}
				stringBuffer.append(homeName).append(info).append(SymbolConstants.PARENTHESES_LEFT);//巴西[+1](
                if(lotteryCode.equals(JCZQConstants.ID_FHT) || lotteryCode.equals(JCLQConstants.ID_JCLQ_HHGG)){
                    allBuffer.append(mixWay(stringBuffer,content,lotteryCode));
                    allBuffer.append(SymbolConstants.VERTICAL_BAR);
				}else{
					allBuffer.append(singleWay(stringBuffer,content,lotteryCode));
					allBuffer.append(SymbolConstants.VERTICAL_BAR);
				}
			}
		}
		return allBuffer.toString().substring(0,allBuffer.toString().length()-1);
	}


	/**
	 * 混投玩法，翻译限号
	 * @param stringBuffer 巴西(
	 * @param content 1807032102_S(3,1)|1807032102_R[+1](3)|1807043101_S(0)|1807043102_S(1)^3_1
	 * @param lotteryCode
	 * @return
	 */
	private String mixWay(StringBuffer stringBuffer, String content, Integer lotteryCode) {
		String lotteryChildName = "";
		Integer lotCode = null;
		if(lotteryCode.equals(JCZQConstants.ID_FHT)){
		    if(content.contains("S")){
		    	lotCode = JCZQConstants.ID_JCZQ;
			    lotteryChildName = "胜平负";
		    }else if(content.contains("R")){
				lotCode = JCZQConstants.ID_RQS;
		    	lotteryChildName = "让球胜平负";
		    }else if(content.contains("B")){
				lotCode = JCZQConstants.ID_FBCQ;
			    lotteryChildName = "半全场";
		    }else if(content.contains("Z")){
		    	lotCode = JCZQConstants.ID_FZJQ;
			    lotteryChildName = "总进球";
		    }else if(content.contains("Q")){
		    	lotCode = JCZQConstants.ID_FBF;
				lotteryChildName = "全场比分";
		    }
			stringBuffer.append(lotteryChildName).append(SymbolConstants.TRAVERSE_SLASH);//巴西(胜平负-
			content = content.substring(content.indexOf(SymbolConstants.PARENTHESES_LEFT)+Constants.NUM_1,content.indexOf(SymbolConstants.PARENTHESES_RIGHT));
			return singleWay(stringBuffer,content,lotCode);
		}
		if(lotteryCode.equals(JCLQConstants.ID_JCLQ_HHGG)){
			if(content.contains("S")){
				lotCode = JCLQConstants.ID_JCLQ_SF;
				lotteryChildName = "胜负";
			}else if(content.contains("R")){
				lotCode = JCLQConstants.ID_JCLQ_RF;
				lotteryChildName = "让分胜负";
			}else if(content.contains("C")){
				lotCode = JCLQConstants.ID_JCLQ_SFC;
				lotteryChildName = "胜分差";
			}else if(content.contains("D")){
				lotCode = JCLQConstants.ID_JCLQ_DXF;
				lotteryChildName = "大小分";
			}
			stringBuffer.append(lotteryChildName).append(SymbolConstants.TRAVERSE_SLASH);//火箭(胜负-
			content = content.substring(content.indexOf(SymbolConstants.PARENTHESES_LEFT)+Constants.NUM_1,content.indexOf(SymbolConstants.PARENTHESES_RIGHT));
		}
		return null;
	}

	/**
	 * 单一玩法，翻译限号
	 * @param stringBuffer
	 * @param content
	 * @param lotteryCode
	 * @return
	 */
	private String singleWay(StringBuffer stringBuffer, String content,Integer lotteryCode){
		if(content.contains(SymbolConstants.PARENTHESES_LEFT) && content.contains(SymbolConstants.PARENTHESES_RIGHT)){
			content = content.substring(content.indexOf(SymbolConstants.PARENTHESES_LEFT)+Constants.NUM_1,content.indexOf(SymbolConstants.PARENTHESES_RIGHT));
		}
		if(content.contains(SymbolConstants.COMMA)){//存在多个投注项
			String[] options = content.split(SymbolConstants.COMMA);
			StringBuffer innerBuffer = new StringBuffer();
			for(String option : options){
			    //竞足、竞篮、北单逻辑处理
				innerBuffer.append(BJDCConstants.checkLotteryId(lotteryCode)?BJDCConstants.translate(lotteryCode,option,null):JCZQConstants.translate(lotteryCode,option,null));//胜
				innerBuffer.append(SymbolConstants.COMMA);//胜,
			}
			String lastString = innerBuffer.substring(0,innerBuffer.toString().length()-1);
			stringBuffer.append(lastString);//巴西(胜,负
		}else{
            //竞足、竞篮、北单逻辑处理
			stringBuffer.append(BJDCConstants.checkLotteryId(lotteryCode)?BJDCConstants.translate(lotteryCode,content,null):JCZQConstants.translate(lotteryCode, content,null));//巴西(胜
		}
		stringBuffer.append(SymbolConstants.PARENTHESES_RIGHT);//巴西(胜)或 巴西(胜,负)
		return stringBuffer.toString();
	}

	/**
	 * 获取主队名称
	 * @param lotteryCode 彩种
	 * @param content 限号内容
	 * @param flag 是否混投
	 * @return
	 */
	private String getHomeTeamName(Integer lotteryCode, String content,boolean flag) {
		List<SportAgainstInfoBO> listAgainstInfoBO  = null;
		Lottery lot = Lottery.getLottery(lotteryCode);
		String[] cont = content.split(SymbolConstants.UNDERLINE);
		List<String> matchs = null;
		if(!flag && content.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){//非混投且包含“[”
			content = content.substring(0,content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT));
		}else if(!flag && !content.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
			content = content.substring(0,content.indexOf(SymbolConstants.PARENTHESES_LEFT));
		}
		matchs = flag?Arrays.asList(cont[0]):Arrays.asList(content);
		switch (lot){
			case FB:
				listAgainstInfoBO = getJczqListAgainstInfoBOs(lotteryCode, matchs);
				break;
			case BB:
				listAgainstInfoBO = getJclqListAgainstInfoBOs(lotteryCode, matchs);
				break;
			case BJDC:
			case SFGG:
				listAgainstInfoBO = getBjdcListAgainstInfoBOs(lotteryCode, matchs);
				break;
			default:
				break;
		}
		if(!ObjectUtil.isBlank(listAgainstInfoBO)){
			return listAgainstInfoBO.get(0).getHomeName();
		}
		return null;
	}

	/**
	 * 
	 * @Description: 根据订单详情，获取赛事，并查询最快要截至的赛事
	 * @param detailVOs
	 * @param lotteryCode
	 * @return
	 * @author wuLong
	 * @date 2017年3月24日 上午11:36:29
	 */
	protected List<SportAgainstInfoBO> getSportAgainstInfoBOs(List<OrderDetailVO> detailVOs, Integer lotteryCode){
		//缓存取数据
		List<SportAgainstInfoBO> listAgainstInfoBO  = null;//去掉缓存
		//取出方案中赛事编号对应的赛事集合
		if(ObjectUtil.isBlank(listAgainstInfoBO)){
			//从投注内容中，得到系统赛事编号
			List<String> matchs = getMatchsByContent(detailVOs);
			Lottery lot = Lottery.getLottery(lotteryCode);
			switch (lot) {
			case FB:
				listAgainstInfoBO = getJczqListAgainstInfoBOs(lotteryCode, matchs);
				break;
			case BB:
				listAgainstInfoBO = getJclqListAgainstInfoBOs(lotteryCode, matchs);
				break;
			case BJDC:
			case SFGG:
				listAgainstInfoBO = getBjdcListAgainstInfoBOs(lotteryCode, matchs);
				break;
			default:
				break;
			}
		}
		return listAgainstInfoBO;
	}
	
	/**
	 * 根据彩种id和赛事id集合，查询竞足赛事信息
	 * @author longguoyou
	 * @date 2017年11月24日
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 */
	private List<SportAgainstInfoBO> getJczqListAgainstInfoBOs(Integer lotteryCode, List<String> matchs){
		List<SportAgainstInfoBO> listAgainstInfoBO = new ArrayList<SportAgainstInfoBO>();
		Map<String,JczqOrderBO> map = jcDataService.findJczqOrderBOBySystemCodes(matchs);
		/**id,systemCode,matchStatus,homeShortName**/
		for(String key : map.keySet()){
			SportAgainstInfoBO sportAgainstInfoBO = new SportAgainstInfoBO();
			JczqOrderBO jczqOrderBO = map.get(key);
			if(!ObjectUtil.isBlank(jczqOrderBO)){
				sportAgainstInfoBO.setId(jczqOrderBO.getId());
				sportAgainstInfoBO.setSystemCode(jczqOrderBO.getSystemCode());
				sportAgainstInfoBO.setMatchStatus(jczqOrderBO.getMatchStatus());
				sportAgainstInfoBO.setHomeName(jczqOrderBO.getHomeFullName());//限号翻译使用
				listAgainstInfoBO.add(sportAgainstInfoBO);
			}
		}
		return listAgainstInfoBO;
	}
	
	/**
	 * 根据彩种id和赛事id集合，查询竞篮赛事信息
	 * @author longguoyou
	 * @date 2017年11月24日
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 */
	private List<SportAgainstInfoBO> getJclqListAgainstInfoBOs(Integer lotteryCode, List<String> matchs){
		List<SportAgainstInfoBO> listAgainstInfoBO = new ArrayList<SportAgainstInfoBO>();
		for(String systemCode : matchs){
			JclqOrderBO jclqOrderBO = jcDataService.findJclqOrderBOBySystemCode(systemCode);
			/**id,systemCode,matchStatus,homeShortName**/
			SportAgainstInfoBO sportAgainstInfoBO = new SportAgainstInfoBO();
			if(!ObjectUtil.isBlank(jclqOrderBO)){
				sportAgainstInfoBO.setId(jclqOrderBO.getId());
				sportAgainstInfoBO.setSystemCode(jclqOrderBO.getSystemCode());
				sportAgainstInfoBO.setMatchStatus(jclqOrderBO.getMatchStatus());
				sportAgainstInfoBO.setHomeName(jclqOrderBO.getHomeFullName());//限号翻译使用
				listAgainstInfoBO.add(sportAgainstInfoBO);
			}
		}
		return listAgainstInfoBO;
	}
	
	/**
	 * 根据彩种id和赛事id集合，查询北单赛事信息
	 * @author longguoyou
	 * @date 2017年11月24日
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 */
	private List<SportAgainstInfoBO> getBjdcListAgainstInfoBOs(Integer lotteryCode, List<String> matchs){
		List<SportAgainstInfoBO> listAgainstInfoBO = new ArrayList<SportAgainstInfoBO>();
		for(String systemCode : matchs){
			BjDaoBO bjDaoBO = jcDataService.findBjDataBOBySystemCode(systemCode, String.valueOf(lotteryCode));
			/**id,systemCode,matchStatus,homeShortName**/
			SportAgainstInfoBO sportAgainstInfoBO = new SportAgainstInfoBO();
			if(!ObjectUtil.isBlank(bjDaoBO)){
				sportAgainstInfoBO.setId(bjDaoBO.getId());
				sportAgainstInfoBO.setSystemCode(bjDaoBO.getSystemCode());
				sportAgainstInfoBO.setMatchStatus(bjDaoBO.getMatchStatus());
				sportAgainstInfoBO.setHomeName(bjDaoBO.getHomeFullName());//限号翻译使用
				listAgainstInfoBO.add(sportAgainstInfoBO);
			}
		}
		return listAgainstInfoBO;
	}

	/**
	 * 
	 * @Description: 根据彩种id和赛事id集合，查询赛事信息(用于获取最早一场截止赛事)
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 * @author wuLong
	 * @date 2017年3月25日 下午3:19:00
	 */
	public List<SportAgainstInfoBO> getSportGameFirstEndMatch(Integer lotteryCode, List<String> matchs) {
		List<SportAgainstInfoBO> listAgainstInfoBO = sportAgainstInfoDaoMapper.findSportAgainstInfoBySystemCodeS(matchs, lotteryCode);
		return listAgainstInfoBO;
	}
	
	/**
	 * 根据彩种id和赛事id集合，查询赛事信息，用于获取最早开赛时间
	 * @author longguoyou
	 * @date 2017年11月9日
	 * @param lotteryCode
	 * @param matchs
	 * @return
	 */
	public List<SportAgainstInfoBO> getSportGameFirstBeginMatch(Integer lotteryCode, List<String> matchs){
		List<SportAgainstInfoBO> listMatch = sportAgainstInfoDaoMapper.findFirstBeginTimeOfSportAgainstInfo(matchs, lotteryCode);
		return listMatch;
	}
	/**
	 * 
	 * @Description: 从投注内容中，得到系统赛事编号
	 * @param detailVOs
	 * @return
	 * @author wuLong
	 * @date 2017年3月24日 上午10:05:58
	 */
	public static List<String> getMatchsByContent(List<OrderDetailVO> detailVOs) {
		Set<String> set = new HashSet<String>();
		for(OrderDetailVO orderDetailVO : detailVOs){
			getMatchIds(set, orderDetailVO.getPlanContent());
		}
		List<String> list = new ArrayList<String>(set);
		return list;
	}

	/**
	 * 获得系统编号
	 * @author longguoyou
	 * @date 2017年4月11日
	 * @param set
	 * @param content
	 */
	public static void getMatchIds(Set<String> set, String content) {
		String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(content);//解析投注详情
		if(!ObjectUtil.isBlank(contents)){
			String cont= contents[0].replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);//解析对阵编号
			//161128001_R[+1](3@1.57,0@2.27)#161128002_S(1@1.89,0@4.21)|161128003_Z(0@4.21)|161128004_R[-2](3@3.33)_Z(0@4.21)^2_1,3_1
			String[] con = cont.split(SymbolConstants.DOUBLE_SLASH+SymbolConstants.VERTICAL_BAR);
			if(cont.indexOf(SymbolConstants.UNDERLINE)>-1){//混投
				for(String a : con){
					set.add(a.substring(0,a.indexOf(SymbolConstants.UNDERLINE)));
				}
			}else{//让球：161128001[+1](3@1.57,0@2.27)|161128002[+1](1@1.89,0@4.21)|161128003[+1](0@4.21)^3_1
				  //其它：161128001(3@1.57,0@2.27)|161128002(1@1.89,0@4.21)|161128003(0@4.21)^3_1
				for(String a : con){
					if(a.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
						set.add(a.substring(0,a.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT)));
					}else{
						set.add(a.substring(0,a.indexOf(SymbolConstants.PARENTHESES_LEFT)));
					}
				}
			}
		}
	}
	/**
	 * 验证赛事状态
	 * @author longguoyou
	 * @date 2017年4月6日
	 * @param lotteryCode
	 * @param sportAgainstInfoBO
	 * @return
	 */
	protected ResultBO<?> verifyAgainstInfoStatus(Integer lotteryCode, SportAgainstInfoBO sportAgainstInfoBO){
		LotteryEnum.Lottery lottery = LotteryEnum.Lottery.getLottery(lotteryCode);
		switch (lottery) {
		//老足彩
		case ZC6:
		case JQ4:
		case SFC:
		case ZC_NINE:
		case BJDC:
		case SFGG:
		case CHP:
		case FNL:
			if(!sportAgainstInfoBO.getMatchStatus().equals((short)Constants.NUM_9)){
				return ResultBO.err(MessageCodeConstants.AGAINST_MATCH_IS_ILLEGAL_SERVICE, sportAgainstInfoBO.getSystemCode());
			}
			break;
		//竞技彩
		case BB:
		case FB:
			if(!sportAgainstInfoBO.getMatchStatus().equals((short)Constants.NUM_9)){
				return ResultBO.err(MessageCodeConstants.AGAINST_MATCH_IS_ILLEGAL_SERVICE, sportAgainstInfoBO.getSystemCode());
			}
			break;
		default:
			break;
		}
		return ResultBO.ok();
	}
	
	/**
	 * 
	 * @Description: 根据投注内容，得到每场对阵赛事选了哪些子玩法
	 * @param content 内容
	 * @param lotteryChidCode 子彩种id
	 * @return
	 * @author wuLong
	 * @date 2017年3月29日 下午4:07:18
	 */
	protected static Map<Long, Integer[]> getSystemCodeVsLotterys(String content,Integer lotteryChidCode,Map<String, Long> mapSportAgainstInfoBO){
		Map<Long, Integer[]> maps = new HashMap<>();//对阵编号 = 选择的玩法
		if(!ObjectUtil.isBlank(content)){
			String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(content);//解析投注详情
			if(JCZQConstants.checkLotteryId(lotteryChidCode)){
				if(lotteryChidCode.equals(JCZQConstants.ID_FHT)){
					String cont= contents[0].replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);//解析对阵编号
					//161128001_R[+1](3@1.57,0@2.27)#161128002_S(1@1.89,0@4.21)|161128003_Z(0@4.21)|161128004_R[-2](3@3.33)_Z(0@4.21)^2_1,3_1
					String[] con = cont.split(SymbolConstants.DOUBLE_SLASH+SymbolConstants.VERTICAL_BAR);
					for(String a : con){
						String[] b = a.split(SymbolConstants.DOUBLE_SLASH+SymbolConstants.UNDERLINE);
						Integer[] lot = new Integer[b.length-1];
						String systemCode = null;
						for(int i = 0,len = b.length;i<len;i++){
							if(i==0){systemCode =b[i]; continue;}
							else if(b[i].indexOf("R")>-1){lot[i-1] = JCZQConstants.ID_RQS;}
							else if(b[i].indexOf("S")>-1){lot[i-1] = JCZQConstants.ID_JCZQ;}
							else if(b[i].indexOf("Q")>-1){lot[i-1] = JCZQConstants.ID_FBF;}
							else if(b[i].indexOf("Z")>-1){lot[i-1] = JCZQConstants.ID_FZJQ;}
							else if(b[i].indexOf("B")>-1){lot[i-1] = JCZQConstants.ID_FBCQ;}
						}
						maps.put(Long.valueOf(systemCode), lot);
						if(logger.isInfoEnabled()){logger.info("对阵对应选择了哪些玩法：systemCode["+systemCode+"],lotterys["+lot+"]");}
					}
				}else{
					for(String a : mapSportAgainstInfoBO.keySet()){
						maps.put(Long.valueOf(a), new Integer[]{lotteryChidCode});
					}
				}
			}else if(JCLQConstants.checkLotteryId(lotteryChidCode)){
				if(lotteryChidCode.equals(JCLQConstants.ID_JCLQ_HHGG)){
					String cont= contents[0].replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);//解析对阵编号
					//161128001_R[+1](3@1.57,0@2.27)#161128002_S(1@1.89,0@4.21)|161128003_Z(0@4.21)|161128004_R[-2](3@3.33)_Z(0@4.21)^2_1,3_1
					String[] con = cont.split(SymbolConstants.DOUBLE_SLASH+SymbolConstants.VERTICAL_BAR);
					for(String a : con){
						String[] b = a.split(SymbolConstants.DOUBLE_SLASH+SymbolConstants.UNDERLINE);
						Integer[] lot = new Integer[b.length-1];
						String systemCode = null;
						for(int i = 0,len = b.length;i<len;i++){
							if(i==0){systemCode =b[i]; continue;}
							else if(b[i].indexOf("R")>-1){lot[i-1] = JCLQConstants.ID_JCLQ_RF;}
							else if(b[i].indexOf("S")>-1){lot[i-1] = JCLQConstants.ID_JCLQ_SF;}
							else if(b[i].indexOf("C")>-1){lot[i-1] = JCLQConstants.ID_JCLQ_SFC;}
							else if(b[i].indexOf("D")>-1){lot[i-1] = JCLQConstants.ID_JCLQ_DXF;}
						}
						if(logger.isInfoEnabled()){logger.info("对阵对应选择了哪些玩法：systemCode["+systemCode+"],lotterys["+lot+"]");}
						maps.put(mapSportAgainstInfoBO.get(systemCode), lot);
						if(logger.isInfoEnabled()){logger.info("对阵对应选择了哪些玩法：systemCode["+systemCode+"],lotterys["+lot+"]");}
					}
				}else{
					for(String systemCode : mapSportAgainstInfoBO.keySet()){
						maps.put(mapSportAgainstInfoBO.get(systemCode), new Integer[]{lotteryChidCode});
					}
				}
			}else if(BJDCConstants.checkLotteryId(lotteryChidCode)){
				for(String systemCode : mapSportAgainstInfoBO.keySet()){
					maps.put(mapSportAgainstInfoBO.get(systemCode), new Integer[]{lotteryChidCode});
				}
			}
		}
		return maps;
	}
	
	/**
	 * 处理判断让球胜平负与胜平负投注内容区别合法性
	 * @author longguoyou
	 * @date 2017年4月10日
	 * @param betContent
	 * @param lotteryChildCode
	 * @return
	 */
	protected ResultBO<?> dealRspfAndSpfDifferent(String betContent, Integer lotteryChildCode){
		if(JCZQConstants.checkLotteryId(lotteryChildCode)){
			//竞彩足球
			if((lotteryChildCode != JCZQConstants.ID_RQS && lotteryChildCode != JCZQConstants.ID_FHT) && betContent.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}else if(JCLQConstants.checkLotteryId(lotteryChildCode)){
			//竞彩篮球
			if((lotteryChildCode != JCLQConstants.ID_JCLQ_RF && lotteryChildCode != JCLQConstants.ID_JCLQ_HHGG && lotteryChildCode != JCLQConstants.ID_JCLQ_DXF) && betContent.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}else if(BJDCConstants.checkLotteryId(lotteryChildCode)){
			//北京单场
			if(lotteryChildCode != BJDCConstants.ID_RQS && lotteryChildCode != BJDCConstants.ID_SFC && betContent.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 验证对阵场次编号合法性：1)存在性。 2)一致性，与投注内容是否一致。
	 * @author longguoyou
	 * @date 2017年5月5日
	 * @param orderInfoVO
	 * @param orderDetailVO
	 * @return
	 */
	protected ResultBO<?> verifySystemCodes(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO){
		//从投注内容中，得到系统赛事编号
		List<String> matchs = getMatchsByContent(Arrays.asList(orderDetailVO));
		if(!ObjectUtil.isBlank(matchs)){
//			String[] buyScreenArr  = orderInfoVO.getBuyScreen().split(SymbolConstants.COMMA);
			//1、存在性验证 （奖金优化时，会出现方案赛事编号，和传参不完全一致问题）
//			if(buyScreenArr.length != matchs.size()){
//				return ResultBO.err(MessageCodeConstants.SYSTEM_CODE_PARAM_ILLEGAL_SERVICE);
//			}
			//2、一致性验证
			String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());//解析投注详情
			if(contents[0].contains(SymbolConstants.NUMBER_SIGN)){
				contents[0] = contents[0].replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
			}
			//投注内容的场次编号
			String[] betContentArr = FormatConversionJCUtil.stringSplitArray(contents[0], SymbolConstants.VERTICAL_BAR, true);
			
			for(int j = 0; j < betContentArr.length; j++){
				//赛事编号加到Set集
				if(orderInfoVO.getLotteryCode() == Lottery.BJDC.getName() || orderInfoVO.getLotteryCode() == Lottery.SFGG.getName()){
					if(betContentArr[j].contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
						orderInfoVO.getMatchSet().add(betContentArr[j].substring(0, betContentArr[j].indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT)));
					}else{
						orderInfoVO.getMatchSet().add(betContentArr[j].substring(0, betContentArr[j].indexOf(SymbolConstants.PARENTHESES_LEFT)));
					}
				}else{//竞技彩
					orderInfoVO.getMatchSet().add(betContentArr[j].substring(0, 10));
				}
			}
		}
		return ResultBO.ok();
	}
	
	
	/**
	 * 验证对阵子玩法，销售状态
	 * @author longguoyou
	 * @date 2017年6月7日
	 * @param maps 每场对阵赛事选了哪些子玩法如：[170204152,{30002,30003}]
	 * @return
	 */
	protected ResultBO<?> verifyChildStatus(Map<Long, Integer[]> maps, OrderInfoVO orderInfoVO){
//		String lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
//		LotteryBO lotteryBO = redisUtil.getObj(CacheConstants.LOTTERY_TYPE + lotteryCode, new LotteryBO());
		if(logger.isInfoEnabled()){logger.info("验证对阵子玩法销售状态：start");}
		LotteryBO lotteryBO = orderInfoVO.getLotteryBO();
		List<LotChildBO> listChildBO = ObjectUtil.isBlank(lotteryBO)?null:lotteryBO.getListLotChildBO();
		for(Long a : maps.keySet()){
			Integer[] lotterys = maps.get(a);
			for(Integer lottery : lotterys){
				if(!ObjectUtil.isBlank(listChildBO)){
					for(LotChildBO lotChild : listChildBO){
						if(lotChild.getLotteryChildCode().equals(lottery) && lotChild.getSaleStatus() == Constants.NUM_0){
							return ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_IS_STOP_SALE_SERVICE);
						}
					}
				}
			}
		}
		if(logger.isInfoEnabled()){logger.info("验证对阵子玩法销售状态：end");}
		return ResultBO.ok();
	}

}

