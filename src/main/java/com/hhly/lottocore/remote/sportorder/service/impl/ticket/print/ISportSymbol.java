package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print;

/**
 * @desc 竞彩格式转换符号处理
 * @author jiangwei
 * @date 2017年9月7日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface ISportSymbol {
	/**
	 * 左括号
	 * @author jiangwei
	 * @Version 1.0
	 * @CreatDate 2017年9月8日 上午10:07:16
	 * @param contentLength 当前类容长度
	 * @param match 场次编号
	 * @param play 混合投注玩法
	 * @return
	 */
	StringBuffer leftBracket(int contentLength,String match,char play);
	  /**
	   * 逗号
	   * @author jiangwei
	   * @Version 1.0
	   * @CreatDate 2017年9月8日 上午9:40:57
	   * @param temp 子玩法+"_"+选号类容：30604_12
	   * @param play 混合投注玩法
	   */
	StringBuffer comma(String temp,char play);
	  /**
	   * 右括号
	   * @author jiangwei
	   * @Version 1.0
	   * @CreatDate 2017年9月8日 上午9:41:07
	   * @param temp 子玩法+"_"+选号类容：30604_12
	   * @param play 混合投注玩法
	   */
	StringBuffer rightBracket(String temp,char play);
	  
}
