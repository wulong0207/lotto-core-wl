package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print;

/**
 * @desc 竞彩符号处理类
 * @author jiangwei
 * @date 2017年9月8日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public class SportSymbolHandler {
	/**
	 * 处理竞彩字符串
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2017年9月8日 下午2:31:05
	 * @param content
	 *            //1611011[+1](3,0)|1611012[+1](0)|1611013(0)
	 * @param childCode
	 *            子玩法
	 * @return
	 */
	public static String doJcContent(String content, int childCode, ISportSymbol sportSymbol) {
		StringBuffer sb = new StringBuffer();
		char[] bytes = content.toCharArray();
		StringBuffer temp = new StringBuffer();
		char play = '0';
		boolean isAdd = true;
		for (char b : bytes) {
			switch (b) {
			// 过滤掉不用的信息
			case '[':
				isAdd = false;
				break;
			case ']':// 赛事编号
				isAdd = true;
				break;
			case '(':// 赛事编号
				sb.append(sportSymbol.leftBracket(sb.length(),temp.toString(),play));
				temp.setLength(0);
				temp.append(childCode);
				temp.append("_");
				isAdd = true;
				break;
			case ',':
				sb.append(sportSymbol.comma(temp.toString(),play));
				temp.setLength(0);
				temp.append(childCode);
				temp.append("_");
				isAdd = true;
				break;
			case ')':
				sb.append(sportSymbol.rightBracket(temp.toString(),play));
				temp.setLength(0);
				break;
			case '|':
				temp.setLength(0);
				isAdd = true;
				break;
			case '@':
				isAdd = false;
				break;
			case '_':
				isAdd = false;
				break;
			case 'S':
			case 'R':
			case 'Q':
			case 'Z':
			case 'B':
			case 'C':
			case 'D':
				play = b;
				break;
			default:
				if (isAdd) {
					temp.append(b);
				}
				break;
			}
		}
		return sb.toString();
	}
}
