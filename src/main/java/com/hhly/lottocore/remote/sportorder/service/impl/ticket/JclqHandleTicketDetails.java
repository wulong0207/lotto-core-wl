package com.hhly.lottocore.remote.sportorder.service.impl.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.AbstractTicket;
import com.hhly.lottocore.remote.sportorder.service.HandleTicketDetails;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.NumberFormatUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.SportUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.sport.bo.JclqOrderBO;
import com.hhly.skeleton.lotto.base.ticket.bo.BetContentBO;
import com.hhly.skeleton.lotto.base.ticket.bo.MatchsBO;
import com.hhly.skeleton.lotto.base.ticket.bo.SportTicketDetailInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketOrderInfoBO;
/**
 * 竞彩篮球出票明细处理
 * @author longguoyou
 * @date 2017年11月2日
 * @compay 益彩网络科技有限公司
 */
@Component
public class JclqHandleTicketDetails extends AbstractTicket implements HandleTicketDetails {
	
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
			sportTicketDetailInfoBO.setBetNum(ticketDetailHandler.getSportsManyNote(ticketInfoBO.getTicketContent(), orderBaseInfoBO.getLotteryChildCode()));
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
				//系统编号： 1)1711024024[-1](1@3.35) 2)1711024024_S(3@1.33) 3)1711024023(3@1.17)
				String systemCode = getSystemCode(content);
				JclqOrderBO jclqOrderBO = jcDataService.findJclqOrderBOBySystemCode(systemCode);
				MatchsBO matchsBO = new MatchsBO();
				if(!ObjectUtil.isBlank(jclqOrderBO)){
					matchsBO.setFullScore(jclqOrderBO.getFullScore());//全场比分
					matchsBO.setHostName(ObjectUtil.isBlank(jclqOrderBO.getHomeShortName())?jclqOrderBO.getHomeFullName():jclqOrderBO.getHomeShortName());//主队名称
					matchsBO.setVisitName(ObjectUtil.isBlank(jclqOrderBO.getGuestShortName())?jclqOrderBO.getGuestFullName():jclqOrderBO.getGuestShortName());//客队名称
					matchsBO.setNum(jclqOrderBO.getOfficialMatchCode());//官方赛事编号
					//盘口
					String pankou = getInfoFromSingleBetContent(content,ticketInfoBO.getLotteryChildCode());
					matchsBO.setCaiguo(JCLQConstants.translate(ticketInfoBO.getLotteryChildCode(), getCaiguoByLotteryChildCode(ticketInfoBO.getLotteryChildCode(),jclqOrderBO,content), content,pankou));
					matchsBO.setChildCode(String.valueOf(ticketInfoBO.getLotteryChildCode()));//拆票后，子玩法编号
					matchsBO.setChildName(ticketInfoBO.getLotteryChildName());//拆票后 ，子玩法名称
					matchsBO.setMatchStatus(jclqOrderBO.getMatchStatus());
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
					String info = getInfoFromSingleBetContent(content,ticketInfoBO.getLotteryChildCode());
					betContentBO.setPlanContent(JCLQConstants.translate(ticketInfoBO.getLotteryChildCode(), bet, content, info));//投注内容
					betContentBO.setFlag(judeFlagLq(ticketInfoBO.getLotteryChildCode(),bet,getCaiguoByLotteryChildCode(ticketInfoBO.getLotteryChildCode(),jclqOrderBO,content),info,content));//是否标红
					betContentBO.setInfo(info);//大小分：如[210.5]
					listBetContent.add(betContentBO);
				}
				
				matchsBO.setMatchStatus(SportUtil.getMatchStatus(jclqOrderBO.getMatchStatus()));
				matchsBO.setListBetContent(listBetContent);
				matchsBO.setInfo(getInfoFromSingleBetContent(content,ticketInfoBO.getLotteryChildCode()));
				
				listMatchs.add(matchsBO);
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
	 * 获取附加信息：让分[+10]/大小分D[210.5]<br>
	 * 1)1711024024[-10](1@3.35)<br>
	 * 2)1711024024_D[210.5](99@1.33)<br>
	 * @author longguoyou
	 * @date 2017年11月4日
	 * @param content
	 * @param lotteryChildCode
	 * @return 如：[+10]
	 */
	private String getInfoFromSingleBetContent(String content, Integer lotteryChildCode) {
		//让胜平负玩法 :1711024024[-10](1@3.35)/ 大小分1711024024[210.5](99@1.33)
		if(JCLQConstants.ID_JCLQ_RF == lotteryChildCode || JCLQConstants.ID_JCLQ_DXF == lotteryChildCode){
			return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
		}
		//混投：1711024024_D[210.5](99@1.33)/1711024024_R[+10.5](3@1.33)
		if(JCLQConstants.ID_JCLQ_HHGG == lotteryChildCode){
			if(content.contains("R") || content.contains("D")){
				return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
			}
		}
		return SymbolConstants.ENPTY_STRING;
	}

	/**
	 * 竞彩篮球通过子玩法获取赛果
	 * @author longguoyou
	 * @date 2017年11月3日
	 * @param lotteryChildCode
	 * @return
	 */
	private String getCaiguoByLotteryChildCode(Integer lotteryChildCode, JclqOrderBO jclqOrderBO, String initContent) {
		switch(lotteryChildCode){
		case JCLQConstants.ID_JCLQ_DXF:
			return jclqOrderBO.getSizeScore();//157.5|99,157.5|99
		case JCLQConstants.ID_JCLQ_RF:
			return jclqOrderBO.getLetWf();//-4.5|0,-5.5|0
		case JCLQConstants.ID_JCLQ_SF:
			return jclqOrderBO.getFullWf();
		case JCLQConstants.ID_JCLQ_SFC:
			return jclqOrderBO.getWinScore();
		case JCLQConstants.ID_JCLQ_HHGG:
		    return getCaiguoByLotteryChildCodeMix(lotteryChildCode,jclqOrderBO,initContent);	
		}
		return null;
	}
	/**
	 * 混投过关
	 * @author longguoyou
	 * @date 2017年11月7日
	 * @param lotteryChildCode
	 * @param jclqOrderBO
	 * @param initContent
	 * @return
	 */
	private String getCaiguoByLotteryChildCodeMix(Integer lotteryChildCode, JclqOrderBO jclqOrderBO, String initContent){
		if(initContent.contains("R")){
			return getCaiguoByLotteryChildCode(JCLQConstants.ID_JCLQ_RF,jclqOrderBO,null);
		}
		if(initContent.contains("D")){
			return getCaiguoByLotteryChildCode(JCLQConstants.ID_JCLQ_DXF,jclqOrderBO,null);
		}
		if(initContent.contains("S")){
			return getCaiguoByLotteryChildCode(JCLQConstants.ID_JCLQ_SF,jclqOrderBO,null);
		}
		if(initContent.contains("C")){
			return getCaiguoByLotteryChildCode(JCLQConstants.ID_JCLQ_SFC,jclqOrderBO,null);
		}
		return null;
	}
}
