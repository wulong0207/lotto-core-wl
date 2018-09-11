package com.hhly.lottocore.remote.sportorder.service.impl.ticket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.hhly.skeleton.base.common.LotteryEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.AbstractTicket;
import com.hhly.lottocore.remote.sportorder.service.HandleTicketDetails;
import com.hhly.skeleton.base.constants.JCZQConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.NumberFormatUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.SportUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JczqOrderBO;
import com.hhly.skeleton.lotto.base.ticket.bo.BetContentBO;
import com.hhly.skeleton.lotto.base.ticket.bo.MatchsBO;
import com.hhly.skeleton.lotto.base.ticket.bo.SportTicketDetailInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketOrderInfoBO;
/**
 * 竞彩足球出票明细处理
 * @author longguoyou
 * @date 2017年11月2日
 * @compay 益彩网络科技有限公司
 */
@Component
public class JczqHandleTicketDetails extends AbstractTicket implements HandleTicketDetails {
	
	@Resource(name = "jcDataService")
	private IJcDataService jcDataService;
	
	@Autowired
    private TicketDetailHandler ticketDetailHandler;
    
	@Override
	public TicketOrderInfoBO handle(OrderBaseInfoBO orderBaseInfoBO, List<TicketInfoBO> listTicketInfoBO,Map<String,Object> map) {
		
		/**返回BO*/
		TicketOrderInfoBO ticketOrderInfoBO = new TicketOrderInfoBO();
		
		/**票信息集：对应数据库每行票信息*/
		List<SportTicketDetailInfoBO> sportList = new ArrayList<SportTicketDetailInfoBO>();
		
		//票信息
		for(TicketInfoBO ticketInfoBO : listTicketInfoBO){

			if(!ObjectUtil.isBlank(ticketInfoBO.getReceiptContentDetail())){//使用出票返回的赔率，盘口
				ticketInfoBO.setTicketContent(ticketInfoBO.getReceiptContentDetail());
			}

			//票投注内容
			String[] betContents = FormatConversionJCUtil.singleBetContentAnalysis(ticketInfoBO.getTicketContent());

			/**结果集*/
			SportTicketDetailInfoBO sportTicketDetailInfoBO = new SportTicketDetailInfoBO();
			sportTicketDetailInfoBO.setMoney(ticketInfoBO.getTicketMoney());
//			if(!ticketInfoBO.getLotteryCode().equals(LotteryEnum.Lottery.FB.getName())){
				sportTicketDetailInfoBO.setBetNum(ticketDetailHandler.getSportsManyNote(ticketInfoBO.getTicketContent(), orderBaseInfoBO.getLotteryChildCode()));
//			}
			sportTicketDetailInfoBO.setMultiple(ticketInfoBO.getMultipleNum());
			sportTicketDetailInfoBO.setPreBonus(ObjectUtil.isBlank(ticketInfoBO.getPreBonus())?null:ticketInfoBO.getPreBonus());
			sportTicketDetailInfoBO.setTicketStatus(ticketInfoBO.getTicketStatus());
			sportTicketDetailInfoBO.setWinningStatus(ticketInfoBO.getWinningStatus());
			sportTicketDetailInfoBO.setPassway(betContents[1].replace(SymbolConstants.UNDERLINE, "串"));//需解析投注内容获取
			
			//投注内容解析 1711024024[-1](1@3.35)|1711024024_S(3@1.33)|1711024023(3@1.17)
			String[] contents = betContents[0].split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
		    
			//解析出对阵信息
			List<MatchsBO> listMatchs = new ArrayList<MatchsBO>();
			
			//通过赛事编号查询对阵信息
			for(String content : contents){
				//系统编号： 1)1711024024[-1](1@3.35) 2)1711024024_S(3@1.33) 3)1711024023(3@1.17) 4)1711024024_R[+10](3@1.33)
				String systemCode = getSystemCode(content);
				Map<String, JczqOrderBO> mapJczqDataBO = jcDataService.findJczqOrderBOBySystemCodes(Arrays.asList(systemCode));
				if(!ObjectUtil.isBlank(mapJczqDataBO)){
					JczqOrderBO jczqOrderBO = mapJczqDataBO.get(systemCode);
					MatchsBO matchsBO = new MatchsBO();
					if(!ObjectUtil.isBlank(jczqOrderBO)){
						matchsBO.setFullScore(jczqOrderBO.getFullScore());//全场比分
						matchsBO.setHalfScore(jczqOrderBO.getHalfScore());//半场比分
						matchsBO.setHostName(ObjectUtil.isBlank(jczqOrderBO.getHomeShortName())?jczqOrderBO.getHomeFullName():jczqOrderBO.getHomeShortName());//主队名称
						matchsBO.setVisitName(ObjectUtil.isBlank(jczqOrderBO.getGuestShortName())?jczqOrderBO.getGuestFullName():jczqOrderBO.getGuestShortName());//客队名称
						matchsBO.setNum(jczqOrderBO.getOfficialMatchCode());//官方赛事编号
						matchsBO.setCaiguo(JCZQConstants.translate(ticketInfoBO.getLotteryChildCode(), getCaiguoByLotteryChildCode(ticketInfoBO.getLotteryChildCode(),jczqOrderBO,content), content));//彩果
						matchsBO.setChildCode(String.valueOf(ticketInfoBO.getLotteryChildCode()));//拆票后，子玩法编号
						matchsBO.setChildName(ticketInfoBO.getLotteryChildName());//拆票后 ，子玩法名称
					}
					
					List<BetContentBO> listBetContent = new ArrayList<BetContentBO>();
					String betSps = FormatConversionJCUtil.singleGameBetContentSubstring(content);
					for(String sps : betSps.split(SymbolConstants.COMMA)){
						String[] betSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(sps);
						//投注项
						String bet = betSp[0];
						//赔率
						String sp = betSp[1];
						BetContentBO betContentBO = new BetContentBO();
						betContentBO.setSp(NumberFormatUtil.format(Float.valueOf(sp)));//赔率
						betContentBO.setPlanContent(JCZQConstants.translate(ticketInfoBO.getLotteryChildCode(), bet, content));//投注内容
						betContentBO.setFlag(judeFlag(bet,getCaiguoByLotteryChildCode(ticketInfoBO.getLotteryChildCode(),jczqOrderBO,content)));//是否标红 
						betContentBO.setInfo(getInfoFromSingleBetContent(content,ticketInfoBO.getLotteryChildCode()));//让分：如[+1]
						betContentBO.setLotteryChildCode(JCZQConstants.translateChildCoe(ticketInfoBO.getLotteryChildCode(), bet, content));//投注内容具体子玩法
						listBetContent.add(betContentBO);
					}
					
					matchsBO.setMatchStatus(SportUtil.getMatchStatus(jczqOrderBO.getMatchStatus()));
					matchsBO.setListBetContent(listBetContent);
					matchsBO.setInfo(getInfoFromSingleBetContent(content,ticketInfoBO.getLotteryChildCode()));
					
					listMatchs.add(matchsBO);
				}
			}
			//设置对阵信息
			sportTicketDetailInfoBO.setListMatchs(listMatchs);
			//添加到票信息集
			sportList.add(sportTicketDetailInfoBO);

		}
		//设置竞技彩票结果集
		ticketOrderInfoBO.setSportList(sportList);
		return ticketOrderInfoBO;
	}





