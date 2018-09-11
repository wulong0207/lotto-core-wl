package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.football;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;
/**
 * @desc 4-6关
 * 4关包含半全场，比分，格式不能转换为打印纸
 * @author jiangwei
 * @date 2018年7月17日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service
public class SixFootBallPaper extends AbstractFootballPaper {
	
	public static Map<String, String> SPORT_SIX = new HashMap<>();
	static{
		// 单关
		SPORT_SIX.put("play_1_1", "5_4");
		// 串关
		SPORT_SIX.put("play_4_1", "5_6");
		SPORT_SIX.put("play_5_1", "5_6");
		SPORT_SIX.put("play_6_1", "5_6");
		// 混合过关
		SPORT_SIX.put("play_4_1_hh", "5_9");
		SPORT_SIX.put("play_5_1_hh", "5_9");
		SPORT_SIX.put("play_6_1_hh", "5_9");
		// 星期
		SPORT_SIX.put("week_1_1", "9_1");
		SPORT_SIX.put("week_2_1", "9_2");
		SPORT_SIX.put("week_3_1", "9_3");
		SPORT_SIX.put("week_4_1", "9_4");
		SPORT_SIX.put("week_5_1", "11_1");
		SPORT_SIX.put("week_6_1", "11_2");
		SPORT_SIX.put("week_7_1", "11_3");

		SPORT_SIX.put("week_1_2", "9_5");
		SPORT_SIX.put("week_2_2", "9_6");
		SPORT_SIX.put("week_3_2", "9_7");
		SPORT_SIX.put("week_4_2", "9_8");
		SPORT_SIX.put("week_5_2", "11_5");
		SPORT_SIX.put("week_6_2", "11_6");
		SPORT_SIX.put("week_7_2", "11_7");

		SPORT_SIX.put("week_1_3", "9_9");
		SPORT_SIX.put("week_2_3", "9_10");
		SPORT_SIX.put("week_3_3", "9_11");
		SPORT_SIX.put("week_4_3", "9_12");
		SPORT_SIX.put("week_5_3", "11_9");
		SPORT_SIX.put("week_6_3", "11_10");
		SPORT_SIX.put("week_7_3", "11_11");
		
		SPORT_SIX.put("week_1_4", "31_1");
		SPORT_SIX.put("week_2_4", "31_2");
		SPORT_SIX.put("week_3_4", "31_3");
		SPORT_SIX.put("week_4_4", "31_4");
		SPORT_SIX.put("week_5_4", "33_1");
		SPORT_SIX.put("week_6_4", "33_2");
		SPORT_SIX.put("week_7_4", "33_3");

		SPORT_SIX.put("week_1_5", "31_5");
		SPORT_SIX.put("week_2_5", "31_6");
		SPORT_SIX.put("week_3_5", "31_7");
		SPORT_SIX.put("week_4_5", "31_8");
		SPORT_SIX.put("week_5_5", "33_5");
		SPORT_SIX.put("week_6_5", "33_6");
		SPORT_SIX.put("week_7_5", "33_7");

		SPORT_SIX.put("week_1_6", "31_9");
		SPORT_SIX.put("week_2_6", "31_10");
		SPORT_SIX.put("week_3_6", "31_11");
		SPORT_SIX.put("week_4_6", "31_12");
		SPORT_SIX.put("week_5_6", "33_9");
		SPORT_SIX.put("week_6_6", "33_10");
		SPORT_SIX.put("week_7_6", "33_11");
		
		// 赛事编号
		SPORT_SIX.put("number_1_1", "13_1");
		SPORT_SIX.put("number_2_1", "13_2");
		SPORT_SIX.put("number_4_1", "13_3");
		SPORT_SIX.put("number_5_1", "13_4");
		SPORT_SIX.put("number_10_1", "15_1");
		SPORT_SIX.put("number_20_1", "15_2");
		SPORT_SIX.put("number_40_1", "15_3");
		SPORT_SIX.put("number_50_1", "15_4");
		SPORT_SIX.put("number_100_1", "17_1");
		SPORT_SIX.put("number_200_1", "17_2");
		SPORT_SIX.put("number_400_1", "17_3");
		SPORT_SIX.put("number_500_1", "17_4");

		SPORT_SIX.put("number_1_2", "13_5");
		SPORT_SIX.put("number_2_2", "13_6");
		SPORT_SIX.put("number_4_2", "13_7");
		SPORT_SIX.put("number_5_2", "13_8");
		SPORT_SIX.put("number_10_2", "15_5");
		SPORT_SIX.put("number_20_2", "15_6");
		SPORT_SIX.put("number_40_2", "15_7");
		SPORT_SIX.put("number_50_2", "15_8");
		SPORT_SIX.put("number_100_2", "17_5");
		SPORT_SIX.put("number_200_2", "17_6");
		SPORT_SIX.put("number_400_2", "17_7");
		SPORT_SIX.put("number_500_2", "17_8");

		SPORT_SIX.put("number_1_3", "13_9");
		SPORT_SIX.put("number_2_3", "13_10");
		SPORT_SIX.put("number_4_3", "13_11");
		SPORT_SIX.put("number_5_3", "13_12");
		SPORT_SIX.put("number_10_3", "15_9");
		SPORT_SIX.put("number_20_3", "15_10");
		SPORT_SIX.put("number_40_3", "15_11");
		SPORT_SIX.put("number_50_3", "15_12");
		SPORT_SIX.put("number_100_3", "17_9");
		SPORT_SIX.put("number_200_3", "17_10");
		SPORT_SIX.put("number_400_3", "17_11");
		SPORT_SIX.put("number_500_3", "17_12");
		
		SPORT_SIX.put("number_1_4", "35_1");
		SPORT_SIX.put("number_2_4", "35_2");
		SPORT_SIX.put("number_4_4", "35_3");
		SPORT_SIX.put("number_5_4", "35_4");
		SPORT_SIX.put("number_10_4", "37_1");
		SPORT_SIX.put("number_20_4", "37_2");
		SPORT_SIX.put("number_40_4", "37_3");
		SPORT_SIX.put("number_50_4", "37_4");
		SPORT_SIX.put("number_100_4", "39_1");
		SPORT_SIX.put("number_200_4", "39_2");
		SPORT_SIX.put("number_400_4", "39_3");
		SPORT_SIX.put("number_500_4", "39_4");

		SPORT_SIX.put("number_1_5", "35_5");
		SPORT_SIX.put("number_2_5", "35_6");
		SPORT_SIX.put("number_4_5", "35_7");
		SPORT_SIX.put("number_5_5", "35_8");
		SPORT_SIX.put("number_10_5", "37_5");
		SPORT_SIX.put("number_20_5", "37_6");
		SPORT_SIX.put("number_40_5", "37_7");
		SPORT_SIX.put("number_50_5", "37_8");
		SPORT_SIX.put("number_100_5", "39_5");
		SPORT_SIX.put("number_200_5", "39_6");
		SPORT_SIX.put("number_400_5", "39_7");
		SPORT_SIX.put("number_500_5", "39_8");

		SPORT_SIX.put("number_1_6", "35_9");
		SPORT_SIX.put("number_2_6", "35_10");
		SPORT_SIX.put("number_4_6", "35_11");
		SPORT_SIX.put("number_5_6", "35_12");
		SPORT_SIX.put("number_10_6", "37_9");
		SPORT_SIX.put("number_20_6", "37_10");
		SPORT_SIX.put("number_40_6", "37_11");
		SPORT_SIX.put("number_50_6", "37_12");
		SPORT_SIX.put("number_100_6", "39_9");
		SPORT_SIX.put("number_200_6", "39_10");
		SPORT_SIX.put("number_400_6", "39_11");
		SPORT_SIX.put("number_500_6", "39_12");
		
		//胜平负
		SPORT_SIX.put("S_3_1", "21_1");
		SPORT_SIX.put("S_1_1", "21_2");
		SPORT_SIX.put("S_0_1", "21_3");

		SPORT_SIX.put("S_3_2", "21_5");
		SPORT_SIX.put("S_1_2", "21_6");
		SPORT_SIX.put("S_0_2", "21_7");

		SPORT_SIX.put("S_3_3", "21_9");
		SPORT_SIX.put("S_1_3", "21_10");
		SPORT_SIX.put("S_0_3", "21_11");
		
		SPORT_SIX.put("S_3_4", "43_1");
		SPORT_SIX.put("S_1_4", "43_2");
		SPORT_SIX.put("S_0_4", "43_3");

		SPORT_SIX.put("S_3_5", "43_5");
		SPORT_SIX.put("S_1_5", "43_6");
		SPORT_SIX.put("S_0_5", "43_7");

		SPORT_SIX.put("S_3_6", "43_9");
		SPORT_SIX.put("S_1_6", "43_10");
		SPORT_SIX.put("S_0_6", "43_11");
		//让球胜平负
		SPORT_SIX.put("R_3_1", "23_1");
		SPORT_SIX.put("R_1_1", "23_2");
		SPORT_SIX.put("R_0_1", "23_3");

		SPORT_SIX.put("R_3_2", "23_5");
		SPORT_SIX.put("R_1_2", "23_6");
		SPORT_SIX.put("R_0_2", "23_7");

		SPORT_SIX.put("R_3_3", "23_9");
		SPORT_SIX.put("R_1_3", "23_10");
		SPORT_SIX.put("R_0_3", "23_11");
		
		SPORT_SIX.put("R_3_4", "45_1");
		SPORT_SIX.put("R_1_4", "45_2");
		SPORT_SIX.put("R_0_4", "45_3");

		SPORT_SIX.put("R_3_5", "45_5");
		SPORT_SIX.put("R_1_5", "45_6");
		SPORT_SIX.put("R_0_5", "45_7");

		SPORT_SIX.put("R_3_6", "45_9");
		SPORT_SIX.put("R_1_6", "45_10");
		SPORT_SIX.put("R_0_6", "45_11");
		//总进球
		SPORT_SIX.put("Z_0_1", "25_1");
		SPORT_SIX.put("Z_1_1", "25_2");
		SPORT_SIX.put("Z_2_1", "25_3");
		SPORT_SIX.put("Z_3_1", "25_4");

		SPORT_SIX.put("Z_0_2", "25_5");
		SPORT_SIX.put("Z_1_2", "25_6");
		SPORT_SIX.put("Z_2_2", "25_7");
		SPORT_SIX.put("Z_3_2", "25_8");

		SPORT_SIX.put("Z_0_3", "25_9");
		SPORT_SIX.put("Z_1_3", "25_10");
		SPORT_SIX.put("Z_2_3", "25_11");
		SPORT_SIX.put("Z_3_3", "25_12");

		SPORT_SIX.put("Z_4_1", "27_1");
		SPORT_SIX.put("Z_5_1", "27_2");
		SPORT_SIX.put("Z_6_1", "27_3");
		SPORT_SIX.put("Z_7_1", "27_4");

		SPORT_SIX.put("Z_4_2", "27_5");
		SPORT_SIX.put("Z_5_2", "27_6");
		SPORT_SIX.put("Z_6_2", "27_7");
		SPORT_SIX.put("Z_7_2", "27_8");

		SPORT_SIX.put("Z_4_3", "27_9");
		SPORT_SIX.put("Z_5_3", "27_10");
		SPORT_SIX.put("Z_6_3", "27_11");
		SPORT_SIX.put("Z_7_3", "27_12");
		
		SPORT_SIX.put("Z_0_4", "47_1");
		SPORT_SIX.put("Z_1_4", "47_2");
		SPORT_SIX.put("Z_2_4", "47_3");
		SPORT_SIX.put("Z_3_4", "47_4");

		SPORT_SIX.put("Z_0_5", "47_5");
		SPORT_SIX.put("Z_1_5", "47_6");
		SPORT_SIX.put("Z_2_5", "47_7");
		SPORT_SIX.put("Z_3_5", "47_8");

		SPORT_SIX.put("Z_0_6", "47_9");
		SPORT_SIX.put("Z_1_6", "47_10");
		SPORT_SIX.put("Z_2_6", "47_11");
		SPORT_SIX.put("Z_3_6", "47_12");

		SPORT_SIX.put("Z_4_4", "49_1");
		SPORT_SIX.put("Z_5_4", "49_2");
		SPORT_SIX.put("Z_6_4", "49_3");
		SPORT_SIX.put("Z_7_4", "49_4");

		SPORT_SIX.put("Z_4_5", "49_5");
		SPORT_SIX.put("Z_5_5", "49_6");
		SPORT_SIX.put("Z_6_5", "49_7");
		SPORT_SIX.put("Z_7_5", "49_8");

		SPORT_SIX.put("Z_4_6", "49_9");
		SPORT_SIX.put("Z_5_6", "49_10");
		SPORT_SIX.put("Z_6_6", "49_11");
		SPORT_SIX.put("Z_7_6", "49_12");
		
		// 过关方式
		SPORT_SIX.put("bunch_4_1", "53_1");
		SPORT_SIX.put("bunch_5_1", "55_1");
		SPORT_SIX.put("bunch_6_1", "57_1");
		
		//倍数
		SPORT_SIX.put("multiple_1", "53_9");
		SPORT_SIX.put("multiple_2", "53_10");
		SPORT_SIX.put("multiple_3", "53_11");
		SPORT_SIX.put("multiple_4", "53_12");

		SPORT_SIX.put("multiple_5", "55_9");
		SPORT_SIX.put("multiple_6", "55_10");
		SPORT_SIX.put("multiple_7", "55_11");
		SPORT_SIX.put("multiple_8", "55_12");

		SPORT_SIX.put("multiple_9", "57_9");
		SPORT_SIX.put("multiple_10", "57_10");
		SPORT_SIX.put("multiple_20", "57_11");
		SPORT_SIX.put("multiple_30", "57_12");

		SPORT_SIX.put("multiple_40", "59_9");
		SPORT_SIX.put("multiple_50", "59_10");
		
	}
	
	
	@Override
	public boolean check(Ticket ticket) {
		//打印纸不支持4关比分，半全场
		if(ticket.getChildType().indexOf("4_") != -1){
			if(ticket.getChildCode()==30004
					||ticket.getChildCode()==30006){
				return false;
			}else if(ticket.getChildCode()==30001){
				if(ticket.getContent().indexOf("_B") != -1
						||ticket.getContent().indexOf("_Q") != -1){
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String header(Ticket ticket) {
		return "0_0,2_0,4_0,6_0,8_0,10_0,12_0,14_0,16_0,18_0,20_0,22_0,24_0,26_0,28_0,30_0,32_0,34_0,36_0,38_0,40_0,42_0,44_0,46_0,48_0,50_0,52_0,54_0,56_0,58_0,60_0,1_1,1_3,1_5,1_7,1_9,1_10,1_11,1_12,3_1,3_4,61_13";
	}

	@Override
	protected Map<String, String> getCoordinateMap() {
		return SPORT_SIX;
	}

}
