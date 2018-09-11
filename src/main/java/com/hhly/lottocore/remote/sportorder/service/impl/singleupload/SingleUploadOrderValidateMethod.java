package com.hhly.lottocore.remote.sportorder.service.impl.singleupload;

import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum.Lottery;
import com.hhly.skeleton.base.common.SportEnum;
import com.hhly.skeleton.base.constants.*;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.RegularValidateUtil;
import com.hhly.skeleton.lotto.base.order.bo.SingleUploadDeatailBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBO;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadJCBetDetail;
import com.hhly.skeleton.lotto.base.singleupload.bo.SingleUploadSuccessResultBO;
import com.hhly.skeleton.lotto.base.singleupload.vo.SingleUploadJCVO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqDaoBO;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 单式上传一些公共验证方法
 * 
 * @author longguoyou
 * @date 2017年6月13日
 * @compay 益彩网络科技有限公司
 */
public class SingleUploadOrderValidateMethod {

	private static Logger logger = LoggerFactory.getLogger(SingleUploadOrderValidateMethod.class);
	
	@Resource(name = "jcDataService")
	private IJcDataService jcDataService;
	
	/**
	 * 验证官方赛事编号, 销售截止时间...
	 * 
	 * @author longguoyou
	 * @date 2017年6月13日
	 * @return
	 */
	protected ResultBO<?> verifyOfficalMatchCodes(SingleUploadJCVO jczqSingleUploadVO) {
		List<String> listMatchCodes = getListMatchFromParam(jczqSingleUploadVO.getSelectedMatchs());
		if (!ObjectUtil.isBlank(listMatchCodes)) {
			for (int i = 0; i < listMatchCodes.size(); i++) {
				JczqDaoBO jczqDaoBO = jcDataService.findSingleUpMatchDataByOfficialCode(listMatchCodes.get(i));
				// 1、 验证场次是否存在
				if (ObjectUtil.isBlank(jczqDaoBO)) {
					return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
				}
				// 2、 验证该场次销售时间是否截止
				if (jczqDaoBO.getSaleEndDate().before(new Date())) {
					return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
				}
			}
		}
		return ResultBO.ok();
	}
	
