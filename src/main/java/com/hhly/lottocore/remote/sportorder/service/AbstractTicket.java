package com.hhly.lottocore.remote.sportorder.service;

import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.JCLQConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.util.ObjectUtil;

/**
 * 抽象票明细相关方法
 * @author longguoyou
 * @date 2017年11月4日
 * @compay 益彩网络科技有限公司
 */
public abstract class AbstractTicket {
	/**
	 * 判断是否标红
	 * @author longguoyou
	 * @date 2017年11月3日
	 * @param bet
	 * @param caiguo
	 * @return
	 */
	protected Integer judeFlag(String bet, String caiguo) {
		return bet.equals(caiguo)?Constants.NUM_1:Constants.NUM_0;
	}

	/**
	 * 竞篮：<br>
	 *  大小分：157.5|99,157.5|99<br>
	 *  让分：//-4.5|0,-5.5|0
	 * @param lotteryCode
	 * @param bet
	 * @param caiguo
	 * @return
	 */
	protected Integer judeFlagLq(Integer lotteryCode, String bet, String caiguo, String panKou, String initContent){
		if(!ObjectUtil.isBlank(caiguo)){
			if(lotteryCode == JCLQConstants.ID_JCLQ_DXF || lotteryCode == JCLQConstants.ID_JCLQ_RF ||
					(lotteryCode == JCLQConstants.ID_JCLQ_HHGG && initContent.contains("R")) ||
					(lotteryCode == JCLQConstants.ID_JCLQ_HHGG && initContent.contains("D"))){
				String[] panKouAndCaiguos = caiguo.split(SymbolConstants.COMMA);//[165.55|99,155.5|99,....]
				for(String panKouAndCaiguo : panKouAndCaiguos){
					String[] bets = panKouAndCaiguo.split(SymbolConstants.DOUBLE_SLASH_VERTICAL_BAR);
					if(!ObjectUtil.isBlank(bets) && bets.length == Constants.NUM_2){
						String panKouWithoutSymbol = getPankouWithoutSymbol(panKou);
						//盘口一致并且投注一致， 使用“||”,防止出现两种情况：1、盘口：156.50和投注：156.5 ；2、盘口：156.5和投注：156.50
						if((panKouWithoutSymbol.indexOf(bets[0]) > -1 || bets[0].indexOf(panKouWithoutSymbol) > -1) && bets[1].equals(bet)){
							return Constants.NUM_1;
						}
					}
				}
				return Constants.NUM_0;
			}else{
				return judeFlag(bet,caiguo);
			}
		}
        return Constants.NUM_0;
	}

	/**
	 * 获取盘口，没有前后符号，如[112.3] , 取112.3
	 * @param panKou
	 * @return
	 */
	private String getPankouWithoutSymbol(String panKou){
		if(!ObjectUtil.isBlank(panKou)){
			return panKou.substring(panKou.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT)+Constants.NUM_1, panKou.indexOf(SymbolConstants.MIDDLE_PARENTHESES_RIGHT));
		}
		return null;
	}
	
	/**
	 * 获取系统编号:<br>1)1711024024_S(3@1.33)<br> 2) 1711024024[-1](1@3.35) <br>3)1711024023(3@1.17)
	 * @author longguoyou
	 * @date 2017年11月3日
	 * @param betContent
	 * @return
	 */
	protected static String getSystemCode(String betContent){
		if(ObjectUtil.isBlank(betContent)){
			return null;
		}
		if(betContent.contains(SymbolConstants.UNDERLINE)){
			return betContent.substring(0, betContent.indexOf(SymbolConstants.UNDERLINE));
		}
		if(betContent.contains(SymbolConstants.MIDDLE_PARENTHESES_LEFT)){
			return betContent.substring(0, betContent.indexOf(SymbolConstants.MIDDLE_PARENTHESES_LEFT));
		}
		if(betContent.contains(SymbolConstants.PARENTHESES_LEFT)){
			return betContent.substring(0, betContent.indexOf(SymbolConstants.PARENTHESES_LEFT));
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(getSystemCode("1711024024_S(3@1.33)"));
		System.out.println(getSystemCode("1711024024[-1](1@3.35)"));
		System.out.println(getSystemCode("1711024023(3@1.17)"));
	}
}
