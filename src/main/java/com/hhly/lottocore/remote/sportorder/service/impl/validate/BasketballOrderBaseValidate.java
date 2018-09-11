/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.lottocore.persistence.sport.dao.SportStatusBBDaoMapper;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SportEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.cms.sportmgr.bo.SportStatusBBBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;

/**
 * @author scott
 * @desc  竞技篮球公共验证方法集
 */
public abstract class BasketballOrderBaseValidate extends SportsOrderValidate {
	
	private static Logger logger = LoggerFactory.getLogger(BasketballOrderBaseValidate.class);

	@Autowired
	private SportStatusBBDaoMapper sportStatusBBDaoMapper;
	
	/**
	 *
	 * @author yuanshangbing
	 * @date 2017年2月16日 上午11:21:27
	 * @desc 验证过关方式相关：包括，格式合法性、内容合法性、最高串关
	 * @param orderInfoVO
	 * @return
	 */
	protected ResultBO<?> varifyPasswayRelated(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO){
		if(!ObjectUtil.isBlank(orderDetailVO)){
			if(ObjectUtil.isBlank(orderDetailVO.getPlanContent())){
				return ResultBO.err(MessageCodeConstants.PLAN_CONTENT_IS_NULL_FIELD);
			}
			String[] betContent = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());
			if(ObjectUtil.isBlank(betContent) || betContent.length <= 2){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			//验证过关方式内容
			ResultBO<?> resultBO = super.varifyPasswayContent(betContent,orderInfoVO,orderDetailVO.getPlanContent());
			if(resultBO.isError()){
				return resultBO;
			}
			//验证过关方式格式
			resultBO = super.varifyPasswayPattern(betContent, orderInfoVO);
			if(resultBO.isError()){
				return resultBO;
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 格式验证
	 * @param orderDetailVO
	 * @return
	 */
	protected ResultBO<?> validateGameFormate(OrderDetailVO orderDetailVO,int lottoId){
		//获取投注内容 如 161128301[+11.5](3@1.57,0@2.27)|161128302[+1.5](3@1.89,0@4.21)|161128303[+8.5](0@4.21)
		String gameContent = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent())[0];
		//解析后： [161128301[+11.5](3@1.57,0@2.27)],[161128302[+1.5](3@1.89,0@4.21)],[161128303[+8.5](0@4.21)]
		String gameDetails[] = FormatConversionJCUtil.betContentDetailsAnalysis(gameContent);
		for(String content : gameDetails){
			//解析后：3@1.57,0@2.27
			String gameDetail = FormatConversionJCUtil.singleGameBetContentSubstring(content);
			//解析后：[3@1.57],[0@2.27]
			String singleStr [] = FormatConversionJCUtil.optionBetContentAnalysis(gameDetail);
			for(String str : singleStr){
				//解析后：[3],[1.57]
				String format = FormatConversionJCUtil.singleOptionBetContentAnalysis(str)[0];
				boolean flag = JCLQConstants.checkJCLQBetContentGame(lottoId,format);
				if(!flag){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_WITH_PASSWAY_SERVICE);
				}
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 验证单式过关方式
	 * @param orderDetailVO
	 * @return
	 */
	protected ResultBO<?> validateGameContent(OrderDetailVO orderDetailVO){
		//允许过关方式验证
		String content = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent())[1];
		for(String passway : content.split(SymbolConstants.COMMA)){
			//单式胜负，让分，大小最高八串一，胜分差四串1
			if(orderDetailVO.getLotteryChildCode().equals(JCLQConstants.ID_JCLQ_SF)||orderDetailVO.getLotteryChildCode().equals(JCLQConstants.ID_JCLQ_RF)
					|| orderDetailVO.getLotteryChildCode().equals(JCLQConstants.ID_JCLQ_DXF)){
				if( JCLQConstants.PASSWAY_LIMIT_EIGHT < Integer.parseInt(passway.substring(0,passway.indexOf(SymbolConstants.UNDERLINE)))){
					return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE, JCLQConstants.PASSWAY_LIMIT_EIGHT);
				}
			}
			if(orderDetailVO.getLotteryChildCode().equals(JCLQConstants.ID_JCLQ_SFC)){
				if( JCLQConstants.PASSWAY_LIMIT_FOUR < Integer.parseInt(passway.substring(0,passway.indexOf(SymbolConstants.UNDERLINE)))){
					return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE, JCLQConstants.PASSWAY_LIMIT_FOUR);
				}
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 竞彩篮球 混合过关允许过关判断
	 * @author longguoyou
	 * @date 2017年3月20日
	 * @param orderDetailVO
	 * @return
	 */
	protected ResultBO<?> varifyPasswayPermit(OrderDetailVO orderDetailVO, String[] passways){
//		if(ObjectUtil.isBlank(passways) || passways.length < 2){
//			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
//		}
		String content = passways[1];
		if(orderDetailVO.getLotteryChildCode().equals(JCLQConstants.ID_JCLQ_HHGG)){//混合
			String[] betMixArr = FormatConversionJCUtil.stringSplitArray(orderDetailVO.getPlanContent(), SymbolConstants.UNDERLINE, false);
			for(int i = 1 ; i < betMixArr.length; i++){
				if(betMixArr[i].substring(0, 1).equals(SportEnum.SportBBSubWay.JCLQ_S.getValue()) || betMixArr[i].substring(0, 1).equals(SportEnum.SportBBSubWay.JCLQ_R.getValue())||
						betMixArr[i].substring(0, 1).equals(SportEnum.SportBBSubWay.JCLQ_D.getValue())){
					for(String passway : content.split(SymbolConstants.COMMA)){
						if(JCLQConstants.PASSWAY_LIMIT_EIGHT < Integer.parseInt(passway.substring(0,passway.indexOf(SymbolConstants.UNDERLINE)))){
							return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE, JCLQConstants.PASSWAY_LIMIT_EIGHT);
						}
					}
				}
				if(betMixArr[i].substring(0, 1).equals(SportEnum.SportBBSubWay.JCLQ_C.getValue())){
					for(String passway : content.split(SymbolConstants.COMMA)){
						if(JCLQConstants.PASSWAY_LIMIT_SIX < Integer.parseInt(passway.substring(0,passway.indexOf(SymbolConstants.UNDERLINE)))){
							return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE, JCLQConstants.PASSWAY_LIMIT_SIX);
						}
					}
				}
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 竞篮对阵赛事验证
	 * @author longguoyou
	 * @date 2017年4月6日
	 * @param orderInfoVO
	 * @param orderDetailVO
	 * @param listAgainstInfoBO
	 * @return
	 */
	protected ResultBO<?> verifyBBAgainstInfo(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, List<SportAgainstInfoBO> listAgainstInfoBO, String[] betContent){
		logger.debug("竞篮：场次对阵开关、子玩法验证     begin......");
		ResultBO<?> resultBo = null;
		//封装成对阵系统编号  ： 对阵id
		Map<String, Long> mapSportAgainstInfoBO = new HashMap<String, Long>();
		//用户选了那几场对阵
		List<Long> sportAgainstInfoIds = new ArrayList<Long>();
		String lotteryCode = null;
		for(SportAgainstInfoBO sportAgainstInfoBO : listAgainstInfoBO){
			lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
			resultBo = super.verifyAgainstInfoStatus(Integer.valueOf(lotteryCode), sportAgainstInfoBO);
			if(resultBo.isError()){
				return resultBo;
			}
			mapSportAgainstInfoBO.put(sportAgainstInfoBO.getSystemCode(), sportAgainstInfoBO.getId());
			sportAgainstInfoIds.add(sportAgainstInfoBO.getId());
		}
		if(logger.isInfoEnabled()){logger.info("竞篮解析mapSportAgainstInfoBO:"+mapSportAgainstInfoBO);}
		//根据投注内容，得到每场对阵赛事选了哪些子玩法
		Map<Long, Integer[]> maps = super.getSystemCodeVsLotterys(orderDetailVO.getPlanContent(), orderDetailVO.getLotteryChildCode(),mapSportAgainstInfoBO);
		if(logger.isInfoEnabled()){logger.info("竞篮对阵对应子玩法maps:"+maps);}
		//list集合 对阵自增id ，查询对阵对应的子玩法的销售状态信息
		List<SportStatusBBBO> sportStatusBBPOs = sportStatusBBDaoMapper.getBysSportAgainstInfoIds(sportAgainstInfoIds);
		if(logger.isInfoEnabled()){logger.info("竞篮缓存销售状态sportStatusFBPOsMap:"+sportStatusBBPOs);}
		Map<Long, SportStatusBBBO> sportStatusFBPOsMap = new HashMap<Long, SportStatusBBBO>();
		for(SportStatusBBBO sportStatusBBPO : sportStatusBBPOs){
			sportStatusFBPOsMap.put(sportStatusBBPO.getSportAgainstInfoId(), sportStatusBBPO);
		}
		if(logger.isInfoEnabled()){logger.info("竞篮缓存销售状态sportStatusFBPOsMap:"+sportStatusFBPOsMap);}
		boolean isSingleWay = false;
		if(betContent[1].length() == 3 && betContent[1].equals("1_1")){//是否单关判断
			isSingleWay = true;
		}
		//验证对阵子玩法，销售状态
		resultBo = verifyAgainstChildStatus(maps, sportStatusFBPOsMap, isSingleWay);
		if(resultBo.isError()){
			return resultBo;
		}
		//验证子玩法销售状态
		resultBo = super.verifyChildStatus(maps, orderInfoVO);
		logger.debug("竞篮：场次对阵开关、子玩法验证   end......");
		if(!ObjectUtil.isBlank(resultBo) && resultBo.isError()){
			return resultBo;
		}
		return ResultBO.ok();
	}
	
	/**
	 * 验证对阵子玩法销售状态
	 * @author longguoyou
	 * @date 2017年8月28日
	 * @param maps
	 * @param sportStatusBBPOsMap
	 * @param isSingleWay 是否单关
	 * @return
	 */
	private ResultBO<?> verifyAgainstChildStatus(Map<Long, Integer[]> maps, Map<Long, SportStatusBBBO> sportStatusBBPOsMap, boolean isSingleWay) {
		ResultBO<?> resultBo = null;
		for(Long a : maps.keySet()){
			Integer[] lotterys = maps.get(a);
			SportStatusBBBO sportStatusBBPO = sportStatusBBPOsMap.get(a);
			if(ObjectUtil.isBlank(sportStatusBBPO)){
				return ResultBO.err(MessageCodeConstants.DATA_NOT_FOUND_SYS);
			}else{
				for(Integer l : lotterys){
					if(l.equals(JCLQConstants.ID_JCLQ_DXF)){
						if(isSingleWay){
							if(sportStatusBBPO.getStatusBigSmall() == Constants.NUM_2 || sportStatusBBPO.getStatusBigSmall() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBBPO.getStatusBigSmall() == Constants.NUM_3 || sportStatusBBPO.getStatusBigSmall() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(l.equals(JCLQConstants.ID_JCLQ_RF)){
						if(isSingleWay){
							if(sportStatusBBPO.getStatusLetWf() == Constants.NUM_2 || sportStatusBBPO.getStatusLetWf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBBPO.getStatusLetWf() == Constants.NUM_3 || sportStatusBBPO.getStatusLetWf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(l.equals(JCLQConstants.ID_JCLQ_SF)){
						if(isSingleWay){
							if(sportStatusBBPO.getStatusWf() == Constants.NUM_2 || sportStatusBBPO.getStatusWf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBBPO.getStatusWf() == Constants.NUM_3 || sportStatusBBPO.getStatusWf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(l.equals(JCLQConstants.ID_JCLQ_SFC)){
						if(isSingleWay){
							if(sportStatusBBPO.getStatusScoreWf() == Constants.NUM_2 || sportStatusBBPO.getStatusScoreWf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBBPO.getStatusScoreWf() == Constants.NUM_3 || sportStatusBBPO.getStatusScoreWf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}
				}

			}
			if(!ObjectUtil.isBlank(resultBo) && resultBo.isError()){
				return resultBo;
			}
		}
		return ResultBO.ok();
	}
	
	
	/**
	 * 
	 * @param orderDetailVO
	 * @return
	 */
	public abstract  ResultBO<?> verifyChildBetContent(OrderDetailVO orderDetailVO, String[] betContent,OrderInfoVO orderInfoVO) ;

}
