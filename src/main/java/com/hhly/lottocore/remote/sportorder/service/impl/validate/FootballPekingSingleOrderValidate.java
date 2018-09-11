package com.hhly.lottocore.remote.sportorder.service.impl.validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.Validator;
import com.hhly.lottocore.persistence.sport.dao.SportStatusBJDaoMapper;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.cms.sportmgr.bo.SportStatusBJBO;
import com.hhly.skeleton.lotto.base.order.vo.OrderDetailVO;
import com.hhly.skeleton.lotto.base.order.vo.OrderInfoVO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;

/**
 * 北京单场订单入库验证
 * @author longguoyou
 * @date 2017年3月10日
 * @compay 益彩网络科技有限公司
 */
@Component("footballPekingSingleOrderValidate")
public class FootballPekingSingleOrderValidate extends SportsOrderValidate implements Validator {

	private static Logger logger = LoggerFactory.getLogger(FootballPekingSingleOrderValidate.class);
	
	@Autowired
	private SportStatusBJDaoMapper sportStatusBJDaoMapper;
	
	@Resource(name = "jcDataService")
	private IJcDataService jcDataService;
	
	@Override
	public ResultBO<?> handle(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO, List<?> list) {
		String[] betContent = FormatConversionJCUtil.singleBetContentAnalysis(orderDetailVO.getPlanContent());
		ResultBO<?> resultBO = null;
		//1、针对单一玩法正则表达式验证投注内容(没有混投)
		resultBO = super.varifySingleWayByRegexp(orderDetailVO, betContent[0]);
		if(resultBO.isError()){
			return resultBO;
		}
		//2、投注内容验证
		resultBO = this.verifyChildBetContent(orderDetailVO, orderInfoVO, list);
		if(resultBO.isError()){
			return resultBO;
		}
		//缓存取数据
		List<SportAgainstInfoBO> listAgainstInfoBO  = getSportAgainstInfoBOs(Arrays.asList(orderDetailVO), Integer.valueOf(String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3)));
		//3、对阵状态验证
		resultBO = this.verifyBJDCAgainstInfo(orderInfoVO, orderDetailVO, listAgainstInfoBO, false);
		if(resultBO.isError()){
			return resultBO;
		}
		//5、验证对阵编号合法性 //此处是验证外层的赛事编号和明细的是否一一匹配。单式上传可能会出现匹配不一致的情况，所以忽悠
		resultBO = super.verifySystemCodes(orderInfoVO, orderDetailVO);
		if (resultBO.isError()) {
			return resultBO;
		}
		//6、限号、解析注数
		return this.verifyBetContent(betContent, orderDetailVO, orderInfoVO.getLotteryCode(), list,orderInfoVO.getIsSingleOrder());
	}
	
    /**
     * 北京单场  验证处理中心：投注内容
     * @author longguoyou
     * @date 2017年3月10日
     * @param orderDetailVO
     * @param orderInfoVO
     * @return
     */
	private ResultBO<?> verifyChildBetContent(OrderDetailVO orderDetailVO, OrderInfoVO orderInfoVO, List<?> list) {
		ResultBO<?> result = null;
		if(ObjectUtil.isBlank(orderDetailVO)){
			return ResultBO.err(MessageCodeConstants.OBJECT_IS_NULL);
		}
		String content = orderDetailVO.getPlanContent();
		String[] flagGJ   = content.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.UP_CAP);
		//验证contentType与投注内容关系合法性
		result = super.varifyContentType(flagGJ, orderDetailVO, orderInfoVO);
		if(result.isError()){
			return result;
		}
		//截取数组元素包含#好，是胆拖投注
		if(content.contains(SymbolConstants.NUMBER_SIGN)){
			return verifyEachDanTuo(orderDetailVO.getLotteryChildCode(),content, orderInfoVO);
		}
		//否则，是单式/复式
		return verifySingle(orderDetailVO.getLotteryChildCode(),content, orderInfoVO);//单式/复式投注内容公用
	}
	
	/**
	 * 验证单式投注内容合法性<br>
	 * e.g. 161128001[+1](3@1.57,0@2.27)|161128002[+1](1@1.89,0@4.21)|161128003[+1](0@4.21)^3_1
	 * @author longguoyou
	 * @date 2017年3月10日
	 * @param content
	 * @return
	 */
	private ResultBO<?> verifySingle(Integer lotteryCode, String content, OrderInfoVO orderInfoVO){
		String[] splitStrs = content.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.UP_CAP);
		//按^截取数组的长度小于2，表示投注内容不合法
		if(splitStrs.length < Constants.NUM_2){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		ResultBO<?> result = null;
		result = super.dealRspfAndSpfDifferent(content, lotteryCode);
		if(result.isError()){
			return result;
		}
		String[] contents = splitStrs[0].split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.VERTICAL_BAR);
		for(int i = 0; i < contents.length; i++){
			result = deal(lotteryCode, contents[i]);
			if(result.isError()){
				return result;
			}
		}
		//验证过关方式合法性
		return verifyPassway(splitStrs, orderInfoVO, content);
	}
	
	/**
	 * 验证复式投注内容合法性<br>
	 * e.g. 161128001[+1](3@1.57,0@2.27)|161128002[+1](1@1.89,0@4.21)|161128003[+1](0@4.21)^2_1
	 * @author longguoyou
	 * @date 2017年3月10日
	 * @param content
	 * @return
	 */
	private ResultBO<?> verifyMultiple(String content){
		return ResultBO.ok();
	}
	
	/**
	 * 胆拖投注内容合法性<br>
	 * e.g. 161128001[+1](3@1.57,0@2.27)#161128002[+1](1@1.89,0@4.21)|161128003[+1](0@4.21)^2_1,3_1
	 * @author longguoyou
	 * @date 2017年3月10日
	 * @param content
	 * @return
	 */
	private ResultBO<?> verifyEachDanTuo(Integer lotteryId, String content, OrderInfoVO orderInfoVO){
		String[] splitStrs = content.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.UP_CAP);
		//按^截取数组的长度小于2，表示投注内容不合法
		if(splitStrs.length < Constants.NUM_2){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		ResultBO<?> result = null;
		String[] contents = splitStrs[0].split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.VERTICAL_BAR);
		for(int i = 0; i < contents.length; i++){
			if(contents[i].contains(SymbolConstants.NUMBER_SIGN)){
				contents[i] = contents[i].replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
				result = deal(lotteryId, contents[i]);
			}else{
				result = deal(lotteryId, contents[i]);
			}
			if(result.isError()){
				return result;
			}
		}
		//验证过关方式合法性
		return verifyPassway(splitStrs, orderInfoVO, content);
	}
	
	/**
	 * 高级上传
	 * e.g. 161128001[+1](3@1.57,0@2.27)|161128002[+1](1@1.89,0@4.21)|161128003[+1](0@4.21)^2_1,3_1^868
	 * @author longguoyou
	 * @date 2017年3月10日
	 * @param content
	 * @return
	 */
	private ResultBO<?> verifyUserUpload(Integer lotteryCode, String content, OrderInfoVO orderInfoVO){
		String[] splitStrs = content.split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.UP_CAP);
		if(splitStrs.length < Constants.NUM_3){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		ResultBO<?> result = null;
		//验证投注内容
		String[] contents = splitStrs[0].split(SymbolConstants.DOUBLE_SLASH + SymbolConstants.VERTICAL_BAR);
		for(int i = 0; i < contents.length; i++){
			if(contents[i].contains(SymbolConstants.NUMBER_SIGN)){
				contents[i] = contents[i].replace(SymbolConstants.NUMBER_SIGN, SymbolConstants.VERTICAL_BAR);
				result = deal(lotteryCode, contents[i]);
			}else{
				result = deal(lotteryCode, contents[i]);
			}
			if(result.isError()){
				return result;
			}
		}
		//验证过关方式
		result = verifyPassway(splitStrs, orderInfoVO, content);
		if(result.isError()){
			return result;
		}
		//投注倍数验证
		if(Integer.valueOf(splitStrs[2]) > Constants.NUM_1000){
			return ResultBO.err(MessageCodeConstants.MULTIPLE_LIMIT_SERVICE);
		}
		return ResultBO.ok();
	}
	
	/**
	 * 验证过关方式合法性
	 * @author longguoyou
	 * @date 2017年3月10日
	 * @param betContents
	 * @param orderInfoVO
	 * @param betContent
	 * @return
	 */
	private ResultBO<?> verifyPassway(String[] betContents, OrderInfoVO orderInfoVO, String betContent){
		for(String passway : betContents[1].split(SymbolConstants.COMMA)){
			if(verifyLimitPassway(passway, orderInfoVO.getLotteryChildCode())){
				return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE);
			}
		}
		return varifyPasswayContent(betContents, orderInfoVO, betContent);
	}
	
	/**
	 * 验证最高串关<br>
	 * 让球胜平负最多支持15串1，进球数、上下单双、半全场最多支持6串1，比分最高支持3串1；（胜负过关最低3串1）<br>
	 * @author longguoyou
	 * @date 2017年8月7日
	 * @param passway
	 * @param childCode
	 * @return
	 */
	private boolean verifyLimitPassway(String passway, Integer childCode){
		if(BJDCConstants.verifyLimitPassway(passway, childCode)){
			return true;
		}
		return false;
	}
	
	/**
	 * 处理判断
	 * @author longguoyou
	 * @date 2017年3月10日
	 * @param lotteryCode
	 * @param contents 字符串 e.g. 161128002[+1](1@1.89,0@4.21)
	 * @return
	 */
	private ResultBO<?> deal(Integer lotteryCode, String contents){
		String[] betContent = null;
		String  eachContent = FormatConversionJCUtil.stringSubstringToString(contents, SymbolConstants.PARENTHESES_LEFT, SymbolConstants.PARENTHESES_RIGHT, false);
		betContent  = eachContent.split(SymbolConstants.COMMA);
		for(int j = 0; j < betContent.length; j++){
			if(!BJDCConstants.checkJCZQBetContentGame(lotteryCode, betContent[j].substring(0, betContent[j].indexOf(SymbolConstants.AT)))){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 北京单场验证赛事
	 * @author longguoyou
	 * @date 2017年4月7日
	 * @param orderInfoVO
	 * @param orderDetailVO
	 * @param listAgainstInfoBO
	 * @return
	 */
	protected ResultBO<?> verifyBJDCAgainstInfo(OrderInfoVO orderInfoVO, OrderDetailVO orderDetailVO, List<SportAgainstInfoBO> listAgainstInfoBO, boolean isSingleWay){
		logger.debug("北单：场次对阵开关、子玩法验证     begin......");
		ResultBO<?> resultBo = null;
		//封装成对阵系统编号  ： 对阵id
		Map<String, Long> mapSportAgainstInfoBO = new HashMap<String, Long>();
		//用户选了那几场对阵
		List<Long> sportAgainstInfoIds = new ArrayList<Long>();
		String lotteryCode = null;
		//对阵编号
		List<String> systemCodes = new ArrayList<String>();
		for(SportAgainstInfoBO sportAgainstInfoBO : listAgainstInfoBO){
			lotteryCode = String.valueOf(orderInfoVO.getLotteryCode()).substring(0, 3);
			resultBo = super.verifyAgainstInfoStatus(Integer.valueOf(lotteryCode), sportAgainstInfoBO);
			if(resultBo.isError()){
				return resultBo;
			}
			mapSportAgainstInfoBO.put(sportAgainstInfoBO.getSystemCode(), sportAgainstInfoBO.getId());
			sportAgainstInfoIds.add(sportAgainstInfoBO.getId());
			systemCodes.add(sportAgainstInfoBO.getSystemCode());
		}
		if(logger.isInfoEnabled()){logger.info("北单解析mapSportAgainstInfoBO:"+mapSportAgainstInfoBO);}
		//根据投注内容，得到每场对阵赛事选了哪些子玩法
		Map<Long, Integer[]> maps = super.getSystemCodeVsLotterys(orderDetailVO.getPlanContent(), orderDetailVO.getLotteryChildCode(),mapSportAgainstInfoBO);
		if(logger.isInfoEnabled()){logger.info("北单对阵对应子玩法maps:"+maps);}
		//list集合 对阵自增id ，查询对阵对应的子玩法的销售状态信息
		List<SportStatusBJBO> sportStatusBJPOs = sportStatusBJDaoMapper.getBysSportAgainstInfoIds(sportAgainstInfoIds);
		Map<Long, SportStatusBJBO> sportStatusBJPOsMap = new HashMap<Long, SportStatusBJBO>();
		for(SportStatusBJBO sportStatusBJBO : sportStatusBJPOs){
			sportStatusBJPOsMap.put(sportStatusBJBO.getSportAgainstInfoId(), sportStatusBJBO);
		}
		if(logger.isInfoEnabled()){logger.info("北单缓存销售状态sportStatusBJPOsMap:"+sportStatusBJPOsMap);}
		
//		Map<Long, SportStatusBJBO> sportStatusBJPOsMap = getSportStatusBJBO(systemCodes, orderInfoVO);
		
		//验证对阵子玩法，销售状态
		resultBo = verifyAgainstBjdcChildStatus(maps, sportStatusBJPOsMap, isSingleWay);
		if(resultBo.isError()){
			return resultBo;
		}
		
		logger.debug("北单：场次对阵开关、子玩法验证   end......");
		return ResultBO.ok();
	}
	
	/**
	 * 通过redis缓存获取对阵子玩法销售状态
	 * @author longguoyou
	 * @date 2017年8月l4日
	 * @param systemCodes
	 * @param orderInfoVO
	 * @return
	 */
	private Map<Long, SportStatusBJBO> getSportStatusBJBO(List<String> systemCodes, OrderInfoVO orderInfoVO){
		Map<Long, SportStatusBJBO> sportStatusBJPOsMap = new HashMap<Long, SportStatusBJBO>();
		if(!ObjectUtil.isBlank(systemCodes)){
			for(int i = 0; i < systemCodes.size(); i++){
				BjDaoBO bjDataBO = jcDataService.findBjDataBOBySystemCode(systemCodes.get(i), String.valueOf(orderInfoVO.getLotteryCode()));
				if(ObjectUtil.isBlank(bjDataBO)){
					SportStatusBJBO sportStatusBJBO = new SportStatusBJBO();
					Long systemCode = bjDataBO.getId();
					sportStatusBJBO.setId(systemCode);
					sportStatusBJBO.setStatusGoal(bjDataBO.getStatusGoal().shortValue());//总进球
					sportStatusBJBO.setStatusHfWdf(bjDataBO.getStatusHfWdf().shortValue());//半全场胜平负
					sportStatusBJBO.setStatusLetWdf(bjDataBO.getStatusLetWdf().shortValue());//让胜平负
					sportStatusBJBO.setStatusLetWf(bjDataBO.getStatusLetWf().shortValue());//胜负彩
					sportStatusBJBO.setStatusScore(bjDataBO.getStatusScore().shortValue());//比分
					sportStatusBJBO.setStatusUdSd(bjDataBO.getStatusUdSd().shortValue());//上下单双
					sportStatusBJPOsMap.put(systemCode, sportStatusBJBO);
				}
			}
		}
		return sportStatusBJPOsMap;
	}
	
	/**
	 * 验证北京单场子玩法销售状态
	 * @author longguoyou
	 * @date 2017年7月27日
	 * @param maps
	 * @param sportStatusBJPOsMap
	 * @param isSingleWay
	 * @return
	 */
	private ResultBO<?> verifyAgainstBjdcChildStatus(Map<Long, Integer[]> maps, Map<Long, SportStatusBJBO> sportStatusBJPOsMap, boolean isSingleWay) {
		//通过缓存查询
		ResultBO<?> resultBo = null;
		for(Long a : maps.keySet()){
			Integer[] lotterys = maps.get(a);
			SportStatusBJBO sportStatusBJPO = sportStatusBJPOsMap.get(a);
			if(ObjectUtil.isBlank(sportStatusBJPO)){
				return ResultBO.err(MessageCodeConstants.DATA_NOT_FOUND_SYS);
			}else{
				for(Integer lot : lotterys){
					if(lot.equals(BJDCConstants.ID_FZJQ)){
						if(isSingleWay){
							if(sportStatusBJPO.getStatusGoal() == Constants.NUM_2 || sportStatusBJPO.getStatusGoal() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBJPO.getStatusGoal() == Constants.NUM_3 || sportStatusBJPO.getStatusGoal() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(lot.equals(BJDCConstants.ID_FBCQ)){
						if(isSingleWay){
							if(sportStatusBJPO.getStatusHfWdf() == Constants.NUM_2 || sportStatusBJPO.getStatusHfWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBJPO.getStatusHfWdf() == Constants.NUM_3 || sportStatusBJPO.getStatusHfWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(lot.equals(BJDCConstants.ID_RQS)){
						if(isSingleWay){
							if(sportStatusBJPO.getStatusLetWdf() == Constants.NUM_2 || sportStatusBJPO.getStatusLetWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBJPO.getStatusLetWdf() == Constants.NUM_2 || sportStatusBJPO.getStatusLetWdf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(lot.equals(BJDCConstants.ID_FBF)){
						if(isSingleWay){
							if(sportStatusBJPO.getStatusScore() == Constants.NUM_2 || sportStatusBJPO.getStatusScore() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBJPO.getStatusScore() == Constants.NUM_3 || sportStatusBJPO.getStatusScore() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(lot.equals(BJDCConstants.ID_SXDX)){
						if(isSingleWay){
							if(sportStatusBJPO.getStatusUdSd() == Constants.NUM_2 || sportStatusBJPO.getStatusUdSd() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBJPO.getStatusUdSd() == Constants.NUM_3 || sportStatusBJPO.getStatusUdSd() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}
					}else if(lot.equals(BJDCConstants.ID_SFC)){
						if(isSingleWay){
							if(sportStatusBJPO.getStatusLetWf() == Constants.NUM_2 || sportStatusBJPO.getStatusLetWf() == Constants.NUM_4){
								resultBo = ResultBO.err(MessageCodeConstants.LOTTERY_CHILD_CODE_STATUS_IS_ILLEGAL_SERVICE);
								break;
							}
						}else{
							if(sportStatusBJPO.getStatusLetWf() == Constants.NUM_3 || sportStatusBJPO.getStatusLetWf() == Constants.NUM_4){
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
	
}
