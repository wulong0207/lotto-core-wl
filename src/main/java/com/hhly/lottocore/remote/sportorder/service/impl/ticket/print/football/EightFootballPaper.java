package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.football;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;
/**
 * @desc 7-8关
 * @author jiangwei
 * @date 2018年7月17日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service
public class EightFootballPaper extends AbstractFootballPaper {
	
	public static Map<String, String> SPORT_EIGHT = new HashMap<>();
	
	static{
		// 单关
		SPORT_EIGHT.put("play_1_1", "5_4");
		// 串关
		SPORT_EIGHT.put("play_7_1", "5_6");
		SPORT_EIGHT.put("play_8_1", "5_6");
		// 混合过关
		SPORT_EIGHT.put("play_7_1_hh", "5_9");
		SPORT_EIGHT.put("play_8_1_hh", "5_9");

		SPORT_EIGHT.put("week_1_1", "9_1");
		SPORT_EIGHT.put("week_2_1", "9_2");
		SPORT_EIGHT.put("week_3_1", "9_3");
		SPORT_EIGHT.put("week_4_1", "9_4");
		SPORT_EIGHT.put("week_5_1", "11_1");
		SPORT_EIGHT.put("week_6_1", "11_2");
		SPORT_EIGHT.put("week_7_1", "11_3");

		SPORT_EIGHT.put("week_1_2", "9_5");
		SPORT_EIGHT.put("week_2_2", "9_6");
		SPORT_EIGHT.put("week_3_2", "9_7");
		SPORT_EIGHT.put("week_4_2", "9_8");
		SPORT_EIGHT.put("week_5_2", "11_5");
		SPORT_EIGHT.put("week_6_2", "11_6");
		SPORT_EIGHT.put("week_7_2", "11_7");

		SPORT_EIGHT.put("week_1_3", "9_9");
		SPORT_EIGHT.put("week_2_3", "9_10");
		SPORT_EIGHT.put("week_3_3", "9_11");
		SPORT_EIGHT.put("week_4_3", "9_12");
		SPORT_EIGHT.put("week_5_3", "11_9");
		SPORT_EIGHT.put("week_6_3", "11_10");
		SPORT_EIGHT.put("week_7_3", "11_11");
		
		SPORT_EIGHT.put("week_1_4", "27_1");
		SPORT_EIGHT.put("week_2_4", "27_2");
		SPORT_EIGHT.put("week_3_4", "27_3");
		SPORT_EIGHT.put("week_4_4", "27_4");
		SPORT_EIGHT.put("week_5_4", "29_1");
		SPORT_EIGHT.put("week_6_4", "29_2");
		SPORT_EIGHT.put("week_7_4", "29_3");

		SPORT_EIGHT.put("week_1_5", "27_5");
		SPORT_EIGHT.put("week_2_5", "27_6");
		SPORT_EIGHT.put("week_3_5", "27_7");
		SPORT_EIGHT.put("week_4_5", "27_8");
		SPORT_EIGHT.put("week_5_5", "29_5");
		SPORT_EIGHT.put("week_6_5", "29_6");
		SPORT_EIGHT.put("week_7_5", "29_7");

		SPORT_EIGHT.put("week_1_6", "27_9");
		SPORT_EIGHT.put("week_2_6", "27_10");
		SPORT_EIGHT.put("week_3_6", "27_11");
		SPORT_EIGHT.put("week_4_6", "27_12");
		SPORT_EIGHT.put("week_5_6", "29_9");
		SPORT_EIGHT.put("week_6_6", "29_10");
		SPORT_EIGHT.put("week_7_6", "29_11");
		
		SPORT_EIGHT.put("week_1_7", "45_1");
		SPORT_EIGHT.put("week_2_7", "45_2");
		SPORT_EIGHT.put("week_3_7", "45_3");
		SPORT_EIGHT.put("week_4_7", "45_4");
		SPORT_EIGHT.put("week_5_7", "47_1");
		SPORT_EIGHT.put("week_6_7", "47_2");
		SPORT_EIGHT.put("week_7_7", "47_3");

		SPORT_EIGHT.put("week_1_8", "45_5");
		SPORT_EIGHT.put("week_2_8", "45_6");
		SPORT_EIGHT.put("week_3_8", "45_7");
		SPORT_EIGHT.put("week_4_8", "45_8");
		SPORT_EIGHT.put("week_5_8", "47_5");
		SPORT_EIGHT.put("week_6_8", "47_6");
		SPORT_EIGHT.put("week_7_8", "47_7");

		// 赛事编号
		SPORT_EIGHT.put("number_1_1", "13_1");
		SPORT_EIGHT.put("number_2_1", "13_2");
		SPORT_EIGHT.put("number_4_1", "13_3");
		SPORT_EIGHT.put("number_5_1", "13_4");
		SPORT_EIGHT.put("number_10_1", "15_1");
		SPORT_EIGHT.put("number_20_1", "15_2");
		SPORT_EIGHT.put("number_40_1", "15_3");
		SPORT_EIGHT.put("number_50_1", "15_4");
		SPORT_EIGHT.put("number_100_1", "17_1");
		SPORT_EIGHT.put("number_200_1", "17_2");
		SPORT_EIGHT.put("number_400_1", "17_3");
		SPORT_EIGHT.put("number_500_1", "17_4");

		SPORT_EIGHT.put("number_1_2", "13_5");
		SPORT_EIGHT.put("number_2_2", "13_6");
		SPORT_EIGHT.put("number_4_2", "13_7");
		SPORT_EIGHT.put("number_5_2", "13_8");
		SPORT_EIGHT.put("number_10_2", "15_5");
		SPORT_EIGHT.put("number_20_2", "15_6");
		SPORT_EIGHT.put("number_40_2", "15_7");
		SPORT_EIGHT.put("number_50_2", "15_8");
		SPORT_EIGHT.put("number_100_2", "17_5");
		SPORT_EIGHT.put("number_200_2", "17_6");
		SPORT_EIGHT.put("number_400_2", "17_7");
		SPORT_EIGHT.put("number_500_2", "17_8");

		SPORT_EIGHT.put("number_1_3", "13_9");
		SPORT_EIGHT.put("number_2_3", "13_10");
		SPORT_EIGHT.put("number_4_3", "13_11");
		SPORT_EIGHT.put("number_5_3", "13_12");
		SPORT_EIGHT.put("number_10_3", "15_9");
		SPORT_EIGHT.put("number_20_3", "15_10");
		SPORT_EIGHT.put("number_40_3", "15_11");
		SPORT_EIGHT.put("number_50_3", "15_12");
		SPORT_EIGHT.put("number_100_3", "17_9");
		SPORT_EIGHT.put("number_200_3", "17_10");
		SPORT_EIGHT.put("number_400_3", "17_11");
		SPORT_EIGHT.put("number_500_3", "17_12");
		
		SPORT_EIGHT.put("number_1_4", "31_1");
		SPORT_EIGHT.put("number_2_4", "31_2");
		SPORT_EIGHT.put("number_4_4", "31_3");
		SPORT_EIGHT.put("number_5_4", "31_4");
		SPORT_EIGHT.put("number_10_4", "33_1");
		SPORT_EIGHT.put("number_20_4", "33_2");
		SPORT_EIGHT.put("number_40_4", "33_3");
		SPORT_EIGHT.put("number_50_4", "33_4");
		SPORT_EIGHT.put("number_100_4", "35_1");
		SPORT_EIGHT.put("number_200_4", "35_2");
		SPORT_EIGHT.put("number_400_4", "35_3");
		SPORT_EIGHT.put("number_500_4", "35_4");

		SPORT_EIGHT.put("number_1_5", "31_5");
		SPORT_EIGHT.put("number_2_5", "31_6");
		SPORT_EIGHT.put("number_4_5", "31_7");
		SPORT_EIGHT.put("number_5_5", "31_8");
		SPORT_EIGHT.put("number_10_5", "33_5");
		SPORT_EIGHT.put("number_20_5", "33_6");
		SPORT_EIGHT.put("number_40_5", "33_7");
		SPORT_EIGHT.put("number_50_5", "33_8");
		SPORT_EIGHT.put("number_100_5", "35_5");
		SPORT_EIGHT.put("number_200_5", "35_6");
		SPORT_EIGHT.put("number_400_5", "35_7");
		SPORT_EIGHT.put("number_500_5", "35_8");

		SPORT_EIGHT.put("number_1_6", "31_9");
		SPORT_EIGHT.put("number_2_6", "31_10");
		SPORT_EIGHT.put("number_4_6", "31_11");
		SPORT_EIGHT.put("number_5_6", "31_12");
		SPORT_EIGHT.put("number_10_6", "33_9");
		SPORT_EIGHT.put("number_20_6", "33_10");
		SPORT_EIGHT.put("number_40_6", "33_11");
		SPORT_EIGHT.put("number_50_6", "33_12");
		SPORT_EIGHT.put("number_100_6", "35_9");
		SPORT_EIGHT.put("number_200_6", "35_10");
		SPORT_EIGHT.put("number_400_6", "35_11");
		SPORT_EIGHT.put("number_500_6", "35_12");
		
		SPORT_EIGHT.put("number_1_7", "49_1");
		SPORT_EIGHT.put("number_2_7", "49_2");
		SPORT_EIGHT.put("number_4_7", "49_3");
		SPORT_EIGHT.put("number_5_7", "49_4");
		SPORT_EIGHT.put("number_10_7", "51_1");
		SPORT_EIGHT.put("number_20_7", "51_2");
		SPORT_EIGHT.put("number_40_7", "51_3");
		SPORT_EIGHT.put("number_50_7", "51_4");
		SPORT_EIGHT.put("number_100_7", "53_1");
		SPORT_EIGHT.put("number_200_7", "53_2");
		SPORT_EIGHT.put("number_400_7", "53_3");
		SPORT_EIGHT.put("number_500_7", "53_4");

		SPORT_EIGHT.put("number_1_8", "49_5");
		SPORT_EIGHT.put("number_2_8", "49_6");
		SPORT_EIGHT.put("number_4_8", "49_7");
		SPORT_EIGHT.put("number_5_8", "49_8");
		SPORT_EIGHT.put("number_10_8", "51_5");
		SPORT_EIGHT.put("number_20_8", "51_6");
		SPORT_EIGHT.put("number_40_8", "51_7");
		SPORT_EIGHT.put("number_50_8", "51_8");
		SPORT_EIGHT.put("number_100_8", "53_5");
		SPORT_EIGHT.put("number_200_8", "53_6");
		SPORT_EIGHT.put("number_400_8", "53_7");
		SPORT_EIGHT.put("number_500_8", "53_8");

		// 胜平负 S
		SPORT_EIGHT.put("S_3_1", "21_1");
		SPORT_EIGHT.put("S_1_1", "21_2");
		SPORT_EIGHT.put("S_0_1", "21_3");

		SPORT_EIGHT.put("S_3_2", "21_5");
		SPORT_EIGHT.put("S_1_2", "21_6");
		SPORT_EIGHT.put("S_0_2", "21_7");

		SPORT_EIGHT.put("S_3_3", "21_9");
		SPORT_EIGHT.put("S_1_3", "21_10");
		SPORT_EIGHT.put("S_0_3", "21_11");
		
		SPORT_EIGHT.put("S_3_4", "39_1");
		SPORT_EIGHT.put("S_1_4", "39_2");
		SPORT_EIGHT.put("S_0_4", "39_3");

		SPORT_EIGHT.put("S_3_5", "39_5");
		SPORT_EIGHT.put("S_1_5", "39_6");
		SPORT_EIGHT.put("S_0_5", "39_7");

		SPORT_EIGHT.put("S_3_6", "39_9");
		SPORT_EIGHT.put("S_1_6", "39_10");
		SPORT_EIGHT.put("S_0_6", "39_11");
		
		SPORT_EIGHT.put("S_3_7", "57_1");
		SPORT_EIGHT.put("S_1_7", "57_2");
		SPORT_EIGHT.put("S_0_7", "57_3");

		SPORT_EIGHT.put("S_3_8", "57_5");
		SPORT_EIGHT.put("S_1_8", "57_6");
		SPORT_EIGHT.put("S_0_8", "57_7");

		
		// 让球胜平负：R
		SPORT_EIGHT.put("R_3_1", "23_1");
		SPORT_EIGHT.put("R_1_1", "23_2");
		SPORT_EIGHT.put("R_0_1", "23_3");

		SPORT_EIGHT.put("R_3_2", "23_5");
		SPORT_EIGHT.put("R_1_2", "23_6");
		SPORT_EIGHT.put("R_0_2", "23_7");

		SPORT_EIGHT.put("R_3_3", "23_9");
		SPORT_EIGHT.put("R_1_3", "23_10");
		SPORT_EIGHT.put("R_0_3", "23_11");
		
		SPORT_EIGHT.put("R_3_4", "41_1");
		SPORT_EIGHT.put("R_1_4", "41_2");
		SPORT_EIGHT.put("R_0_4", "41_3");

		SPORT_EIGHT.put("R_3_5", "41_5");
		SPORT_EIGHT.put("R_1_5", "41_6");
		SPORT_EIGHT.put("R_0_5", "41_7");

		SPORT_EIGHT.put("R_3_6", "41_9");
		SPORT_EIGHT.put("R_1_6", "41_10");
		SPORT_EIGHT.put("R_0_6", "41_11");
		
		SPORT_EIGHT.put("R_3_7", "59_1");
		SPORT_EIGHT.put("R_1_7", "59_2");
		SPORT_EIGHT.put("R_0_7", "59_3");

		SPORT_EIGHT.put("R_3_8", "59_5");
		SPORT_EIGHT.put("R_1_8", "59_6");
		SPORT_EIGHT.put("R_0_8", "59_7");
		
		// 过关方式
		SPORT_EIGHT.put("bunch_7_1", "63_1");
		SPORT_EIGHT.put("bunch_8_1", "65_1");
		
		//倍数
		SPORT_EIGHT.put("multiple_1", "63_9");
		SPORT_EIGHT.put("multiple_2", "63_10");
		SPORT_EIGHT.put("multiple_3", "63_11");
		SPORT_EIGHT.put("multiple_4", "63_12");

		SPORT_EIGHT.put("multiple_5", "65_9");
		SPORT_EIGHT.put("multiple_6", "65_10");
		SPORT_EIGHT.put("multiple_7", "65_11");
		SPORT_EIGHT.put("multiple_8", "65_12");

		SPORT_EIGHT.put("multiple_9", "67_9");
		SPORT_EIGHT.put("multiple_10", "67_10");
		SPORT_EIGHT.put("multiple_20", "67_11");
		SPORT_EIGHT.put("multiple_30", "67_12");

		SPORT_EIGHT.put("multiple_40", "69_9");
		SPORT_EIGHT.put("multiple_50", "69_10");
	}
	
	@Override
	public String header(Ticket ticket) {
		return "0_0,2_0,4_0,6_0,8_0,10_0,12_0,14_0,16_0,18_0,20_0,22_0,24_0,26_0,28_0,30_0,32_0,34_0,36_0,38_0,40_0,42_0,44_0,46_0,48_0,50_0,52_0,54_0,56_0,58_0,60_0,62_0,64_0,66_0,68_0,70_0,1_1,1_3,1_5,1_7,1_8,1_9,1_13,3_2,3_4,71_13";
	}

	@Override
	protected Map<String, String> getCoordinateMap() {
		return SPORT_EIGHT;
	}

}
