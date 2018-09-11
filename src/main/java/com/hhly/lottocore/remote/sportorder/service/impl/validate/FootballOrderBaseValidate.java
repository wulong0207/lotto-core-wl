/**
 * 
 */
package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.SportEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.cms.sportmgr.bo.SportStatusFBBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;

/**
 * @author scott
 * @desc  竞技足球公共验证方法集
 */
public abstract class FootballOrderBaseValidate extends SportsOrderValidate {
	
	private static Logger logger = LoggerFactory.getLogger(FootballOrderBaseValidate.class);
	
	@Resource(name = "jcDataService")
	private IJcDataService jcDataService;
	
	/**
	 * 验证过关方式相关：包括，格式合法性、内容合法性、最高串关
	 * @author longguoyou
	 * @date 2017年2月15日 上午11:21:27
	 * @param orderInfoVO
	 * @return
	 */
	protected ResultBO<?> varifyPasswayRelated(String[] betContent, OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO){
		if(!ObjectUtil.isBlank(orderDetailVO)){
			if(ObjectUtil.isBlank(orderDetailVO.getPlanContent())){
				return ResultBO.err(MessageCodeConstants.PLAN_CONTENT_IS_NULL_FIELD);
			}
			if(ObjectUtil.isBlank(betContent) || betContent.length <= 2){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			//验证过关方式格式(是否包含下划线)
			ResultBO<?> resultBO = super.varifyPasswayPattern(betContent, orderInfoVO);
			if(resultBO.isError()){
				return resultBO;
			}
			//验证过关方式内容
			resultBO = super.varifyPasswayContent(betContent, orderInfoVO, orderDetailVO.getPlanContent());
			if(resultBO.isError()){
				return resultBO;
			}
			//验证过关方式允许过关(混合)
			resultBO = this.varifyPasswayPermit(betContent, orderDetailVO, orderInfoVO);
			if(resultBO.isError()){
				return resultBO;
			}
		}
		return ResultBO.ok();
	}
	
	
	/**
	 * 验证过关方式：允许过关方式
	 * @author longguoyou
	 * @date 2017年2月21日 下午7:13:25
	 * @param orderInfoVO
	 * @return
	 */
	protected ResultBO<?> varifyPasswayPermit(String[] betContent, OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO){
		if(orderDetailVO.getLotteryChildCode() == JCZQConstants.ID_FHT || orderDetailVO.getLotteryChildCode() == JCLQConstants.ID_JCLQ_HHGG){//混合
			return verifyMultiplePasswayPermit(betContent, orderDetailVO);
		}
		return ResultBO.ok();
	}
	
	/**
	 * 混合过关
	 * @author longguoyou
	 * @date 2017年3月20日
	 * @param betContent
	 * @param orderDetailVO
	 * @return
	 */
	private ResultBO<?> verifyMultiplePasswayPermit(String[] betContent, OrderDetailVO orderDetailVO){
		if(ObjectUtil.isBlank(betContent) || betContent.length < 2){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		//取得过关方式内容字符串
		String content = betContent[1];
		String[] betMixArr = FormatConversionJCUtil.stringSplitArray(betContent[0], SymbolConstants.UNDERLINE, false);
		for(int i = 1 ; i < betMixArr.length; i++){
			ResultBO<?> result = null;
			if(betMixArr[i].indexOf(SymbolConstants.NUMBER_SIGN) > -1){
				for(String danContent : betMixArr[i].split(SymbolConstants.NUMBER_SIGN)){
					result = deal(danContent.substring(0, 1), content);
				}
			}else{
				result = deal(betMixArr[i].substring(0, 1), content);
				if(result.isError()){
					return result;
				}
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 处理判断过关方式
	 * @author longguoyou
	 * @date 2017年3月7日 上午10:20:09
	 * @param pass 投注内容解析得到子玩法 R、S、B、Q...
	 * @param content 投注内容解析得到过关方式 2_1,3_1,4_1
	 * @return
	 */
	private ResultBO<?> deal(String pass, String content){
		if(!ObjectUtil.isBlank(pass)){
			if(pass.equals(SportEnum.SportFbSubWay.JCZQ_Q.getValue()) || pass.equals(SportEnum.SportFbSubWay.JCZQ_B.getValue())){
				for(String passway : content.split(SymbolConstants.COMMA)){
					if( JCZQConstants.PASSWAY_LIMIT_FOUR < Integer.parseInt(passway.substring(0,1))){
						return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE, JCZQConstants.PASSWAY_LIMIT_FOUR);
					}
				}
			}
			if(pass.equals(SportEnum.SportFbSubWay.JCZQ_Z.getValue())){
				for(String passway : content.split(SymbolConstants.COMMA)){
					if( JCZQConstants.PASSWAY_LIMIT_SIX < Integer.parseInt(passway.substring(0,1))){
						return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE, JCZQConstants.PASSWAY_LIMIT_SIX);
					}
				}
			}
			if(pass.equals(SportEnum.SportFbSubWay.JCZQ_S.getValue()) || pass.equals(SportEnum.SportFbSubWay.JCZQ_R.getValue())){
				for(String passway : content.split(SymbolConstants.COMMA)){
					if( JCZQConstants.PASSWAY_LIMIT_EIGHT < Integer.parseInt(passway.substring(0,1))){
						return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE, JCZQConstants.PASSWAY_LIMIT_EIGHT);
					}
				}
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 竞足对阵赛事验证
	 * @author longguoyou
	 * @date 2017年3月13日
	 * @param orderInfoVO
	 * @param orderDetailVO
	 * @param listAgainstInfoBO 对阵信息
	 * @param betContent 解析投注内容后的字符串数组， [0]投注的详细内容.[1]过关方式(1/多个).[2]倍数(单个)
	 * @return
	 */
	protected ResultBO<?> verifyAgainstInfo(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, List<SportAgainstInfoBO> listAgainstInfoBO, String[] betContent){
		logger.debug("场次对阵开关、子玩法验证     begin......");
		
		ResultBO<?> resultBo = null;
		//封装成对阵系统编号  ： 对阵id
		Map<String, Long> mapSportAgainstInfoBO = new HashMap<String, Long>();
		//用户选了那几场对阵
		List<Long> sportAgainstInfoIds = new ArrayList<Long>();
		//对阵编号
		List<String> systemCodes = new ArrayList<String>();
		for(SportAgainstInfoBO sportAgainstInfoBO : listAgainstInfoBO){
			String lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
			resultBo = super.verifyAgainstInfoStatus(Integer.valueOf(lotteryCode), sportAgainstInfoBO);
			if(resultBo.isError()){
				return resultBo;
			}
			mapSportAgainstInfoBO.put(sportAgainstInfoBO.getSystemCode(), sportAgainstInfoBO.getId());
			sportAgainstInfoIds.add(sportAgainstInfoBO.getId());
			systemCodes.add(sportAgainstInfoBO.getSystemCode());
		}
		if(logger.isInfoEnabled()){logger.info("竞足解析mapSportAgainstInfoBO:"+mapSportAgainstInfoBO);}
		//根据投注内容，得到每场对阵赛事选了哪些子玩法
		Map<Long, Integer[]> maps = super.getSystemCodeVsLotterys(orderDetailVO.getPlanContent(), orderDetailVO.getLotteryChildCode(),mapSportAgainstInfoBO);
		if(logger.isInfoEnabled()){logger.info("竞足对阵对应子玩法maps:"+maps);}
		//list集合 对阵自增id ，查询对阵对应的子玩法的销售状态信息
//		List<SportStatusFBBO> sportStatusFBPOs = sportStatusFBDaoMapper.getBysSportAgainstInfoIds(sportAgainstInfoIds);
//		Map<Long, SportStatusFBBO> sportStatusFBPOsMap = new HashMap<Long, SportStatusFBBO>();
//		for(SportStatusFBBO sportStatusFBPO : sportStatusFBPOs){
//			sportStatusFBPOsMap.put(sportStatusFBPO.getSportAgainstInfoId(), sportStatusFBPO);
//		}
		
		//通过缓存查询
		Map<Long, SportStatusFBBO> sportStatusFBPOsMap = getSportStatusFBBO(systemCodes);
		if(logger.isInfoEnabled()){logger.info("竞足缓存销售状态sportStatusFBPOsMap:"+sportStatusFBPOsMap);}
		boolean isSingleWay = false;
		if(betContent[1].length() == 3 && betContent[1].equals("1_1")){//是否单关判断
			isSingleWay = true;
		}
		//TODO 新过关方式：1_1,2_1,3_1改造处
		if(betContent[1].contains("1_1")){
			isSingleWay = true;
		}
		//验证对阵子玩法，销售状态
		resultBo = verifyAgainstChildStatus(maps, sportStatusFBPOsMap, isSingleWay);
		if(resultBo.isError()){
			return resultBo;
		}
		//验证子玩法销售状态
		resultBo = super.verifyChildStatus(maps, orderInfoVO);
		if(resultBo.isError()){
			return resultBo;
		}
		logger.debug("场次对阵开关、子玩法验证   end......");
		return ResultBO.ok();
	}

	/**
	 * 通过redis缓存获取对阵子玩法销售状态
	 * @author longguoyou
	 * @date 2017年7月12日
	 * @param systemCodes
	 * @return
	 */
	private Map<Long, SportStatusFBBO> getSportStatusFBBO(List<String> systemCodes){
		Map<String, JczqOrderBO> mapJczqDataBO = jcDataService.findJczqOrderBOBySystemCodes(systemCodes);
		Map<Long, SportStatusFBBO> sportStatusFBPOsMap = new HashMap<Long, SportStatusFBBO>();
		for(String systemCode : systemCodes){
			JczqOrderBO jczqOrderBO = mapJczqDataBO.get(systemCode);
			if(!ObjectUtil.isBlank(jczqOrderBO)){
				Long system = jczqOrderBO.getId();
				SportStatusFBBO sportStatusFBBO = new SportStatusFBBO();
				sportStatusFBBO.setId(system);
				sportStatusFBBO.setStatusGoal(jczqOrderBO.getStatusGoal().shortValue());
				sportStatusFBBO.setStatusHfWdf(jczqOrderBO.getStatusHfWdf().shortValue());
				sportStatusFBBO.setStatusLetWdf(jczqOrderBO.getStatusLetWdf().shortValue());
				sportStatusFBBO.setStatusScore(jczqOrderBO.getStatusScore().shortValue());
				sportStatusFBBO.setStatusWdf(jczqOrderBO.getStatusWdf().shortValue());
				sportStatusFBPOsMap.put(Long.valueOf(systemCode), sportStatusFBBO);
			}else{
				sportStatusFBPOsMap.put(Long.valueOf(systemCode), null);
			}
		}
		return sportStatusFBPOsMap;
	}
	
    /**
     * 验证对阵子玩法销售状态
     * @author longguoyou
     * @date 2017年6月7日
     * @param maps <statusFBID,lotterys> --> <子玩法销售状态表主键，子玩法数组>
     * @param sportStatusFBPOsMap <systemCode,sportStatusFBPO> --> <对阵系统编号，子玩法销售状态对象>
     * @param isSingleWay
     * @return
     */
	private ResultBO<?> verifyAgainstChildStatus(Map<Long, Integer[]> maps, Map<Long, SportStatusFBBO> sportStatusFBPOsMap, boolean isSingleWay) {
		ResultBO<?> resultBo = null;
		for(Long a : maps.keySet()){
			Integer[] lotterys = maps.get(a);
			SportStatusFBBO sportStatusFBPO = sportStatusFBPOsMap.get(a);
			if(ObjectUtil.isBlank(sportStatusFBPO)){
				return ResultBO.err(MessageCodeConstants.DATA_NOT_FOUND_SYS);
			}else{
				for(Integer l : lotterys){
					if(l.equals(JCZQConstants.ID_JCZQ)){
						if(isSingleWay){
							if(sportStatusFBPO.getStatusWdf() == Constants.NUM_2 || sportStatusFBPO.getStatusWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusFBPO.getStatusWdf() == Constants.NUM_3 || sportStatusFBPO.getStatusWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(l.equals(JCZQConstants.ID_RQS)){
						if(isSingleWay){
							if(sportStatusFBPO.getStatusLetWdf() == Constants.NUM_2 || sportStatusFBPO.getStatusLetWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusFBPO.getStatusLetWdf() == Constants.NUM_3 || sportStatusFBPO.getStatusLetWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(l.equals(JCZQConstants.ID_FBF)){
						if(isSingleWay){
							if(sportStatusFBPO.getStatusScore() == Constants.NUM_2 || sportStatusFBPO.getStatusScore() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusFBPO.getStatusScore() == Constants.NUM_3 || sportStatusFBPO.getStatusScore() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(l.equals(JCZQConstants.ID_FZJQ)){
						if(isSingleWay){
							if(sportStatusFBPO.getStatusGoal() == Constants.NUM_2 || sportStatusFBPO.getStatusGoal() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusFBPO.getStatusGoal() == Constants.NUM_3 || sportStatusFBPO.getStatusGoal() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(l.equals(JCZQConstants.ID_FBCQ)){
						if(isSingleWay){
							if(sportStatusFBPO.getStatusHfWdf() == Constants.NUM_2 || sportStatusFBPO.getStatusHfWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusFBPO.getStatusHfWdf() == Constants.NUM_3 || sportStatusFBPO.getStatusHfWdf() == Constants.NUM_4){
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
	 * @param betContent 
	 * @return
	 */
	public abstract  ResultBO<?> verifyChildBetContent(OrderDetailVO orderDetailVO, String[] betContent, OrderInfoVO orderInfoVO) ;

}
