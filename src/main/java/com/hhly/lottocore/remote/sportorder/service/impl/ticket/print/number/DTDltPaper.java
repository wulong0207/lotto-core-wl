package com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.number;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.hhly.lottocore.remote.sportorder.service.impl.ticket.print.Ticket;

/**
 * @desc 大乐透胆拖投注
 * @author jiangwei
 * @date 2018年7月25日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service
public class DTDltPaper extends AbstractNumberPaper {

	private int[] multipleNum = { 9, 8, 7, 6, 5, 4, 3, 2, 1 };

	public static Map<String, String> COORDINATE = new HashMap<>();

	private static final String HEADER = "0_0,2_0,4_0,6_0,8_0,10_0,12_0,14_0,16_0,18_0,20_0,22_0,24_0,26_0,28_0,30_0,32_0,1_1,1_2,1_3,1_7,1_12,33_13";

	static {
		//前区胆码
		COORDINATE.put("b_d_01", "5_13");
		COORDINATE.put("b_d_06", "5_12");
		COORDINATE.put("b_d_11", "5_11");
		COORDINATE.put("b_d_16", "5_10");
		COORDINATE.put("b_d_21", "5_9");
		COORDINATE.put("b_d_26", "5_8");
		COORDINATE.put("b_d_31", "5_7");
		//后区胆码
		COORDINATE.put("a_d_01", "5_4");
		COORDINATE.put("a_d_06", "5_3");
		COORDINATE.put("a_d_11", "5_2");
		
		
		COORDINATE.put("b_d_02", "7_13");
		COORDINATE.put("b_d_07", "7_12");
		COORDINATE.put("b_d_12", "7_11");
		COORDINATE.put("b_d_17", "7_10");
		COORDINATE.put("b_d_22", "7_9");
		COORDINATE.put("b_d_27", "7_8");
		COORDINATE.put("b_d_32", "7_7");
		                          
		COORDINATE.put("a_d_02", "7_4");
		COORDINATE.put("a_d_07", "7_3");
		COORDINATE.put("a_d_12", "7_2");
		
		COORDINATE.put("b_d_03", "9_13");
		COORDINATE.put("b_d_08", "9_12");
		COORDINATE.put("b_d_13", "9_11");
		COORDINATE.put("b_d_18", "9_10");
		COORDINATE.put("b_d_23", "9_9");
		COORDINATE.put("b_d_28", "9_8");
		COORDINATE.put("b_d_33", "9_7");
		
		COORDINATE.put("a_d_03", "9_4");
		COORDINATE.put("a_d_08", "9_3");

		COORDINATE.put("b_d_04", "11_13");
		COORDINATE.put("b_d_09", "11_12");
		COORDINATE.put("b_d_14", "11_11");
		COORDINATE.put("b_d_19", "11_10");
		COORDINATE.put("b_d_24", "11_9");
		COORDINATE.put("b_d_29", "11_8");
		COORDINATE.put("b_d_34", "11_7");
		                          
		COORDINATE.put("a_d_04", "11_4");
		COORDINATE.put("a_d_09", "11_3");		
		
		COORDINATE.put("b_d_05", "13_13");
		COORDINATE.put("b_d_10", "13_12");
		COORDINATE.put("b_d_15", "13_11");
		COORDINATE.put("b_d_20", "13_10");
		COORDINATE.put("b_d_25", "13_9");
		COORDINATE.put("b_d_30", "13_8");
		COORDINATE.put("b_d_35", "13_7");
		                           
		COORDINATE.put("a_d_05", "13_4");
		COORDINATE.put("a_d_10", "13_3");
		//前区拖码
		COORDINATE.put("b_t_01", "15_13");
		COORDINATE.put("b_t_06", "15_12");
		COORDINATE.put("b_t_11", "15_11");
		COORDINATE.put("b_t_16", "15_10");
		COORDINATE.put("b_t_21", "15_9");
		COORDINATE.put("b_t_26", "15_8");
		COORDINATE.put("b_t_31", "15_7");
		//后区拖码                       
		COORDINATE.put("a_t_01", "15_4");
		COORDINATE.put("a_t_06", "15_3");
		COORDINATE.put("a_t_11", "15_2");
		                  
		                  
		COORDINATE.put("b_t_02", "17_13");
		COORDINATE.put("b_t_07", "17_12");
		COORDINATE.put("b_t_12", "17_11");
		COORDINATE.put("b_t_17", "17_10");
		COORDINATE.put("b_t_22", "17_9");
		COORDINATE.put("b_t_27", "17_8");
		COORDINATE.put("b_t_32", "17_7");
		                          
		COORDINATE.put("a_t_02", "17_4");
		COORDINATE.put("a_t_07", "17_3");
		COORDINATE.put("a_t_12", "17_2");
		                  
		COORDINATE.put("b_t_03", "19_13");
		COORDINATE.put("b_t_08", "19_12");
		COORDINATE.put("b_t_13", "19_11");
		COORDINATE.put("b_t_18", "19_10");
		COORDINATE.put("b_t_23", "19_9");
		COORDINATE.put("b_t_28", "19_8");
		COORDINATE.put("b_t_33", "19_7");
		                          
		COORDINATE.put("a_t_03", "19_4");
		COORDINATE.put("a_t_08", "19_3");
                          
		COORDINATE.put("b_t_04", "21_13");
		COORDINATE.put("b_t_09", "21_12");
		COORDINATE.put("b_t_14", "21_11");
		COORDINATE.put("b_t_19", "21_10");
		COORDINATE.put("b_t_24", "21_9");
		COORDINATE.put("b_t_29", "21_8");
		COORDINATE.put("b_t_34", "21_7");
		                          
		COORDINATE.put("a_t_04", "21_4");
		COORDINATE.put("a_t_09", "21_3");		
		                  
		COORDINATE.put("b_t_05", "23_13");
		COORDINATE.put("b_t_10", "23_12");
		COORDINATE.put("b_t_15", "23_11");
		COORDINATE.put("b_t_20", "23_10");
		COORDINATE.put("b_t_25", "23_9");
		COORDINATE.put("b_t_30", "23_8");
		COORDINATE.put("b_t_35", "23_7");
		                          
		COORDINATE.put("a_t_05", "23_4");
		COORDINATE.put("a_t_10", "23_3");
		
		//倍数
		COORDINATE.put("multiple_10", "25_12");
		COORDINATE.put("multiple_20", "25_11");
		COORDINATE.put("multiple_30", "25_10");
		COORDINATE.put("multiple_40", "25_9");
		COORDINATE.put("multiple_50", "25_8");
		COORDINATE.put("multiple_60", "25_7");
		COORDINATE.put("multiple_70", "25_6");
		COORDINATE.put("multiple_80", "25_5");
		COORDINATE.put("multiple_90", "25_4");
		
		COORDINATE.put("multiple_1", "27_12");
		COORDINATE.put("multiple_2", "27_11");
		COORDINATE.put("multiple_3", "27_10");
		COORDINATE.put("multiple_4", "27_9");
		COORDINATE.put("multiple_5", "27_8");
		COORDINATE.put("multiple_6", "27_7");
		COORDINATE.put("multiple_7", "27_6");
		COORDINATE.put("multiple_8", "27_5");
		COORDINATE.put("multiple_9", "27_4");
		COORDINATE.put("multiple_0", "27_3");
		
		COORDINATE.put("many_1", "31_12");
	}

	@Override
	public String header(Ticket ticket) {
		return HEADER;
	}

	@Override
	public String play(Ticket ticket) {
		return "";
	}

	@Override
	public String content(Ticket ticket) {
		StringBuilder sb = new StringBuilder(",");
		sb.append(COORDINATE.get("many_1"));
		String[] ba = ticket.getContent().split("\\+");
		String[] before = ba[0].split("#");
		String[] after = ba[1].split("#");
		if (before.length == 2) {
			sb.append(analysis("b_d_", before[0]));
			sb.append(analysis("b_t_", before[1]));
		} else {
			sb.append(analysis("b_t_", before[0]));
		}
		if (after.length == 2) {
			sb.append(analysis("a_d_", after[0]));
			sb.append(analysis("a_t_", after[1]));
		} else {
			sb.append(analysis("a_t_", after[0]));
		}
		return sb.toString();
	}

	private StringBuilder analysis(String head, String content) {
		StringBuilder sb = new StringBuilder();
		for (String str : content.split(",")) {
			sb.append(",");
			sb.append(COORDINATE.get(head + str));
		}
		return sb;
	}

	@Override
	protected Map<String, String> getCoordinateMap() {
		return COORDINATE;
	}

	@Override
	protected int[] getMultipleNum() {
		return multipleNum;
	}

}