	/**
	 * 获取附加信息：让分[+10]<br>
	 * 1)1711024024[-1](1@3.35)<br>
	 * 2)1711024024_R[+10](3@1.33)<br>
	 * @author longguoyou
	 * @date 2017年11月4日
	 * @param content
	 * @param lotteryChildCode
	 * @return 如：[+10]
	 */
	private String getInfoFromSingleBetContent(String content, Integer lotteryChildCode) {
		//让胜平负玩法 :1711024024[-1](1@3.35)
		if(JCZQConstants.ID_RQS == lotteryChildCode){
			return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
		}
		//混投：1711024024_R[+10](3@1.33)
		if(JCZQConstants.ID_FHT == lotteryChildCode && content.contains("R")){
			return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
		}
		return SymbolConstants.ENPTY_STRING;
	}
	
	public static void main(String[] args) {
		String content = "1711024024[-1](1@3.35)";
		System.out.println(content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1));
	}

	/**
	 * 竞彩足球通过子玩法获取赛果
	 * @author longguoyou
	 * @date 2017年11月3日
	 * @param lotteryChildCode
	 * @return
	 */
	private String getCaiguoByLotteryChildCode(Integer lotteryChildCode, JczqOrderBO jczqOrderBO, String initContent) {
		switch(lotteryChildCode){
		case JCZQConstants.ID_FBCQ:
			return jczqOrderBO.getHfWdf();
		case JCZQConstants.ID_FBF:
			return jczqOrderBO.getScore();
		case JCZQConstants.ID_FZJQ:
			return jczqOrderBO.getGoalNum();
		case JCZQConstants.ID_JCZQ:
			return jczqOrderBO.getFullSpf();
		case JCZQConstants.ID_RQS:
			return jczqOrderBO.getLetSpf();
		case JCZQConstants.ID_FHT:
			return getCaiguoByLotteryChildCodeMix(lotteryChildCode,jczqOrderBO,initContent);
		}
		return null;
	}
	
	private String getCaiguoByLotteryChildCodeMix(Integer lotteryChildCode, JczqOrderBO jczqOrderBO, String initContent) {
		if(initContent.contains("R")){
			return getCaiguoByLotteryChildCode(JCZQConstants.ID_RQS,jczqOrderBO,null);
		}
		if(initContent.contains("S")){
			return getCaiguoByLotteryChildCode(JCZQConstants.ID_JCZQ,jczqOrderBO,null);
		}
		if(initContent.contains("Q")){
			return getCaiguoByLotteryChildCode(JCZQConstants.ID_FBF,jczqOrderBO,null);
		}
		if(initContent.contains("B")){
			return getCaiguoByLotteryChildCode(JCZQConstants.ID_FBCQ,jczqOrderBO,null);
		}
		if(initContent.contains("Z")){
			return getCaiguoByLotteryChildCode(JCZQConstants.ID_FZJQ,jczqOrderBO,null);
		}
		return null;
	}

	/**
	 * 翻译投注项 移到 lotto工程
	 * @author longguoyou
	 * @date 2017年11月3日
	 * @param lotteryCode
	 * @param bet 
	 * @return
	 */
	public static String process(Integer lotteryCode,  String bet){
		/** 根据子玩法， 翻译*/
		switch(lotteryCode){
		    
		}
		return null;
	}
}