	/**
	 * 验证北京单场官方赛事编号，销售截止时间
	 * @author longguoyou
	 * @date 2017年8月14日
	 * @param listBjDataBo
	 * @return
	 */
	protected ResultBO<?> verifyBjNum(List<BjDaoBO> listBjDaoBO){
		if(!ObjectUtil.isBlank(listBjDaoBO)){
			for(int i = 0; i < listBjDaoBO.size(); i++){
				if(ObjectUtil.isBlank(listBjDaoBO.get(i))){
					return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
				}
				if(listBjDaoBO.get(i).getSaleEndDate().before(new Date())){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_CONTAIN_END_MATCH);
				}
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 单式上传选择场次验证<br>
	 * 如果选择场数大于等于8场，小于等于15场，让胜平负最高8串1，小于8场的，就是默认[场数_1],其它类推。<br>
	 * 
	 * @author longguoyou
	 * @date 2017年6月13日
	 * @param jczqSingleUploadVO
	 * @param txtBetContent
	 *            txt读取一行的投注内容
	 * @param line
	 *            用于获取销售截止时间
	 * @return
	 */
	protected ResultBO<?> verify(SingleUploadJCVO jczqSingleUploadVO, String txtBetContent, int line) {
		Lottery lottery = Lottery.getLottery(Integer.valueOf(jczqSingleUploadVO.getLotteryCode()));
		switch (lottery) {
		case FB:
			return explainTxtBetContent(jczqSingleUploadVO, txtBetContent, line);
		case SFC:
		case ZC_NINE:
		case JQ4:
		case ZC6:
			return explainTxtBetContentOld(jczqSingleUploadVO, txtBetContent, line);
		case BB:
			break;
		case BJDC:
		case SFGG:
			return explainTxtBetContent(jczqSingleUploadVO, txtBetContent, line);
		}
		return ResultBO.ok();
	}

	/**
	 * 老足彩：包含十四场胜平负、任九、六场半全场、四场进球<br>
	 * 解析过程中：验证场次边界值、场次合法性、投注内容合法性、倍数
	 * @author longguoyou
	 * @date 2017年7月17日
	 * @param jczqSingleUploadVO
	 * @param txtBetContent
	 * @param line
	 * @return
	 */
	private ResultBO<?> explainTxtBetContentOld(SingleUploadJCVO jczqSingleUploadVO, String txtBetContent, int line) {
		//胜负彩：13031003310130（默认1注、1倍） / 3,1,1,1,1,1,1,1,1,1,1,1,1（默认1注、1倍）/13031003310130_10（默认1注、10倍）/ 3,1,1,1,1,1,1,1,1,1,1,1,1_10（默认1注、10倍）
		//任九：    13**10*33*013*（默认1注、1倍） / 3,1,*,*,1,1,*,1,1,*,1,1,1,*（默认1注、1倍）/13**10*33*013*_10（默认1注、10倍）/ 3,1,*,*,1,1,*,1,1,*,1,1,1,*_10（默认1注、10倍）
		String[] betContents = null;// 解析投注内容
		String multiple = null;//倍数
		int valid;//有效场次
		ResultBO<?> result = null;
		if(txtBetContent.contains(SymbolConstants.UNDERLINE)){//包含倍数
			String[] betContent = txtBetContent.split(SymbolConstants.UNDERLINE);
			if(betContent[0].contains(SymbolConstants.COMMA)){//包含逗号
				betContents = betContent[0].split(SymbolConstants.COMMA);
			}else{//不包含逗号
				result = getBetContent(jczqSingleUploadVO.getLotteryCode(), jczqSingleUploadVO.getLotteryChildCode(), betContent[0]);
				if(result.isError()){return result;}
				betContents = (String[])result.getData();
			}
			multiple = betContent[1];
		}else{//不包含倍数
			if(txtBetContent.contains(SymbolConstants.COMMA)){
				betContents = txtBetContent.split(SymbolConstants.COMMA);
			}else{
				result = getBetContent(jczqSingleUploadVO.getLotteryCode(), jczqSingleUploadVO.getLotteryChildCode(), txtBetContent);
				if(result.isError()){return result;}
				betContents = (String[])result.getData();
			}
			multiple = Constants.NUM_1 + SymbolConstants.ENPTY_STRING;
		}
		
		//1、 验证倍数合法性
		result = verifyMultiple(multiple);
		if (result.isError()) {
			return result;
		}
		result = reduceInvalid(betContents, jczqSingleUploadVO.getTransfer());
		if(result.isError()){return result;}
		valid = (Integer)result.getData();
		//2、边界值合法性
		if(jczqSingleUploadVO.getLotteryCode() == Lottery.SFC.getName()){//十四场
			//1、场数14
			if(betContents.length != Constants.NUM_14){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE); 
			}
			//2、有效场数14
			if(valid != Constants.NUM_14){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE); 
			}
		}else if(jczqSingleUploadVO.getLotteryCode() == Lottery.ZC_NINE.getName()){//任九
			//1、场数14
			if(betContents.length != Constants.NUM_14){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE); 
			}
			//2、有效场数9
			if(valid != Constants.NUM_9){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE); 
			}
		}else if(jczqSingleUploadVO.getLotteryCode() == Lottery.JQ4.getName()){//四场进球
			
		}else{//六场半全场
			
		}
		
		//3、验证投注内容
		result = verifyContents(jczqSingleUploadVO, betContents, txtBetContent);
		if (result.isError()) {
			return result;
		}
		//4、 组装正确内容返回前端
		SingleUploadSuccessResultBO successResultBO = new SingleUploadSuccessResultBO(line, txtBetContent, null,
				Integer.valueOf(multiple), valid);
		successResultBO.setBetDetails(explainBetJCBetDetail(jczqSingleUploadVO, betContents));
		return ResultBO.ok(successResultBO);
	}

	/**竞彩足球<br>
	 * 解析txt投注内容, 解析过程中： 验证场次边界值、场次合法性、过关方式合法性、投注内容合法性
	 * 
	 * @author longguoyou
	 * @date 2017年6月14日
	 * @param jczqSingleUploadVO
	 * @param txtBetContent
	 * @param line
	 * @return 返回字符串数组
	 */
	private ResultBO<?> explainTxtBetContent(SingleUploadJCVO jczqSingleUploadVO, String txtBetContent, int line) {
		// 例 30111 或3,0,1,1,1 （默认5串1、1倍）
		// ##111|3_1,10 或#,#,1,1,1|3_1,10（默认3串1、10倍）
		// 1、先判断是否带过关方式和倍数
		// 2、再判断是否包含逗号“,”，决定采取何种截取方案
		// 3、再根据方案，决定是根据长度或分割符截取
		String[] betContent = null;// 解析投注内容
		String passway = null;// 过关方式
		String multiple = null;// 倍数
		int valid;//有效场次
		ResultBO<?> result = null;
		// 判断内容有无空格
		if (ObjectUtil.isBlank(txtBetContent))
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_IS_NULL_FIELD);
		if (txtBetContent.indexOf(SymbolConstants.SPACE) != -1)
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);

		if (txtBetContent.contains(SymbolConstants.VERTICAL_BAR)) {
			String[] betContents = txtBetContent.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
			// 出现两个以上"|"
			if (betContents.length != 2) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			if (betContents[0].contains(SymbolConstants.COMMA)) {// 投注内容包含逗号:
																	// #,#,1,1,1|3_1,10
				betContent = betContents[0].split(SymbolConstants.COMMA);
			} else {// 不包含:##111|3_1,10
				result = getBetContent(jczqSingleUploadVO.getLotteryCode(), jczqSingleUploadVO.getLotteryChildCode(),
						betContents[0]);
				if (result.isError()) {
					return result;
				}
				betContent = (String[]) result.getData();
			}
			// 投注内容后部分,无","
			if (!betContents[1].contains(SymbolConstants.COMMA)) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			try {
				passway = betContents[1].split(SymbolConstants.COMMA)[0];
				multiple = betContents[1].split(SymbolConstants.COMMA)[1];
			} catch (Exception e) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		} else {// 无过关方式和倍数
			if (txtBetContent.contains(SymbolConstants.COMMA)) {// 包含逗号：3,0,1,1,1
				betContent = txtBetContent.split(SymbolConstants.COMMA);
			} else {// 不包含：30111
				result = getBetContent(jczqSingleUploadVO.getLotteryCode(), jczqSingleUploadVO.getLotteryChildCode(),
						txtBetContent);
				if (result.isError()) {
					return result;
				}
				betContent = (String[]) result.getData();
			}
			result = reduceInvalid(betContent,jczqSingleUploadVO.getTransfer());
			if(result.isError()){return result;}
//			passway = getPassway(String.valueOf(result.getData()), jczqSingleUploadVO.getLotteryChildCode());
			passway = String.valueOf(result.getData()) + SymbolConstants.UNDERLINE + Constants.NUM_1;
			multiple = Constants.NUM_1 + SymbolConstants.ENPTY_STRING;
		}
		//投注内容同时包含“#”和“*”, 则报错
        if(txtBetContent.contains(SymbolConstants.NUMBER_SIGN) && txtBetContent.contains(SymbolConstants.STAR)){
        	return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
        }
		result = reduceInvalid(betContent,jczqSingleUploadVO.getTransfer());
		if(result.isError()){return result;}
		valid = (Integer)result.getData();
		// 场次边界值、合法性验证(选择场次与投注内容是否一致)
		result = verifySelectedNum(betContent.length, jczqSingleUploadVO.getSelectedMatchs());
		if (result.isError()) {
			return result;
		}
		// 验证倍数合法性
		result = verifyMultiple(multiple);
		if (result.isError()) {
			return result;
		}
		// 过关方式验证
		if(JCZQConstants.checkLotteryId(jczqSingleUploadVO.getLotteryChildCode()) || JCLQConstants.checkLotteryId(jczqSingleUploadVO.getLotteryChildCode())){
			result = verifySelectedFbPassway(jczqSingleUploadVO.getLotteryChildCode(), passway, txtBetContent, valid);
		}else{
			result = verifySelectedBJDCPassway(jczqSingleUploadVO.getLotteryChildCode(), passway, txtBetContent, valid);
		}
		if (result.isError()) {
			return result;
		}
		// 投注内容验证
		result = verifyContents(jczqSingleUploadVO, betContent, txtBetContent);
		if (result.isError()) {
			return result;
		}
		// 组装正确内容返回前端
		SingleUploadSuccessResultBO successResultBO = new SingleUploadSuccessResultBO(line, txtBetContent, passway,
				Integer.valueOf(multiple), valid);
		successResultBO.setBetDetails(explainBetJCBetDetail(jczqSingleUploadVO, betContent));
		return ResultBO.ok(successResultBO);
	}
	
	/**
	 * 获取过关方式：竞足使用<br>
	 * 1、大于最高串关，就为最高
	 * 2、否则，则为[有效场次_1]串关
	 * @author longguoyou
	 * @date 2017年7月11日
	 * @param valid 有效场次
	 * @return
	 */
	private String getPassway(String valid, Integer lotteryChildCode){
		String retVal = valid + SymbolConstants.UNDERLINE + Constants.NUM_1;
		if (JCZQConstants.ID_JCZQ == lotteryChildCode || JCZQConstants.ID_RQS == lotteryChildCode) {// 让胜平负/胜平负
			if(Integer.valueOf(valid) > JCZQConstants.PASSWAY_LIMIT_EIGHT)
				retVal = JCZQConstants.PASSWAY_LIMIT_EIGHT + SymbolConstants.UNDERLINE + Constants.NUM_1;
		} else if (JCZQConstants.ID_FZJQ == lotteryChildCode) {// 总进球
			if(Integer.valueOf(valid) > JCZQConstants.PASSWAY_LIMIT_SIX)
				retVal = JCZQConstants.PASSWAY_LIMIT_SIX + SymbolConstants.UNDERLINE + Constants.NUM_1;
		} else if (JCZQConstants.ID_FBCQ == lotteryChildCode || JCZQConstants.ID_FBF == lotteryChildCode) {// 半全场 /比分
			if(Integer.valueOf(valid) > JCZQConstants.PASSWAY_LIMIT_FOUR)
				retVal = JCZQConstants.PASSWAY_LIMIT_FOUR + SymbolConstants.UNDERLINE + Constants.NUM_1;
		} 
		return retVal;
	}

	/**
	 * 验证倍数合法性
	 * 
	 * @author longguoyou
	 * @date 2017年7月7日
	 * @param multiple
	 * @return
	 */
	protected ResultBO<?> verifyMultiple(String multiple) {
		if (!multiple.matches(RegularValidateUtil.REGULAR_ACCOUNT2) || Integer.valueOf(multiple) == Constants.NUM_0) {
			return ResultBO.err(MessageCodeConstants.ORDER_DETAIL_MULTIPLE_IS_ILLEGAL_SERVICE);
		}
		if (Integer.valueOf(multiple) > JCConstants.MAX_LIMIT_MULTIPLE) {
			return ResultBO.err(MessageCodeConstants.MULTIPLE_LIMIT_SERVICE);
		}
		return ResultBO.ok();
	}

	/**
	 * 获取有效投注赛事场次，减去无效占位的赛事(仅用于选择场次)
	 * 
	 * @author longguoyou
	 * @date 2017年6月29日
	 * @param betContent [55abc]
	 * @param transfer转换器 [a,3][b,1][c,0][5,#]
	 * @return
	 */
	protected ResultBO<?> reduceInvalid(String[] betContent, Map<String,String> transfer) {
		List<String> listBetContent = new ArrayList<String>();
		for (String content : betContent) {
			if(ObjectUtil.isBlank(transfer)){
				if (!content.contains(SymbolConstants.NUMBER_SIGN) && !content.contains(SymbolConstants.STAR)) {
					listBetContent.add(content);
				}
			}else{
				if(ObjectUtil.isBlank(transfer.get(content)) && !content.equals(SymbolConstants.STAR)){
					 return ResultBO.err(MessageCodeConstants.USER_CHAR_TRAN_ILIEGAL_SERVICE);
				}
				if(!content.contains(SymbolConstants.STAR) && !SymbolConstants.DOUBLE_STAR.contains(transfer.get(content)) && 
						!SymbolConstants.DOUBLE_NUMBER_SIGN.contains(transfer.get(content))){
					listBetContent.add(content);
				}
			}
		}
		return ResultBO.ok(listBetContent.size());
	}

	/**
	 * 拼装投注内容详情
	 * 
	 * @author longguoyou
	 * @date 2017年6月21日
	 * @param jczqSingleUploadVO
	 * @param betContent 读取txt文件内容，并去掉占位符后的投注内容 如：3#1--> 31
	 * @return
	 */
	protected static List<SingleUploadJCBetDetail> explainBetJCBetDetail(SingleUploadJCVO jczqSingleUploadVO,
			String[] betContent) {
		List<SingleUploadJCBetDetail> betDetails = new ArrayList<SingleUploadJCBetDetail>();
		List<String> listMatchCode = getListMatchFromParam(jczqSingleUploadVO.getSelectedMatchs());
		for (int i = 0; i < betContent.length; i++) {//如：51C, 有转换5=3,1=1,C=*
			SingleUploadJCBetDetail uploadJCBetDetail = new SingleUploadJCBetDetail();
			if (!betContent[i].contains(SymbolConstants.NUMBER_SIGN) && !betContent[i].contains(SymbolConstants.STAR)) {
				if(!ObjectUtil.isBlank(listMatchCode)){
					uploadJCBetDetail.setSessionNumber(listMatchCode.get(i));//场次编号
				}
			}
			if (ObjectUtil.isBlank(jczqSingleUploadVO.getShiftContent())) {// 无转换
				uploadJCBetDetail.setLotteryResult(betContent[i]);//投注彩果(单个场次) 如:胜平负  -> 310
			} else {// 有转换
				uploadJCBetDetail.setUserResult(betContent[i]);//用户投注彩果(单个场次) 如:胜平负  -> abc 【有转换格式时要传此值】
				if (!ObjectUtil.isBlank(jczqSingleUploadVO.getShiftContent())) {//
					uploadJCBetDetail.setLotteryResult(jczqSingleUploadVO.getTransfer().get(betContent[i]));
					if(betContent[i].equals(SymbolConstants.STAR)){
						uploadJCBetDetail.setLotteryResult(betContent[i]);
					}
				}
			}
			uploadJCBetDetail.setPlay(jczqSingleUploadVO.getPlayCode());
			betDetails.add(uploadJCBetDetail);
		}
		return betDetails;
	}

	/**
	 * 根据传参，获取赛事编号List集合
	 * 
	 * @author longguoyou
	 * @date 2017年6月22日
	 * @param selectedMatchInfo
	 * @return
	 */
	protected static List<String> getListMatchFromParam(String selectedMatchInfo) {
		return ObjectUtil.isBlank(selectedMatchInfo)?null:Arrays.asList(selectedMatchInfo.split(SymbolConstants.OBLIQUE_LINE));
	}

	/**
	 * 通过用户选择比赛信息,key:彩期编号 value:赛事编号集合list
	 * 
	 * @author longguoyou
	 * @date 2017年6月20日
	 * @param selectedMatchInfo
	 * @return
	 */
	protected static Map<String, List<String>> getMatchFromParam(String selectedMatchInfo) {
		Map<String, List<String>> listIssueSystemCodes = new HashMap<String, List<String>>();
		String[] sysMap = selectedMatchInfo.split(SymbolConstants.SEMICOLON);
		for (String sys : sysMap) {
			String[] issueWithMatchs = sys.split(SymbolConstants.NUMBER_SIGN);
			List<String> listSystemCodes = new ArrayList<String>();
			for (String systemCodes : issueWithMatchs[1].split(SymbolConstants.OBLIQUE_LINE)) {
				listSystemCodes.add(systemCodes);
			}
			listIssueSystemCodes.put(issueWithMatchs[0], listSystemCodes);
		}
		return listIssueSystemCodes;
	}

	/**
	 * 获取解析后内容数组(不包含，过关方式和倍数)
	 * 
	 * @author longguoyou
	 * @date 2017年6月14日
	 * @param lotteryCode
	 * @param lotteryChildCode
	 * @param txtBetContent
	 * @return
	 */
	protected ResultBO<?> getBetContent(Integer lotteryCode, Integer lotteryChildCode, String txtBetContent) {
		int length = getLength(lotteryCode, lotteryChildCode);
		if (txtBetContent.toCharArray().length % length != 0) {// 333111
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		String[] strs = new String[txtBetContent.toCharArray().length / length];
		for (int i = 0, j = 0; i < txtBetContent.toCharArray().length; j++, i = i + length) {
			strs[j] = txtBetContent.substring(i, i + length);
		}
		return ResultBO.ok(strs);
	}

	/**
	 * 验证投注内容合法性
	 * 
	 * @author longguoyou
	 * @date 2017年6月13日
	 * @param jczqSingleUploadVO
	 * @param betContent
	 *            解析后投注内容
	 * @param txtBetContent
	 *            txt读取一行的投注内容
	 * @return
	 */
	private ResultBO<?> verifyContents(SingleUploadJCVO jczqSingleUploadVO, String[] betContent, String txtBetContent) {
//		long begin = System.currentTimeMillis();
		// 1、正则表达式过一次
		// 2、循环遍历一次
		String playCode = getPlayCode(jczqSingleUploadVO);
		ResultBO<?> result = getStandarTransfer(jczqSingleUploadVO.getShiftContent(), playCode);
		Map<String, String> mapTransfer = (Map<String, String>) result.getData();// [a,3][b,1][c,0][#,#]
		if (!ObjectUtil.isBlank(betContent)) {
			String transferContent = null;
			int len = getLength(jczqSingleUploadVO.getLotteryCode(), jczqSingleUploadVO.getLotteryChildCode());
			for (int i = 0; i < betContent.length; i++) {// [a,#,c]
				if (!ObjectUtil.isBlank(mapTransfer)) {// 转换标准投注内容
					boolean flag = false;
					transferContent = mapTransfer.get(betContent[i]);
					//如果是“#”或者“*”号，并且转换器没有找到，则赋值原始值
					if(ObjectUtil.isBlank(transferContent) && (betContent[i].equals(SymbolConstants.STAR) || betContent[i].equals(SymbolConstants.NUMBER_SIGN))){
						transferContent = betContent[i];
					}
					if(!ObjectUtil.isBlank(transferContent))
						flag = true;
					if (!flag) {
						return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
					}
				} else {
					transferContent = betContent[i];
				}
				if (len != transferContent.length()) {
					try {
						logger.error("投注内容长度与规定不一致：'" + transferContent + "'"
								+ java.net.URLEncoder.encode(transferContent, "utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				result = checkBetContentGame(jczqSingleUploadVO, transferContent, playCode);
				if(result.isError()){return result;}
			}
		}
//		logger.info("循环验证投注内容耗时：" + (System.currentTimeMillis() - begin) /100+ "秒");
		return ResultBO.ok();
	}
	
	/**
	 * 检查投注内容合法性
	 * @author longguoyou
	 * @date 2017年8月14日
	 * @param jczqSingleUploadVO
	 * @param transferContent
	 * @param playCode
	 * @return
	 */
	private ResultBO<?> checkBetContentGame(SingleUploadJCVO jczqSingleUploadVO, String transferContent, String playCode){
		//竞足
		if (Lottery.FB.getName() == jczqSingleUploadVO.getLotteryCode() && !transferContent.contains(SymbolConstants.NUMBER_SIGN) && 
				!transferContent.contains(SymbolConstants.STAR) && !JCZQConstants.checkJCZQGameForSingleUpload(playCode, transferContent)) {
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		//老足
		if((Lottery.ZC_NINE.getName() == jczqSingleUploadVO.getLotteryCode() || Lottery.SFC.getName() == jczqSingleUploadVO.getLotteryCode() || 
				Lottery.JQ4.getName() == jczqSingleUploadVO.getLotteryCode() || Lottery.ZC6.getName() == jczqSingleUploadVO.getLotteryCode()) && 
				!transferContent.contains(SymbolConstants.NUMBER_SIGN) && !transferContent.contains(SymbolConstants.STAR) && 
				!OldFootballConstants.checkBetContentGame(jczqSingleUploadVO.getLotteryCode(), transferContent)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		//北京单场
		if((Lottery.BJDC.getName() == jczqSingleUploadVO.getLotteryCode() || Lottery.SFGG.getName() == jczqSingleUploadVO.getLotteryCode()) &&
				!transferContent.contains(SymbolConstants.NUMBER_SIGN) && !transferContent.contains(SymbolConstants.STAR) &&
				!BJDCConstants.checkJCZQBetContentGame(jczqSingleUploadVO.getLotteryChildCode(), transferContent)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		return ResultBO.ok();
	}
	
	/**
	 * 获取子玩法的简写名称
	 * @author longguoyou
	 * @date 2017年8月14日
	 * @param jczqSingleUploadVO
	 * @return
	 */
	private static String getPlayCode(SingleUploadJCVO jczqSingleUploadVO){
		Lottery lottery = Lottery.getLottery(Integer.valueOf(jczqSingleUploadVO.getLotteryCode()));
		switch (lottery) {
		case FB:
			return JCZQConstants.getShortName(jczqSingleUploadVO.getLotteryChildCode());
		case SFC:
		case ZC_NINE:
		case JQ4:
		case ZC6:
			return OldFootballConstants.getShortName(jczqSingleUploadVO.getLotteryCode());
		case BB:
			break;
		case BJDC:
		case SFGG:
			return BJDCConstants.getShortName(jczqSingleUploadVO.getLotteryChildCode());
		}
		return null;
	}

	/**
	 * 竞彩足球选择场次过关方式验证<br>
	 * 例 30111 或3,0,1,1,1 （默认5串1、1倍）<br>
	 * ##111|3_1,10 或#,#,1,1,1|3_1,10（默认3串1、10倍）<br>
	 * 子玩法只有： R、S、B、Q、Z <br>
	 * 注：让球胜平负玩法最多过8关，总进球数玩法最多过6关，比分和半全场胜平负玩法最多过4关。<br>
	 * 
	 * @author longguoyou
	 * @date 2017年6月14日 解析后的数组
	 * @param lotteryChildCode 子玩法
	 * @param passway 过关方式
	 * @param txtBetContent 原始投注内容 
	 * @count 有效场数
	 * @return
	 */
	private ResultBO<?> verifySelectedFbPassway(Integer lotteryChildCode, String passway, String txtBetContent,
			Integer valid) {
		//验证有效场次与指定过关方式是否一致
//		if(!passway.contains(String.valueOf(valid))){
//			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
//		}
		// 目前过关方式只支持2_1 到 8_1
		if (!passway.matches(JCZQConstants.PASSWAY_UPLOAD_REGEXN)) {
			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
		}
		//验证有效场次小于等于过关方式
		if(Integer.valueOf(passway.substring(0, passway.indexOf(SymbolConstants.UNDERLINE))) > valid){
			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
		}
		// 需要判断最高过关数
		if(JCZQConstants.verifyLimitPassway(passway, lotteryChildCode)){
			return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE);
		}
//		if (txtBetContent.contains(SymbolConstants.VERTICAL_BAR)) {
//		}
		return ResultBO.ok();
	}
	
	
	/**北京单场验证最高过关方式<br>
	 * 胜平负玩法最多支持15串1，总进球、半全场、上下单双玩法最多支持6串1，比分玩法最高支持3串1
	 * @author longguoyou
	 * @date 2017年8月14日
	 * @param lotteryChildCode 子玩法
	 * @param passway 过关方式
	 * @param txtBetContent 原始投注内容
	 * @param valid 有效场数
	 * @return
	 */
	private ResultBO<?> verifySelectedBJDCPassway(Integer lotteryChildCode, String passway, String txtBetContent,
			int valid) {
		//暂时不允许单过 ：前端没打开
		if(valid == 1){
			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
		}
		if(passway.equals("1_1")){
			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
		}
		//验证有效场次与指定过关方式是否一致
//		if(!passway.contains(String.valueOf(valid))){
//			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
//		}
		//正则表达式验证过关方式
		if (!passway.matches(BJDCConstants.PASSWAY_UPLOAD_REGEXN)) {
			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
		}
		//验证有效场次小于过关方式
		if(Integer.valueOf(passway.substring(0, passway.indexOf(SymbolConstants.UNDERLINE))) > valid){
			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
		}
		// 需要判断最高过关数
		if(BJDCConstants.verifyLimitPassway(passway, lotteryChildCode)){
			return ResultBO.err(MessageCodeConstants.PASSWAY_LIMIT_SERVICE);
		}
//		if(txtBetContent.contains(SymbolConstants.VERTICAL_BAR)){
//		}
		
		return ResultBO.ok();
	}
	
	/**
	 * 根据彩种获取截取的长度
	 * 
	 * @author longguoyou
	 * @date 2017年6月14日
	 * @param lotteryCode
	 * @return
	 */
	protected static int getLength(Integer lotteryCode, Integer lotteryChildCode) {
		Lottery lottery = Lottery.getLottery(Integer.valueOf(lotteryCode));
		switch (lottery) {
		case FB:
			if (JCZQConstants.ID_RQS == lotteryChildCode || JCZQConstants.ID_JCZQ == lotteryChildCode
					|| JCZQConstants.ID_FZJQ == lotteryChildCode || JCZQConstants.ID_FHT == lotteryChildCode) {
				return Constants.NUM_1;
			} else if (JCZQConstants.ID_FBCQ == lotteryChildCode || JCZQConstants.ID_FBF == lotteryChildCode) {
				return Constants.NUM_2;
			}
		case SFC:
		case ZC_NINE:
		case JQ4:
		case ZC6:
			return Constants.NUM_1;
		case BB:
			break;
		case BJDC:
		case SFGG:
			if (BJDCConstants.ID_RQS == lotteryChildCode || BJDCConstants.ID_SFC == lotteryChildCode
					|| BJDCConstants.ID_FZJQ == lotteryChildCode || BJDCConstants.ID_SXDX == lotteryChildCode) {
				return Constants.NUM_1;
			} else if (BJDCConstants.ID_FBCQ == lotteryChildCode || BJDCConstants.ID_FBF == lotteryChildCode) {
				return Constants.NUM_2;
			}
		}
		return 0;
	}

	/**
	 * 验证场次边界值及合法性
	 * 
	 * @author longguoyou
	 * @date 2017年6月14日
	 * @param length
	 *            解析后投注内容长度
	 * @param selectedMatchs
	 *            前端选择比赛信息
	 * @return
	 */
	private ResultBO<?> verifySelectedNum(int length, String selectedMatchs) {
		if (length > Constants.NUM_15) {
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		if (length < Constants.NUM_2) {
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}

		List<String> newList = getListMatchFromParam(selectedMatchs);

		if (length != newList.size()) {
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		return ResultBO.ok();
	}

	/**
	 * 获取用户与标准转换字符对应关系Map&ltuserChar,standarChar&gt;
	 * 
	 * @author longguoyou
	 * @date 2017年6月14日
	 * @param shiftContent
	 *            转换字符串，逗号分割
	 * @param playCode
	 *            子玩法
	 * @return
	 */
	protected static ResultBO<?> getStandarTransfer(String shiftContent, String playCode) {
		Map<String, String> map = new HashMap<String, String>();
		if (!ObjectUtil.isBlank(shiftContent) && !ObjectUtil.isBlank(playCode)) {
			String[] standarChar = SingleUploadStandarChar.getStandarChar(playCode);
			String[] userChar = shiftContent.split(SymbolConstants.COMMA);
			if (userChar.length != standarChar.length) {
				return ResultBO.err(MessageCodeConstants.USER_CHAR_TRAN_ILIEGAL_SERVICE);
			}
			for (int i = 0; i < standarChar.length; i++) {
				map.put(userChar[i], standarChar[i]);
			}
		}
		return ResultBO.ok(map);
	}
    
	//============包含场次===============================================================

	/**
	 * 检查竞足单式上传格式合法性 : →
	 * 
	 * @param bettingContent
	 */
	public boolean checkFormatSingleUpload(String bettingContent) {
		return !ObjectUtil.isBlank(bettingContent) && (bettingContent.indexOf(SymbolConstants.COLON) != -1
				|| bettingContent.indexOf(SymbolConstants.ARROW_RIGHT) != -1);
	}

	/**
	 * 验证场次编号格式
	 * 
	 * @param bettingContent
	 *            单个投注内容 6006:[彩果] , 7001→[彩果] , 7001→彩果
	 * @return
	 */
	public ResultBO<?> verifySerialNoFormat(String bettingContent, String playCode, Map<String, String> shiftMap, Integer lotteryCode) {
		// 如果是混投 判断是否转换格式
		if (playCode.equals(SportEnum.SportFbSubWay.JCZQ_M.getValue())) {
			// 如果有转换格式，先变成标准格式再进行验证
			if (!ObjectUtil.isBlank(shiftMap)) {
				if (shiftMap.containsKey(bettingContent.substring(0, 1))) {
					bettingContent = "R" + bettingContent.substring(1);
				} else {
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
			}
		}
		if(lotteryCode == Lottery.FB.getName()){//竞彩足球
			if (!bettingContent.matches(JCZQConstants.BETTING_UPLOAD_REGEXN)) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}else if(lotteryCode == Lottery.BJDC.getName()){//北京单场
			if (!bettingContent.matches(BJDCConstants.BETTING_UPLOAD_REGEXN)) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 处理判断过关方式
	 * 
	 * @param playCode
	 *            投注内容解析得到子玩法 R、S、B、Q、Z、M
	 * @param content
	 *            投注内容解析得到过关方式 2_1
	 * @return
	 */
	private ResultBO<?> verifyPasswayRule(String playCode, String content, Integer lotteryCode) {
		if (!ObjectUtil.isBlank(playCode)) {
			//北京单场 / 胜负过关
			if(lotteryCode == Lottery.BJDC.getName() || lotteryCode == Lottery.SFGG.getName()){
				if(!content.matches(BJDCConstants.PASSWAY_UPLOAD_REGEXN)){
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				if (BJDCConstants.verifyLimitPassway(content, Integer.valueOf(BJDCConstants.getLotteryChildCode(playCode)))) {
					return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
				}
			}else{
				if (!content.matches(JCZQConstants.PASSWAY_UPLOAD_REGEXN)) {
					return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
				}
				// 比分和半全场胜平负玩法最多过4关
				if (playCode.equals(SportEnum.SportFbSubWay.JCZQ_Q.getValue())
						|| playCode.equals(SportEnum.SportFbSubWay.JCZQ_B.getValue())) {
					if (JCZQConstants.PASSWAY_LIMIT_FOUR < Integer.parseInt(content.substring(0, 1))) {
						return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
					}
				}
				// 总进球数玩法最多过6关
				if (playCode.equals(SportEnum.SportFbSubWay.JCZQ_Z.getValue())) {
					if (JCZQConstants.PASSWAY_LIMIT_SIX < Integer.parseInt(content.substring(0, 1))) {
						return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
					}
				}
				// 让球胜平负玩法最多过8关 (混合过关暂时只支持胜平负和让球胜平负的混投上传方案)
				if (playCode.equals(SportEnum.SportFbSubWay.JCZQ_S.getValue())
						|| playCode.equals(SportEnum.SportFbSubWay.JCZQ_R.getValue())
						|| playCode.equals(SportEnum.SportFbSubWay.JCZQ_M.getValue())) {
					if (JCZQConstants.PASSWAY_LIMIT_EIGHT < Integer.parseInt(content.substring(0, 1))) {
						return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
					}
				}
			}
		}
		return ResultBO.ok();
	}

	/**
	 * 验证彩果玩法内容是否匹配
	 * 
	 * @param content
	 *            投注内容
	 * @param playCode
	 *            子玩法
	 * @return
	 */
	private ResultBO<?> verifyLotteryPlayContent(String content, String playCode, Map<String, String> shiftMap, Integer lotteryCode) {
		String lotteryResult = "";
		if (content.contains(SymbolConstants.COLON) && content.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)
				&& content.contains(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)) {
			lotteryResult = content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT) + 1,
					content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT));
		} else if (content.contains(SymbolConstants.ARROW_RIGHT)) {
			lotteryResult = content.substring(content.indexOf(SymbolConstants.ARROW_RIGHT) + 1);
		} else {
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		String standarChar = lotteryResult;
		// 如果有转换格式，先变成标准格式再进行验证
		if (!ObjectUtil.isBlank(shiftMap)) {
			if (shiftMap.containsKey(lotteryResult)) {
				standarChar = shiftMap.get(lotteryResult);
			} else {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		}
		if(lotteryCode == Lottery.FB.getName()){
			if (!JCZQConstants.checkJCZQGameForSingleUpload(playCode, standarChar)) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_WITH_PASSWAY_SERVICE);
			}
		}else if(lotteryCode == Lottery.BJDC.getName() || lotteryCode == Lottery.SFGG.getName()){
			if(!BJDCConstants.checkJCZQBetContentGame(Integer.valueOf(BJDCConstants.getLotteryChildCode(playCode)), standarChar)){
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_WITH_PASSWAY_SERVICE);
			}
		}else{
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_WITH_PASSWAY_SERVICE);
		}
		return ResultBO.ok(lotteryResult + "," + standarChar);
	}

	/**
	 * 竞技彩 设值
	 * 
	 * @author longguoyou
	 * @date 2017年6月29日
	 * @param singleUploadBO
	 * @param listJczqDaoBO
	 */
	public void setValueOfSaleEndTimeAndIssueCode(SingleUploadJCBO singleUploadBO, List<JczqDaoBO> listJczqDaoBO) {
		if (!ObjectUtil.isBlank(listJczqDaoBO)) {
			Collections.sort(listJczqDaoBO, new Comparator<JczqDaoBO>() {
				@Override
				public int compare(JczqDaoBO o1, JczqDaoBO o2) {
					// 降序排序
					return Long.valueOf(DateUtil.convertDateToStr(o1.getSaleEndDate(), DateUtil.DATE_FORMAT_NUM))
							.compareTo(Long
									.valueOf(DateUtil.convertDateToStr(o2.getSaleEndDate(), DateUtil.DATE_FORMAT_NUM)));
				}
			});
			singleUploadBO.setCurrentMatchIssueCode(listJczqDaoBO.get(0).getIssueCode());
			singleUploadBO.setCurrentMatchSaleEndTime(
					DateUtil.convertDateToStr(listJczqDaoBO.get(0).getSaleEndDate(), DateUtil.DEFAULT_FORMAT));
		}
	}
	
	/**
	 * 北京单场设值
	 * 
	 * @author longguoyou
	 * @date 2017年6月29日
	 * @param singleUploadBO
	 * @param listJczqDaoBO
	 */
	public void setValueOfSaleEndTimeAndIssueCodeBj(SingleUploadJCBO singleUploadBO, List<BjDaoBO> listBjDaoBO) {
		if (!ObjectUtil.isBlank(listBjDaoBO)) {
			Collections.sort(listBjDaoBO, new Comparator<BjDaoBO>() {
				@Override
				public int compare(BjDaoBO o1, BjDaoBO o2) {
					// 降序排序
					return Long.valueOf(DateUtil.convertDateToStr(o1.getSaleEndDate(), DateUtil.DATE_FORMAT_NUM))
							.compareTo(Long
									.valueOf(DateUtil.convertDateToStr(o2.getSaleEndDate(), DateUtil.DATE_FORMAT_NUM)));
				}
			});
			singleUploadBO.setCurrentMatchIssueCode(listBjDaoBO.get(0).getIssueCode());
			singleUploadBO.setCurrentMatchSaleEndTime(
					DateUtil.convertDateToStr(listBjDaoBO.get(0).getSaleEndDate(), DateUtil.DEFAULT_FORMAT));
		}
	}


	public SingleUploadDeatailBO analysisScheme(SingleUploadJCVO singleUploadJCVO, String txtBetContent, Integer id) {
		String[] betContent = null;// 解析投注内容
		String passway = null;// 过关方式
		String multiple = null;// 倍数
		ResultBO<?> result = null;
		if (txtBetContent.contains(SymbolConstants.VERTICAL_BAR)) {
			String[] betContents = txtBetContent.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
			if (betContents[0].contains(SymbolConstants.COMMA)) {// 投注内容包含逗号:
																	// #,#,1,1,1|3_1,10
				betContent = betContents[0].split(SymbolConstants.COMMA);
			} else {// 不包含:##111|3_1,10
				result = getBetContent(singleUploadJCVO.getLotteryCode(), singleUploadJCVO.getLotteryChildCode(),
						betContents[0]);
				if (result.isOK()) {
					betContent = (String[]) result.getData();
				}
			}
			passway = betContents[1].split(SymbolConstants.COMMA)[0];
			multiple = betContents[1].split(SymbolConstants.COMMA)[1];
		} else {// 无过关方式和倍数
			if (txtBetContent.contains(SymbolConstants.COMMA)) {// 包含逗号：3,0,1,1,1
				betContent = txtBetContent.split(SymbolConstants.COMMA);
			} else {// 不包含：30111
				result = getBetContent(singleUploadJCVO.getLotteryCode(), singleUploadJCVO.getLotteryChildCode(),
						txtBetContent);
				if (result.isOK()) {
					betContent = (String[]) result.getData();
				}
			}
			passway = betContent.length + SymbolConstants.UNDERLINE + Constants.NUM_1;
			multiple = Constants.NUM_1 + "";
		}
		SingleUploadDeatailBO singleUploadDeatailBO = new SingleUploadDeatailBO();
		singleUploadDeatailBO.setMultipleNum(multiple);
		singleUploadDeatailBO.setPassway(passway);
		singleUploadDeatailBO.setId(id + 1);
		String con = "";
		for (int i = 0; i < betContent.length; i++) {
			con = con.concat(betContent[i]);
		}
		singleUploadDeatailBO.setBetContent(con);
		return singleUploadDeatailBO;
	}

	/**
	 * 获取场次信息 如果是符号原样返回
	 * 
	 * @param playCode
	 *            子玩法
	 * @param content
	 *            内容
	 * @return
	 */
	private String getMatchNo(String playCode, String content, Map<String, String> shiftMap, Integer lotteryCode) {
		if(lotteryCode == Lottery.BJDC.getName() || lotteryCode == Lottery.SFGG.getName()){
			return content.substring(0, content.indexOf(SymbolConstants.ARROW_RIGHT) == -1 ? content.indexOf(SymbolConstants.COLON) : content.indexOf(SymbolConstants.ARROW_RIGHT));
		}
		String matchNo = content.substring(Constants.NUM_0, Constants.NUM_4);
		// 混合过关特殊处理 （混合过关只支持胜平负和让球胜平负的混投上传方案）
		if (playCode.equals(SportEnum.SportFbSubWay.JCZQ_M.getValue())) {
			// 如果有转换格式，先变成标准格式再进行验证
			if (!ObjectUtil.isBlank(shiftMap)) {
				if (shiftMap.containsKey(content.substring(0, 1))) {
					content = "R" + content.substring(1);
				}
			}
			if (content.contains("R")) {
				matchNo = content.substring(Constants.NUM_1, Constants.NUM_5);
			}
		}
		return matchNo;
	}

	/**
	 * 
	 * 解析投注内容
	 * 
	 * @param content
	 * @return
	 */
	public ResultBO<?> analysisContentForIncludeMatch(String content) {
		String[] bettings = null;// 解析投注内容
		String passway = null;// 过关方式
		String multiple = null;// 倍数
		if (content.contains(SymbolConstants.VERTICAL_BAR)) {
			String[] betContent = content.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
			if (betContent[0].contains(SymbolConstants.COMMA)) {// 1001→3,2002→1,#,4004→3|3_1,100
				bettings = betContent[0].split(SymbolConstants.COMMA);
			} else if (betContent[0].contains(SymbolConstants.OBLIQUE_LINE)) {// 1001:[31]/2002:[12]/3003:[10]|3_1,100
				bettings = betContent[0].split(SymbolConstants.OBLIQUE_LINE);
			} else {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			try {
				passway = betContent[1].split(SymbolConstants.COMMA)[0];
				multiple = betContent[1].split(SymbolConstants.COMMA)[1];
			} catch (Exception e) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		} else {// 无过关方式和倍数
			if (content.contains(SymbolConstants.COMMA)) {// 1001→3,2002→1,#,4004→3
				bettings = content.split(SymbolConstants.COMMA);
			} else if (content.contains(SymbolConstants.OBLIQUE_LINE)) {// 1001:[31]/2002:[12]/3003:[10]
				bettings = content.split(SymbolConstants.OBLIQUE_LINE);
			} else {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			passway = bettings.length + SymbolConstants.UNDERLINE + Constants.NUM_1;
			multiple = Constants.NUM_1 + "";
		}
		SingleUploadDeatailBO singleUploadDeatailBO = new SingleUploadDeatailBO();
		singleUploadDeatailBO.setMultipleNum(multiple);
		singleUploadDeatailBO.setPassway(passway);
		singleUploadDeatailBO.setBetContent(content);
		return ResultBO.ok(singleUploadDeatailBO);
	}

	/**
	 * 竞彩足球-验证单式上传单条内容 （包含场次）
	 * 
	 * @param content
	 *            内容
	 *            1001:[31]/2002:[12]/3003:[10]/4004:[23]/5005:[23]/6006:[1A]/
	 *            7001:[3A]|4_1,100
	 *            1001→3,2002→1,#,4004→3,5005→3,6006→1,7001→3|6_1,100
	 * @param playCode
	 *            玩法code R、S、B、Q、Z、M
	 * @param playLength
	 *            投注内容长度
	 * @param shiftMap
	 *            转换内容
	 * @return
	 */
	public ResultBO<?> verifySingleUploadContent(String content, String playCode, int playLength,
			Map<String, String> shiftMap, Integer lotteryCode) {
		// 1.基本验证
		if (!checkFormatSingleUpload(content)) {
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		// 判断内容有无空格
		if (ObjectUtil.isBlank(content))
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_IS_NULL_FIELD);
		if (content.indexOf(SymbolConstants.SPACE) != -1)
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		
		//投注内容同时包含“#”和“*”, 则报错
		if(content.contains(SymbolConstants.NUMBER_SIGN) && content.contains(SymbolConstants.STAR)){
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		// 2.解析投注内容
		ResultBO<?> result = null;
		String[] bettings = null;// 解析投注内容
		String passway = null;// 过关方式
		String multiple = null;// 倍数
		if (content.contains(SymbolConstants.VERTICAL_BAR)) {
			String[] betContent = content.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
			if (betContent[0].contains(SymbolConstants.COMMA)) {// 1001→3,2002→1,#,4004→3|3_1,100
				bettings = betContent[0].split(SymbolConstants.COMMA);
			} else if (betContent[0].contains(SymbolConstants.OBLIQUE_LINE)) {// 1001:[31]/2002:[12]/3003:[10]|3_1,100
				bettings = betContent[0].split(SymbolConstants.OBLIQUE_LINE);
			} else {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			try {
				passway = betContent[1].split(SymbolConstants.COMMA)[0];
				multiple = betContent[1].split(SymbolConstants.COMMA)[1];
			} catch (Exception e) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
		} else {// 无过关方式和倍数
			if (content.contains(SymbolConstants.COMMA)) {// 1001→3,2002→1,#,4004→3
				bettings = content.split(SymbolConstants.COMMA);
			} else if (content.contains(SymbolConstants.OBLIQUE_LINE)) {// 1001:[31]/2002:[12]/3003:[10]
				bettings = content.split(SymbolConstants.OBLIQUE_LINE);
			} else {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
			passway = bettings.length + SymbolConstants.UNDERLINE + Constants.NUM_1;
			multiple = Constants.NUM_1 + "";
		}
		
		result = verifyMultiple(multiple);
		if (result.isError()) {
			return result;
		}

		// 4.场次边界值
		if (bettings.length > Constants.NUM_15) {
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}

		Integer betLength = bettings.length;
		Set<String> matchSet = new HashSet<String>();
		List<SingleUploadJCBetDetail> betDetails = new ArrayList<SingleUploadJCBetDetail>();
		// 5.循环验证投注内容
		for (String b : bettings) {
			SingleUploadJCBetDetail betDetail = new SingleUploadJCBetDetail();
			betDetail.setUserResult(b);
			if (!ObjectUtil.isBlank(shiftMap)) {
				if (shiftMap.containsKey(b)) { // 符号
					b = shiftMap.get(b);
				}
			}
			// 不投的场次不用验证 #、*
			if (b.contains(SymbolConstants.STAR) || b.contains(SymbolConstants.NUMBER_SIGN)) {
				if (playLength == Constants.NUM_1) {
					if (SymbolConstants.STAR.equals(b) || SymbolConstants.NUMBER_SIGN.equals(b)) {
						betDetail.setLotteryResult(b);
						betDetails.add(betDetail);
						betLength--;
						continue;
					} else {
						return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
					}
				} else if (playLength == Constants.NUM_2) {
					if (SymbolConstants.DOUBLE_STAR.equals(b) || SymbolConstants.DOUBLE_NUMBER_SIGN.equals(b)) {
						betDetail.setLotteryResult(b);
						betDetails.add(betDetail);
						betLength--;
						continue;
					} else {
						return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
					}
				}
			}

			// 1) 验证场次编号及彩果基本格式 1001:[3]
			result = verifySerialNoFormat(b.trim(), playCode, shiftMap, lotteryCode);
			if (result.isError()) {
				return result;
			}
			// 2) 验证彩果玩法内容是否匹配
			result = verifyLotteryPlayContent(b.trim(), playCode, shiftMap, lotteryCode);
			if (result.isError()) {
				return result;
			}
			String lotteryResults = (String) result.getData();
			String[] lr = lotteryResults.split(SymbolConstants.COMMA);

			// 赛事编号 1001
			String matchNo = getMatchNo(playCode, b.trim(), shiftMap, lotteryCode);
			matchSet.add(matchNo);
			JczqDaoBO jczqData = null;
			BjDaoBO bjData = null;
			Date saleEndTime = null;
			boolean flag = false;//非北单、胜负过关
			if(lotteryCode == Lottery.BJDC.getName() || lotteryCode == Lottery.SFGG.getName()){
				flag = true;
				bjData = jcDataService.findBjSingleDataByBjNum(matchNo, String.valueOf(lotteryCode));
				// 3) 验证场次是否存在
				if (ObjectUtil.isBlank(bjData)) {
					return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
				}
				saleEndTime = bjData.getSaleEndDate();
			}else{
				jczqData = jcDataService.findSingleUpMatchDataByOfficialCode(matchNo);
				// 3) 验证场次是否存在
				if (ObjectUtil.isBlank(jczqData)) {
					return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
				}
				saleEndTime = jczqData.getSaleEndDate();
			}
			// 4) 验证该场次销售时间是否截止
			if (DateUtil.compare(saleEndTime, DateUtil.getNowDate()) != 1) {
				return ResultBO.err(MessageCodeConstants.MATCH_DOES_NOT_EXIST_OR_STOP_SELLING);
			}
			betDetail.setSessionNumber(matchNo);
			betDetail.setLotteryResult(lr[1]);
			betDetail.setUserResult(lr[0]);
			// 混投需传 区分 胜平负[S]/让胜平负[R]
			if (playCode.equals(SportEnum.SportFbSubWay.JCZQ_M.getValue())) {
				if (b.trim().contains("R")) {
					betDetail.setPlay("R");
				} else {
					betDetail.setPlay("S");
				}
			}
			betDetail.setSaleEndTime(saleEndTime);
			betDetail.setIssueCode(flag ? bjData.getIssueCode() : jczqData.getIssueCode());
			betDetails.add(betDetail);
		}
		// 有重复赛事
		if (matchSet.size() != betLength) {
			return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
		}
		
		if (!content.contains(SymbolConstants.VERTICAL_BAR)) {
			passway = betLength + SymbolConstants.UNDERLINE + Constants.NUM_1;
		} else {//否则验证过关方式：不允许比有效场次大
			if (Integer.valueOf(passway.substring(0, 1)) > Integer.valueOf(betLength.toString())) {
				return ResultBO.err(MessageCodeConstants.BET_CONTENT_ILLEGAL_SERVICE);
			}
        }
		if(passway.equals("1_1")){
			return ResultBO.err(MessageCodeConstants.PASSWAY_PERMIT_SERVICE);
		}
		// 3.验证过关方式及倍数合法性
		result = verifyPasswayRule(playCode, passway, lotteryCode);
		if (result.isError()) {
			return result;
		}

        SingleUploadSuccessResultBO successResultBO = new SingleUploadSuccessResultBO(content, passway,
				Integer.parseInt(multiple), betLength);
		successResultBO.setBetDetails(betDetails);
		return ResultBO.ok(successResultBO);
	}

	public static void main(String[] args) throws Exception {
//		 String content = "4004→3,5005→3,6006→1,7001→3|4_1,100";
//		 SingleUploadOrderValidateMethod method = new
//		 SingleUploadOrderValidateMethod();
//		 ResultBO<?> result = method.verifySingleUploadContent(content, "M", 1,
//		 null);
//		 System.out.println(result.getErrorCode());
//		 for (String str : getListMatchFromParam("星期一001/星期一002/星期一003")) {
//		 System.out.println(str);
//		 }
//		 System.out.println("#".length());
//		System.out.println(verifyPassway("2_1", 3));
		System.out.println("2_1".substring(0, 1));
	}
}