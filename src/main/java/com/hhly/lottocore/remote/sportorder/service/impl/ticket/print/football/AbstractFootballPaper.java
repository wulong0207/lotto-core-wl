	package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.football;

import java.util.Map;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.IPaper;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.ISportSymbol;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.SportSymbolHandler;
import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;

/**
 * @desc 竞彩足球
 * @author jiangwei
 * @date 2018年7月17日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class AbstractFootballPaper implements IPaper {

	private static ISportSymbol SYMBOL = new SportSymbol();

	private static int[] MATCH_NUM = { 5, 4, 2, 1 };

	private static int[] MULTIPLE_NUM = { 5, 4, 3, 2, 1 };

	 
	@Override
	public boolean check(Ticket ticket) {
		return true;
	}

	@Override
	public String play(Ticket ticket) {
		String key = "play_" + ticket.getChildType();
		if (ticket.getChildCode() == 30001) {
			key = key + "_hh";
		}
		return "," + getCoordinate(key);
	}

	@Override
	public String content(Ticket ticket) {
		StringBuilder sb = new StringBuilder();
		String content = SportSymbolHandler.doJcContent(ticket.getContent(), ticket.getChildCode(), SYMBOL);
		int number = 1;
		for (String match : content.split("\\|")) {
			String[] s = match.split("_");
			sb.append(",");
			sb.append(getCoordinate(getWeek(s[0], number)));
			sb.append(getMatch(getCoordinateMap(), number, Integer.parseInt(s[1])));
			sb.append(getPlayChoose(ticket.getChildCode(), getCoordinateMap(), number, s));
			number++;
		}
		String bunch = getCoordinate("bunch_" + ticket.getChildType());
		if (bunch != null) {
			sb.append(",");
			sb.append(bunch);
		}
		return sb.toString();
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
	 * 获取坐标
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月17日 下午3:17:57
	 * @param key
	 * @return
	 */
	protected  String getCoordinate(String key){
		return getCoordinateMap().get(key);
	}

	/**
	 * 获取星期
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月17日 下午3:21:36
	 * @param week
	 * @param num
	 * @return
	 */
	private String getWeek(String week, int num) {
		return "week_" + week + "_" + num;
	}

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
		return getNumber(coordinate, 0, multiple, MULTIPLE_NUM, "multiple").toString();
	}

	/**
	 * 获取比赛场次
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月17日 下午3:23:20
	 * @param coordinate
	 * @param no
	 * @param matchNo
	 * @return
	 */
	private String getMatch(Map<String, String> coordinate, int no, int matchNo) {
		return getNumber(coordinate, no, matchNo, MATCH_NUM, "number").toString();
	}

	/**
	 * 玩法
	 * 
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2018年7月14日 下午5:09:12
	 * @param childCode
	 * @param coordinate
	 * @param number
	 * @param s
	 * @return
	 */
	private StringBuilder getPlayChoose(int childCode, Map<String, String> coordinate, int number, String[] s) {
		StringBuilder sb = new StringBuilder();
		String palyCode = "";
		for (int i = 2; i < s.length; i++) {
			String c = s[i];
			if (i == 2) {
				String[] code = s[i].split("\\*");
				if (code.length == 2) {
					palyCode = code[0];
					c = code[1];
				} else {
					palyCode = getPlayCode(childCode);
				}
			}
			sb.append(",");
			String key = palyCode + "_" + c + "_" + number;
			sb.append(coordinate.get(key));
		}
		return sb;
	}

	private String getPlayCode(int childCode) {
		switch (childCode) {
		// 足球
		case 30002:// 胜平负
			return "S";
		case 30003:// 让胜平负
			return "R";
		case 30004:// 比分
			return "Q";
		case 30005:// 总进球
			return "Z";
		case 30006:// 半全场
			return "B";
		default:
			break;
		}
		throw new RuntimeException("子玩法错误");
	}

	/**
	 * 竞彩格式足球解析
	 * 
	 * @desc
	 * @author jiangwei
	 * @date 2018年7月17日
	 * @company 益彩网络科技公司
	 * @version 1.0
	 */
	private static class SportSymbol implements ISportSymbol {

		@Override
		public StringBuffer leftBracket(int contentLength, String match, char play) {
			StringBuffer sb = new StringBuffer();
			if (contentLength > 0) {
				sb.append("|");
			}
			sb.append(match.substring(6, 7));
			sb.append("_");
			sb.append(match.substring(7));
			sb.append("_");
			switch (play) {
			case 'S':
				sb.append("S*");
				break;
			case 'R':
				sb.append("R*");
				break;
			case 'Q':
				sb.append("Q*");
				break;
			case 'Z':
				sb.append("Z*");
				break;
			case 'B':
				sb.append("B*");
				break;
			default:
				break;
			}
			return sb;
		}

		@Override
		public StringBuffer comma(String temp, char play) {
			StringBuffer sb = new StringBuffer(temp.split("_")[1]);
			sb.append("_");
			return sb;
		}

		@Override
		public StringBuffer rightBracket(String temp, char play) {
			StringBuffer sb = comma(temp, play);
			sb.setLength(sb.length() - 1);
			return sb;
		}
	}
}
