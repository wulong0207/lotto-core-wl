package com.hhly.lottocore.remote.sportorder.service.impl.ticket;

import com.hhly.lottocore.persistence.sport.dao.SportAgainstInfoDaoMapper;
import com.hhly.lottocore.remote.lotto.service.IJcDataService;
import com.hhly.lottocore.remote.sportorder.service.AbstractTicket;
import com.hhly.lottocore.remote.sportorder.service.HandleTicketDetails;
import com.hhly.skeleton.base.constants.BJDCConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.FormatConversionJCUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.SportUtil;
import com.hhly.skeleton.lotto.base.order.bo.OrderBaseInfoBO;
import com.hhly.skeleton.lotto.base.order.bo.OrderMatchInfoBO;
import com.hhly.skeleton.lotto.base.sport.bo.BjDaoBO;
import com.hhly.skeleton.lotto.base.sport.bo.SportAgainstInfoBO;
import com.hhly.skeleton.lotto.base.ticket.bo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 北京单场出票明细处理
 * @author longguoyou
 * @date 2017年11月2日
 * @compay 益彩网络科技有限公司
 */
@Component("gYJHandleTicketDetails")
public class GYJHandleTicketDetails extends AbstractTicket implements HandleTicketDetails {
	
	@Resource(name = "jcDataService")
	private IJcDataService jcDataService;
	
	@Autowired
    private TicketDetailHandler ticketDetailHandler;

	@Autowired
	private SportAgainstInfoDaoMapper sportAgainstInfoDaoMapper;
    
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
			/**结果集*/
			SportTicketDetailInfoBO sportTicketDetailInfoBO = new SportTicketDetailInfoBO();
			sportTicketDetailInfoBO.setMoney(ticketInfoBO.getTicketMoney());
			sportTicketDetailInfoBO.setBetNum(ticketDetailHandler.getSportsManyNote(ticketInfoBO.getTicketContent(), orderBaseInfoBO.getLotteryCode()));
			sportTicketDetailInfoBO.setMultiple(ticketInfoBO.getMultipleNum());
			sportTicketDetailInfoBO.setPreBonus(ObjectUtil.isBlank(ticketInfoBO.getPreBonus())?null:ticketInfoBO.getPreBonus());
			sportTicketDetailInfoBO.setTicketStatus(ticketInfoBO.getTicketStatus());
			sportTicketDetailInfoBO.setWinningStatus(ticketInfoBO.getWinningStatus());
			sportTicketDetailInfoBO.setPassway("单关");//需解析投注内容获取

			String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(ticketInfoBO.getTicketContent());//解析投注详情
			//投注内容的场次编号
			String[] betContentArr = FormatConversionJCUtil.stringSplitArray(contents[0], SymbolConstants.VERTICAL_BAR, true);
			List<OrderMatchInfoBO> orderMatchInfoBOs = new ArrayList<OrderMatchInfoBO>();
			for(String content : betContentArr){
				String systemCode = content.split(SymbolConstants.AT)[0];
				List<SportAgainstInfoBO> sportAgainstInfoBOs = sportAgainstInfoDaoMapper.findSportAgainstInfoBySystemCode(systemCode,orderBaseInfoBO.getLotteryCode());
				if(!ObjectUtil.isBlank(sportAgainstInfoBOs)) {
					for (SportAgainstInfoBO sportAgainstInfoBO : sportAgainstInfoBOs) {
						OrderMatchInfoBO orderMatchInfoBO = new OrderMatchInfoBO();
						orderMatchInfoBO.setSystemCode(sportAgainstInfoBO.getSystemCode());
						orderMatchInfoBO.setHomeName(sportAgainstInfoBO.getHomeName());
						orderMatchInfoBO.setVisitiName(sportAgainstInfoBO.getVisitiName());
						orderMatchInfoBO.setMatchShortName(sportAgainstInfoBO.getMatchName());
						orderMatchInfoBO.setMatchStatus(Integer.valueOf(sportAgainstInfoBO.getMatchStatus()));
						orderMatchInfoBO.setBetGameContent(getBetGameContent(content, sportAgainstInfoBO.getSystemCode()));
						//设置彩果
						if (sportAgainstInfoBO.getMatchStatus().intValue() == Constants.NUM_18) {//状态为已淘汰时显示已淘汰
							orderMatchInfoBO.setCaiGuoStatus(Constants.NUM_1);
						} else if (sportAgainstInfoBO.getMatchStatus().intValue() == Constants.NUM_15 || sportAgainstInfoBO.getMatchStatus().intValue() == Constants.NUM_16 || sportAgainstInfoBO.getMatchStatus().intValue() == Constants.NUM_17) {//已审核，已开奖，已派奖，直接显示对应的队伍
							orderMatchInfoBO.setCaiGuoStatus(Constants.NUM_2);
						} else {
							orderMatchInfoBO.setCaiGuoStatus(Constants.NUM_3);
						}
						orderMatchInfoBOs.add(orderMatchInfoBO);
					}
				}
			}
			sportTicketDetailInfoBO.setOrderMatchInfoBOs(orderMatchInfoBOs);
			sportList.add(sportTicketDetailInfoBO);
		}
		//设置竞技彩票结果集
		ticketOrderInfoBO.setSportList(sportList);
		return ticketOrderInfoBO;
	}

	/**
	 * 获取每场对阵的显示内容
	 * @param betContent
	 * @return
	 */
	private String getBetGameContent(String betContent,String matchSystemCode){
		String[]  contents = FormatConversionJCUtil.singleBetContentAnalysis(betContent);//解析投注详情
		//投注内容的场次编号
		String[] betContentArr = FormatConversionJCUtil.stringSplitArray(contents[0], SymbolConstants.VERTICAL_BAR, true);
		for(String content : betContentArr){
			String systemCode = content.split(SymbolConstants.AT)[0];
			if(systemCode.equals(matchSystemCode)){
				return SymbolConstants.AT+content.split(SymbolConstants.AT)[1];
			}
		}
		return "";
	}
	

}
