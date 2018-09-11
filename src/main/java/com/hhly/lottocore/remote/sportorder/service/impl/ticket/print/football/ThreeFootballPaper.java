package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.football;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;
/**
 * @desc 3关
 * 单关的平其它 打印纸 体彩机器识别不了
 * @author jiangwei
 * @date 2018年7月17日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service
public class ThreeFootballPaper extends AbstractFootballPaper {
	
	public static Map<String, String> SPORT_THREE = new HashMap<>();
	
	static{

		// 单关
		SPORT_THREE.put("play_1_1", "5_4");
		// 串关
		SPORT_THREE.put("play_2_1", "5_6");
		SPORT_THREE.put("play_3_1", "5_6");
		// 混合过关
		SPORT_THREE.put("play_2_1_hh", "5_9");
		SPORT_THREE.put("play_3_1_hh", "5_9");
		// 星期
		SPORT_THREE.put("week_1_1", "9_1");
		SPORT_THREE.put("week_2_1", "9_2");
		SPORT_THREE.put("week_3_1", "9_3");
		SPORT_THREE.put("week_4_1", "9_4");
		SPORT_THREE.put("week_5_1", "11_1");
		SPORT_THREE.put("week_6_1", "11_2");
		SPORT_THREE.put("week_7_1", "11_3");

		SPORT_THREE.put("week_1_2", "9_5");
		SPORT_THREE.put("week_2_2", "9_6");
		SPORT_THREE.put("week_3_2", "9_7");
		SPORT_THREE.put("week_4_2", "9_8");
		SPORT_THREE.put("week_5_2", "11_5");
		SPORT_THREE.put("week_6_2", "11_6");
		SPORT_THREE.put("week_7_2", "11_7");

		SPORT_THREE.put("week_1_3", "9_9");
		SPORT_THREE.put("week_2_3", "9_10");
		SPORT_THREE.put("week_3_3", "9_11");
		SPORT_THREE.put("week_4_3", "9_12");
		SPORT_THREE.put("week_5_3", "11_9");
		SPORT_THREE.put("week_6_3", "11_10");
		SPORT_THREE.put("week_7_3", "11_11");

		// 赛事编号
		SPORT_THREE.put("number_1_1", "13_1");
		SPORT_THREE.put("number_2_1", "13_2");
		SPORT_THREE.put("number_4_1", "13_3");
		SPORT_THREE.put("number_5_1", "13_4");
		SPORT_THREE.put("number_10_1", "15_1");
		SPORT_THREE.put("number_20_1", "15_2");
		SPORT_THREE.put("number_40_1", "15_3");
		SPORT_THREE.put("number_50_1", "15_4");
		SPORT_THREE.put("number_100_1", "17_1");
		SPORT_THREE.put("number_200_1", "17_2");
		SPORT_THREE.put("number_400_1", "17_3");
		SPORT_THREE.put("number_500_1", "17_4");

		SPORT_THREE.put("number_1_2", "13_5");
		SPORT_THREE.put("number_2_2", "13_6");
		SPORT_THREE.put("number_4_2", "13_7");
		SPORT_THREE.put("number_5_2", "13_8");
		SPORT_THREE.put("number_10_2", "15_5");
		SPORT_THREE.put("number_20_2", "15_6");
		SPORT_THREE.put("number_40_2", "15_7");
		SPORT_THREE.put("number_50_2", "15_8");
		SPORT_THREE.put("number_100_2", "17_5");
		SPORT_THREE.put("number_200_2", "17_6");
		SPORT_THREE.put("number_400_2", "17_7");
		SPORT_THREE.put("number_500_2", "17_8");

		SPORT_THREE.put("number_1_3", "13_9");
		SPORT_THREE.put("number_2_3", "13_10");
		SPORT_THREE.put("number_4_3", "13_11");
		SPORT_THREE.put("number_5_3", "13_12");
		SPORT_THREE.put("number_10_3", "15_9");
		SPORT_THREE.put("number_20_3", "15_10");
		SPORT_THREE.put("number_40_3", "15_11");
		SPORT_THREE.put("number_50_3", "15_12");
		SPORT_THREE.put("number_100_3", "17_9");
		SPORT_THREE.put("number_200_3", "17_10");
		SPORT_THREE.put("number_400_3", "17_11");
		SPORT_THREE.put("number_500_3", "17_12");

		// 胜平负 S
		SPORT_THREE.put("S_3_1", "21_1");
		SPORT_THREE.put("S_1_1", "21_2");
		SPORT_THREE.put("S_0_1", "21_3");

		SPORT_THREE.put("S_3_2", "21_5");
		SPORT_THREE.put("S_1_2", "21_6");
		SPORT_THREE.put("S_0_2", "21_7");

		SPORT_THREE.put("S_3_3", "21_9");
		SPORT_THREE.put("S_1_3", "21_10");
		SPORT_THREE.put("S_0_3", "21_11");
		// 让球胜平负：R
		SPORT_THREE.put("R_3_1", "23_1");
		SPORT_THREE.put("R_1_1", "23_2");
		SPORT_THREE.put("R_0_1", "23_3");

		SPORT_THREE.put("R_3_2", "23_5");
		SPORT_THREE.put("R_1_2", "23_6");
		SPORT_THREE.put("R_0_2", "23_7");

		SPORT_THREE.put("R_3_3", "23_9");
		SPORT_THREE.put("R_1_3", "23_10");
		SPORT_THREE.put("R_0_3", "23_11");

		// 全场比分：Q
		SPORT_THREE.put("Q_10_1", "25_1");
		SPORT_THREE.put("Q_20_1", "27_1");
		SPORT_THREE.put("Q_21_1", "29_1");
		SPORT_THREE.put("Q_30_1", "31_1");
		SPORT_THREE.put("Q_31_1", "33_1");
		SPORT_THREE.put("Q_32_1", "35_1");
		SPORT_THREE.put("Q_40_1", "37_1");
		SPORT_THREE.put("Q_41_1", "39_1");

		SPORT_THREE.put("Q_42_1", "25_2");
		SPORT_THREE.put("Q_50_1", "27_2");
		SPORT_THREE.put("Q_51_1", "29_2");
		SPORT_THREE.put("Q_52_1", "31_2");
		SPORT_THREE.put("Q_90_1", "33_2");
		SPORT_THREE.put("Q_00_1", "35_2");
		SPORT_THREE.put("Q_11_1", "37_2");
		SPORT_THREE.put("Q_22_1", "39_2");

		SPORT_THREE.put("Q_24_1", "25_3");
		SPORT_THREE.put("Q_05_1", "27_3");
		SPORT_THREE.put("Q_15_1", "29_3");
		SPORT_THREE.put("Q_25_1", "31_3");
		SPORT_THREE.put("Q_09_1", "33_3");
		SPORT_THREE.put("Q_33_1", "35_3");
		SPORT_THREE.put("Q_99_1", "37_3");

		SPORT_THREE.put("Q_01_1", "25_4");
		SPORT_THREE.put("Q_02_1", "27_4");
		SPORT_THREE.put("Q_12_1", "29_4");
		SPORT_THREE.put("Q_03_1", "31_4");
		SPORT_THREE.put("Q_13_1", "33_4");
		SPORT_THREE.put("Q_23_1", "35_4");
		SPORT_THREE.put("Q_04_1", "37_4");
		SPORT_THREE.put("Q_14_1", "39_4");

		SPORT_THREE.put("Q_10_2", "25_5");
		SPORT_THREE.put("Q_20_2", "27_5");
		SPORT_THREE.put("Q_21_2", "29_5");
		SPORT_THREE.put("Q_30_2", "31_5");
		SPORT_THREE.put("Q_31_2", "33_5");
		SPORT_THREE.put("Q_32_2", "35_5");
		SPORT_THREE.put("Q_40_2", "37_5");
		SPORT_THREE.put("Q_41_2", "39_5");

		SPORT_THREE.put("Q_42_2", "25_6");
		SPORT_THREE.put("Q_50_2", "27_6");
		SPORT_THREE.put("Q_51_2", "29_6");
		SPORT_THREE.put("Q_52_2", "31_6");
		SPORT_THREE.put("Q_90_2", "33_6");
		SPORT_THREE.put("Q_00_2", "35_6");
		SPORT_THREE.put("Q_11_2", "37_6");
		SPORT_THREE.put("Q_22_2", "39_6");

		SPORT_THREE.put("Q_24_2", "25_7");
		SPORT_THREE.put("Q_05_2", "27_7");
		SPORT_THREE.put("Q_15_2", "29_7");
		SPORT_THREE.put("Q_25_2", "31_7");
		SPORT_THREE.put("Q_09_2", "33_7");
		SPORT_THREE.put("Q_33_2", "35_7");
		SPORT_THREE.put("Q_99_2", "37_7");

		SPORT_THREE.put("Q_01_2", "25_8");
		SPORT_THREE.put("Q_02_2", "27_8");
		SPORT_THREE.put("Q_12_2", "29_8");
		SPORT_THREE.put("Q_03_2", "31_8");
		SPORT_THREE.put("Q_13_2", "33_8");
		SPORT_THREE.put("Q_23_2", "35_8");
		SPORT_THREE.put("Q_04_2", "37_8");
		SPORT_THREE.put("Q_14_2", "39_8");

		SPORT_THREE.put("Q_10_3", "25_9");
		SPORT_THREE.put("Q_20_3", "27_9");
		SPORT_THREE.put("Q_21_3", "29_9");
		SPORT_THREE.put("Q_30_3", "31_9");
		SPORT_THREE.put("Q_31_3", "33_9");
		SPORT_THREE.put("Q_32_3", "35_9");
		SPORT_THREE.put("Q_40_3", "37_9");
		SPORT_THREE.put("Q_41_3", "39_9");

		SPORT_THREE.put("Q_42_3", "25_10");
		SPORT_THREE.put("Q_50_3", "27_10");
		SPORT_THREE.put("Q_51_3", "29_10");
		SPORT_THREE.put("Q_52_3", "31_10");
		SPORT_THREE.put("Q_90_3", "33_10");
		SPORT_THREE.put("Q_00_3", "35_10");
		SPORT_THREE.put("Q_11_3", "37_10");
		SPORT_THREE.put("Q_22_3", "39_10");

		SPORT_THREE.put("Q_24_3", "25_11");
		SPORT_THREE.put("Q_05_3", "27_11");
		SPORT_THREE.put("Q_15_3", "29_11");
		SPORT_THREE.put("Q_25_3", "31_11");
		SPORT_THREE.put("Q_09_3", "33_11");
		SPORT_THREE.put("Q_33_3", "35_11");
		SPORT_THREE.put("Q_99_3", "37_11");

		SPORT_THREE.put("Q_01_3", "25_12");
		SPORT_THREE.put("Q_02_3", "27_12");
		SPORT_THREE.put("Q_12_3", "29_12");
		SPORT_THREE.put("Q_03_3", "31_12");
		SPORT_THREE.put("Q_13_3", "33_12");
		SPORT_THREE.put("Q_23_3", "35_12");
		SPORT_THREE.put("Q_04_3", "37_12");
		SPORT_THREE.put("Q_14_3", "39_12");
		// 半全场比分
		SPORT_THREE.put("B_33_1", "41_1");
		SPORT_THREE.put("B_31_1", "41_2");
		SPORT_THREE.put("B_30_1", "41_3");

		SPORT_THREE.put("B_33_2", "41_5");
		SPORT_THREE.put("B_31_2", "41_6");
		SPORT_THREE.put("B_30_2", "41_7");

		SPORT_THREE.put("B_33_3", "41_9");
		SPORT_THREE.put("B_31_3", "41_10");
		SPORT_THREE.put("B_30_3", "41_11");

		SPORT_THREE.put("B_13_1", "43_1");
		SPORT_THREE.put("B_11_1", "43_2");
		SPORT_THREE.put("B_10_1", "43_3");

		SPORT_THREE.put("B_13_2", "43_5");
		SPORT_THREE.put("B_11_2", "43_6");
		SPORT_THREE.put("B_10_2", "43_7");

		SPORT_THREE.put("B_13_3", "43_9");
		SPORT_THREE.put("B_11_3", "43_10");
		SPORT_THREE.put("B_10_3", "43_11");

		SPORT_THREE.put("B_03_1", "45_1");
		SPORT_THREE.put("B_01_1", "45_2");
		SPORT_THREE.put("B_00_1", "45_3");

		SPORT_THREE.put("B_03_2", "45_5");
		SPORT_THREE.put("B_01_2", "45_6");
		SPORT_THREE.put("B_00_2", "45_7");

		SPORT_THREE.put("B_03_3", "45_9");
		SPORT_THREE.put("B_01_3", "45_10");
		SPORT_THREE.put("B_00_3", "45_11");
		// 总进球
		SPORT_THREE.put("Z_0_1", "47_1");
		SPORT_THREE.put("Z_1_1", "47_2");
		SPORT_THREE.put("Z_2_1", "47_3");
		SPORT_THREE.put("Z_3_1", "47_4");

		SPORT_THREE.put("Z_0_2", "47_5");
		SPORT_THREE.put("Z_1_2", "47_6");
		SPORT_THREE.put("Z_2_2", "47_7");
		SPORT_THREE.put("Z_3_2", "47_8");

		SPORT_THREE.put("Z_0_3", "47_9");
		SPORT_THREE.put("Z_1_3", "47_10");
		SPORT_THREE.put("Z_2_3", "47_11");
		SPORT_THREE.put("Z_3_3", "47_12");

		SPORT_THREE.put("Z_4_1", "49_1");
		SPORT_THREE.put("Z_5_1", "49_2");
		SPORT_THREE.put("Z_6_1", "49_3");
		SPORT_THREE.put("Z_7_1", "49_4");

		SPORT_THREE.put("Z_4_2", "49_5");
		SPORT_THREE.put("Z_5_2", "49_6");
		SPORT_THREE.put("Z_6_2", "49_7");
		SPORT_THREE.put("Z_7_2", "49_8");

		SPORT_THREE.put("Z_4_3", "49_9");
		SPORT_THREE.put("Z_5_3", "49_10");
		SPORT_THREE.put("Z_6_3", "49_11");
		SPORT_THREE.put("Z_7_3", "49_12");
		// 过关方式
		SPORT_THREE.put("bunch_2_1", "53_1");
		SPORT_THREE.put("bunch_3_1", "53_2");

		SPORT_THREE.put("multiple_1", "53_9");
		SPORT_THREE.put("multiple_2", "53_10");
		SPORT_THREE.put("multiple_3", "53_11");
		SPORT_THREE.put("multiple_4", "53_12");

		SPORT_THREE.put("multiple_5", "55_9");
		SPORT_THREE.put("multiple_6", "55_10");
		SPORT_THREE.put("multiple_7", "55_11");
		SPORT_THREE.put("multiple_8", "55_12");

		SPORT_THREE.put("multiple_9", "57_9");
		SPORT_THREE.put("multiple_10", "57_10");
		SPORT_THREE.put("multiple_20", "57_11");
		SPORT_THREE.put("multiple_30", "57_12");

		SPORT_THREE.put("multiple_40", "59_9");
		SPORT_THREE.put("multiple_50", "59_10");
	
	}
	@Override
	public String header(Ticket ticket) {
		return "0_0,1_1,1_3,1_5,1_7,1_9,1_10,1_11,1_12,2_0,3_4,4_0,6_0,8_0,10_0,12_0,14_0,16_0,18_0,20_0,22_0,24_0,26_0,28_0,30_0,32_0,34_0,36_0,38_0,40_0,42_0,44_0,46_0,48_0,50_0,52_0,54_0,56_0,58_0,60_0,61_13";
	}

	@Override
	protected Map<String, String> getCoordinateMap() {
		return SPORT_THREE;
	}

}
