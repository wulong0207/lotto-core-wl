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
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.SportUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.BetContentBO;
import com.hhly.skeleton.lotto.base.ticket.bo.MatchsBO;
import com.hhly.skeleton.lotto.base.ticket.bo.SportTicketDetailInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.TicketOrderInfoBO;
/**
 * 北京单场出票明细处理
 * @author longguoyou
 * @date 2017年11月2日
 * @compay 益彩网络科技有限公司
 */
@Component
public class BjdcHandleTicketDetails extends AbstractTicket implements HandleTicketDetails {
	
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
			sportTicketDetailInfoBO.setBetNum(ticketDetailHandler.getSportsManyNote(ticketInfoBO.getTicketContent(), orderBaseInfoBO.getLotteryCode()));
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
				//系统编号： 1)1711024024[-1](1@3.35,3@2.23) 2)1711024024_S(3@1.33) 3)1711024023(3@1.17)
				
				String systemCode = getSystemCode(content);
				BjDaoBO bjDaoBO = jcDataService.findBjDataBOBySystemCode(systemCode, String.valueOf(orderBaseInfoBO.getLotteryCode()));
				MatchsBO matchsBO = new MatchsBO();
				if(!ObjectUtil.isBlank(bjDaoBO)){
					matchsBO.setFullScore(bjDaoBO.getFullScore());//全场比分
					matchsBO.setHalfScore(bjDaoBO.getHalfScore());//半场比分
					matchsBO.setHostName(ObjectUtil.isBlank(bjDaoBO.getHomeShortName())?bjDaoBO.getHomeFullName():bjDaoBO.getHomeShortName());//主队名称
					matchsBO.setVisitName(ObjectUtil.isBlank(bjDaoBO.getGuestShortName())?bjDaoBO.getGuestFullName():bjDaoBO.getGuestShortName());//客队名称
					matchsBO.setNum(bjDaoBO.getBjNum());//官方赛事编号
					matchsBO.setCaiguo(BJDCConstants.translate(ticketInfoBO.getLotteryChildCode(), getCaiguoByLotteryChildCode(ticketInfoBO.getLotteryChildCode(),bjDaoBO), content));
					matchsBO.setChildCode(String.valueOf(ticketInfoBO.getLotteryChildCode()));//拆票后，子玩法编号
					matchsBO.setChildName(ticketInfoBO.getLotteryChildName());//拆票后 ，子玩法名称
					matchsBO.setMatchStatus(bjDaoBO.getMatchStatus());//比赛状态
				}
				
				List<BetContentBO> listBetContent = new ArrayList<BetContentBO>();
				String betSps = FormatConversionJCUtil.singleGameBetContentSubstring(content);
				for(String sps : betSps.split(SymbolConstants.COMMA)){
					BetContentBO betContentBO = new BetContentBO();
					String[] betSp = FormatConversionJCUtil.singleOptionBetContentAnalysis(sps);
					//投注项
					String bet = betSp[0];
					//赔率
//					String sp = betSp[1];
//					betContentBO.setSp(Float.valueOf(sp));// 不展示赔率
					betContentBO.setPlanContent(BJDCConstants.translate(ticketInfoBO.getLotteryChildCode(),bet,content));//投注内容
					betContentBO.setFlag(judeFlag(bet,getCaiguoByLotteryChildCode(ticketInfoBO.getLotteryChildCode(),bjDaoBO)));//是否标红 
					betContentBO.setInfo(getInfoFromSingleBetContent(content,ticketInfoBO.getLotteryChildCode()));//让分：如[+1]
					listBetContent.add(betContentBO);
				}
				
			    matchsBO.setMatchStatus(SportUtil.getMatchStatus(bjDaoBO.getMatchStatus()));
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
		if(BJDCConstants.ID_RQS == lotteryChildCode || BJDCConstants.ID_SFC == lotteryChildCode){
			return content.substring(content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT), content.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT)+1);
		}
		return SymbolConstants.ENPTY_STRING;
	}

	/**
	 * 北京单场通过子玩法获取赛果
	 * @author longguoyou
	 * @date 2017年11月3日
	 * @param lotteryChildCode
	 * @return
	 */
	private String getCaiguoByLotteryChildCode(Integer lotteryChildCode, BjDaoBO bjDaoBO) {
		switch(lotteryChildCode){
		case BJDCConstants.ID_FBCQ:
			return bjDaoBO.getHfWdf();
		case BJDCConstants.ID_FBF:
			return bjDaoBO.getScore();
		case BJDCConstants.ID_FZJQ:
			return bjDaoBO.getGoalNum();
		case BJDCConstants.ID_SXDX:
			return bjDaoBO.getUdSd();
		case BJDCConstants.ID_RQS:
			return bjDaoBO.getLetWdf();
		case BJDCConstants.ID_SFC:
			return bjDaoBO.getLetSf();
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println("竞彩篮球胜负".length());
		System.out.println("竞彩篮球胜负".substring(4));
	}
}
