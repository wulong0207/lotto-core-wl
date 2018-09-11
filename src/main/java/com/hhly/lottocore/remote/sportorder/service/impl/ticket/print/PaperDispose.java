package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.football.EightFootballPaper;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.football.SixFootBallPaper;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.football.ThreeFootballPaper;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.number.DTDltPaper;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.number.DltPaper;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;

/**
 * @desc 彩种处理
 * @author jiangwei
 * @date 2018年7月17日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service
public class PaperDispose {
	@Autowired
	SixFootBallPaper sixFootBallPaper;
	@Autowired
	ThreeFootballPaper threeFootballPaper;
	@Autowired
	EightFootballPaper eightFootballPaper;
	@Autowired
	DltPaper dltPaper;
	@Autowired
	DTDltPaper dtDltPaper;
	
    /**
     * 解析投资类容为二维坐标字符串
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年7月17日 下午3:46:57
     * @param ticket
     * @return
     */
	public String coordinate(Ticket ticket) {
		IPaper lottery = getLottery(ticket);
		StringBuilder sb = new StringBuilder();
		if(lottery.check(ticket)){
			sb.append(lottery.header(ticket));
			sb.append(lottery.play(ticket));
			sb.append(lottery.content(ticket));
			sb.append(lottery.multiple(ticket));
		}
		return sb.toString();
	}
	
    /**
     * 获取彩票纸解析
     * @author jiangwei
     * @Version 1.0
     * @CreatDate 2018年7月17日 下午3:47:22
     * @param lotteryCode
     * @param childType
     * @return
     */
	private IPaper getLottery(Ticket ticket) {
		String childType = ticket.getChildType();
		switch (ticket.getLotteryCode()) {
		case 300:
			if ("1_1".equals(childType) || "2_1".equals(childType) || "3_1".equals(childType)) {
				return threeFootballPaper;
			} else if ("4_1".equals(childType) || "5_1".equals(childType) || "6_1".equals(childType)) {
				return sixFootBallPaper;
			} else {
				return eightFootballPaper;
			}
		case 102:
			if("3".equals(ticket.getContentType())){
				return dtDltPaper;
			}else{
				return dltPaper;
			}
		default:
			break;
		}
		throw new ServiceRuntimeException("不存在彩种解析");
	}
}
