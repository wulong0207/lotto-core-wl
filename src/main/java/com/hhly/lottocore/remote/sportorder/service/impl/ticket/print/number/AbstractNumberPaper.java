package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.number;

import java.util.Map;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.IPaper;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;

/**
 * @desc 大乐透
 * @author jiangwei
 * @date 2018年7月24日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class AbstractNumberPaper implements IPaper {

	@Override
	public boolean check(Ticket ticket) {
		return true;
	}

	@Override
	public String multiple(Ticket ticket) {
		return getMultiple(getCoordinateMap(), ticket.getMultiple());
	}

	/**
	 * 获取二维坐标map
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月17日 下午3:13:46
	 * @return
	 */
	protected abstract Map<String, String> getCoordinateMap();
	
	/**
	 * 获取倍数
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月17日 下午3:08:46
	 * @param coordinate
	 * @param matchNo
	 * @param num
	 * @param header
	 * @return
	 */
	private String getMultiple(Map<String, String> coordinate, int multiple) {
		return getNumber(coordinate, 0, multiple, getMultipleNum(), "multiple").toString();
	}
	
	/**
	 * 获取倍数解析
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月24日 下午12:23:00
	 * @return
	 */
	protected abstract int[] getMultipleNum();

}
